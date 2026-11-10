package com.example.sisgesc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sisgesc.DTOs.AlunoPerfil;
import com.example.sisgesc.Repository.AlunoRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class perfil_aluno extends AppCompatActivity {

    // Lado maior da foto enviada. Fotos de celular são grandes e não precisam disso no perfil.
    private static final int LADO_MAXIMO_FOTO = 1024;

    private AlunoRepository repository;
    private String cpf;
    private ImageView imgPerfil;

    // Abre a galeria para o aluno escolher a foto
    private final ActivityResultLauncher<String> escolherFoto =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::enviarFoto);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_aluno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAluno.configurar(this, R.id.navPerfil);

        cpf = SessaoAluno.cpfOuVoltarAoLogin(this);
        if (cpf == null) {
            return;
        }
        repository = new AlunoRepository();
        imgPerfil = findViewById(R.id.imgPerfilAluno);

        imgPerfil.setOnClickListener(v -> escolherFoto.launch("image/*"));
        findViewById(R.id.btnEditarDados).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarDadosAluno.class);
            intent.putExtra(MainActivity.EXTRA_CPF, cpf);
            startActivity(intent);
        });
        findViewById(R.id.btnAlterarSenha).setOnClickListener(v -> mostrarDialogoSenha());
        findViewById(R.id.btnSairPerfil).setOnClickListener(v -> SessaoAluno.sair(this));
    }

    // Recarrega ao voltar da tela Editar dados, para o e-mail e o telefone já aparecerem atualizados
    @Override
    protected void onResume() {
        super.onResume();
        if (repository != null) {
            carregarPerfil();
        }
    }

    private void carregarPerfil() {
        repository.buscarPerfil(cpf, new RepositoryCallback<AlunoPerfil>() {
            @Override
            public void onSucesso(AlunoPerfil perfil) {
                if (SessaoAluno.telaFechada(perfil_aluno.this)) return;
                mostrarPerfil(perfil);
            }

            @Override
            public void onErro(String mensagem) {
                if (SessaoAluno.telaFechada(perfil_aluno.this)) return;
                Toast.makeText(perfil_aluno.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarPerfil(AlunoPerfil perfil) {
        texto(R.id.txtNomePerfil, perfil.getNomeCompleto(), "-");
        texto(R.id.txtEmailPerfil, perfil.getEmail(), "Não informado");
        texto(R.id.txtTelefonePerfil, perfil.getTelefone(), "Não informado");
        texto(R.id.txtResponsavelPerfil, perfil.getNomeResponsavel(), "Não informado");
        texto(R.id.txtTelefoneResponsavel, perfil.getTelefoneResponsavel(), "Não informado");

        mostrarFoto(perfil.getFotoUrl());
    }

    private void mostrarFoto(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        Glide.with(this).load(url).circleCrop().placeholder(R.drawable.bg_foto_aluno).into(imgPerfil);
    }

    private void texto(int idDoCampo, String valor, String alternativa) {
        ((TextView) findViewById(idDoCampo)).setText(valor == null || valor.isEmpty() ? alternativa : valor);
    }

    // ===== Foto de perfil =====

    private void enviarFoto(Uri imagem) {
        if (imagem == null) {
            return; // o aluno fechou a galeria sem escolher
        }
        byte[] bytes = prepararFoto(imagem);
        if (bytes == null) {
            Toast.makeText(this, "Não foi possível ler essa imagem.", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Enviando foto...", Toast.LENGTH_SHORT).show();
        repository.atualizarFoto(cpf, bytes, "image/jpeg", new RepositoryCallback<String>() {
            @Override
            public void onSucesso(String url) {
                if (SessaoAluno.telaFechada(perfil_aluno.this)) return;
                mostrarFoto(url);
                Toast.makeText(perfil_aluno.this, "Foto atualizada!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(String mensagem) {
                if (SessaoAluno.telaFechada(perfil_aluno.this)) return;
                Toast.makeText(perfil_aluno.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Lê a imagem escolhida, reduz se for grande e devolve em JPEG. Devolve null se não conseguir.
    private byte[] prepararFoto(Uri imagem) {
        try (InputStream entrada = getContentResolver().openInputStream(imagem)) {
            Bitmap original = BitmapFactory.decodeStream(entrada);
            if (original == null) {
                return null;
            }
            int maior = Math.max(original.getWidth(), original.getHeight());
            Bitmap redimensionada = original;
            if (maior > LADO_MAXIMO_FOTO) {
                float escala = (float) LADO_MAXIMO_FOTO / maior;
                redimensionada = Bitmap.createScaledBitmap(original,
                        Math.round(original.getWidth() * escala),
                        Math.round(original.getHeight() * escala), true);
            }
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            redimensionada.compress(Bitmap.CompressFormat.JPEG, 85, saida);
            return saida.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    // ===== Alterar senha =====

    private void mostrarDialogoSenha() {

        View conteudo = getLayoutInflater().inflate(R.layout.dialog_alterar_senha, null);
        EditText edtAtual = conteudo.findViewById(R.id.edtSenhaAtual);
        EditText edtNova = conteudo.findViewById(R.id.edtNovaSenha);
        EditText edtConfirmar = conteudo.findViewById(R.id.edtConfirmarSenha);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Alterar senha")
                .setView(conteudo)
                .setPositiveButton("Salvar", null)
                .setNegativeButton("Cancelar", null)
                 .create();

        dialogo.show();



        // O botão é configurado depois do show() para o diálogo não fechar quando há erro de preenchimento
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String atual = edtAtual.getText().toString();
            String nova = edtNova.getText().toString();
            String confirmar = edtConfirmar.getText().toString();

            if (atual.isEmpty()) {
                edtAtual.setError("Digite a senha atual");
            } else if (nova.trim().isEmpty()) {
                edtNova.setError("Digite a nova senha");
            } else if (!nova.equals(confirmar)) {
                edtConfirmar.setError("As senhas não são iguais");
            } else {
                v.setEnabled(false);
                repository.alterarSenha(cpf, atual, nova, new RepositoryCallback<Void>() {
                    @Override
                    public void onSucesso(Void resultado) {
                        if (SessaoAluno.telaFechada(perfil_aluno.this)) return;
                        dialogo.dismiss();
                        Toast.makeText(perfil_aluno.this, "Senha alterada!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onErro(String mensagem) {
                        if (SessaoAluno.telaFechada(perfil_aluno.this)) return;
                        v.setEnabled(true);
                        Toast.makeText(perfil_aluno.this, mensagem, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

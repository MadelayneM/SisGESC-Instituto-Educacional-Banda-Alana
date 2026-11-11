package com.example.sisgesc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sisgesc.DTOs.AdminPerfil;
import com.example.sisgesc.Repository.AdminRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

public class activity_admin_perfil extends AppCompatActivity {

    private final AdminRepository repo = new AdminRepository();
    private ImageView imgPerfilAdmin;
    private AdminPerfil perfilAtual;
    private String cpfAdmin; // Variável global para guardar o CPF

    private final ActivityResultLauncher<PickVisualMediaRequest> selectorImagem =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri == null) return; // usuário cancelou

                Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(imgPerfilAdmin);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAdmin.configurar(this, R.id.navPerfil);

        // 1. Inicializar corretamente o ImageView da foto
        imgPerfilAdmin = findViewById(R.id.imgPerfilAdmin);

        // 2. Capturar o CPF da Intent e guardar na variável global
        cpfAdmin = getIntent().getStringExtra(MainActivity.EXTRA_CPF);

        // Botão "Editar dados"
        Button btnEditarDados = findViewById(R.id.btnEditarDados);
        btnEditarDados.setOnClickListener(v -> abrirDialogEditarDados());

        // Botão "Alterar senha"
        Button btnAlterarSenha = findViewById(R.id.btnAlterarSenha);
        btnAlterarSenha.setOnClickListener(v -> abrirDialogAlterarSenha());

        // Clique na foto de perfil para atualizar a imagem
        imgPerfilAdmin.setOnClickListener(v -> abrirSeletorDeImagens());

        // Sair
        findViewById(R.id.btnSairPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        carregarPerfil();
    }

    private void carregarPerfil() {
        if (cpfAdmin == null) {
            Toast.makeText(this, "CPF do administrador não encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        repo.buscarPerfil(cpfAdmin, new RepositoryCallback<AdminPerfil>() {
            @Override
            public void onSucesso(AdminPerfil perfil) {
                perfilAtual = perfil;
                mostrarPerfil(perfil);

                // Carregar Foto com Glide
                if (perfil.getFotoUrl() != null && !perfil.getFotoUrl().isEmpty()) {
                    Glide.with(activity_admin_perfil.this)
                            .load(perfil.getFotoUrl())
                            .circleCrop()
                            .into(imgPerfilAdmin);
                }
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_perfil.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void abrirSeletorDeImagens() {
        selectorImagem.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void abrirDialogAlterarSenha() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_alterar_senha_admin, null);

        EditText edtSenhaAtual = dialogView.findViewById(R.id.edtSenhaAtual);
        EditText edtNovaSenha = dialogView.findViewById(R.id.edtNovaSenha);
        EditText edtConfirmarSenha = dialogView.findViewById(R.id.edtConfirmarSenha);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alterar Senha")
                .setView(dialogView)
                .setPositiveButton("Atualizar", (dialog, which) -> {
                    String senhaAtual = edtSenhaAtual.getText().toString().trim();
                    String novaSenha = edtNovaSenha.getText().toString().trim();
                    String confirmarSenha = edtConfirmarSenha.getText().toString().trim();

                    if (!novaSenha.equals(confirmarSenha)) {
                        Toast.makeText(this, "A nova senha e a confirmação não conferem.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    repo.alterarSenha(cpfAdmin, senhaAtual, novaSenha, new RepositoryCallback<Void>() {
                        @Override
                        public void onSucesso(Void res) {
                            Toast.makeText(activity_admin_perfil.this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onErro(String msg) {
                            Toast.makeText(activity_admin_perfil.this, "Erro ao alterar senha: " + msg, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void abrirDialogEditarDados() {
        if (perfilAtual == null) return;

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_dados_admin, null);

        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtDdd = dialogView.findViewById(R.id.edtDdd);
        EditText edtNumeroTelefone = dialogView.findViewById(R.id.edtNumeroTelefone);
        Spinner spnTipoTelefone = dialogView.findViewById(R.id.spnTipoTelefone);

        edtEmail.setText(perfilAtual.getEmail());
        edtDdd.setText(perfilAtual.getDdd());
        edtNumeroTelefone.setText(perfilAtual.getNumeroTelefone());

        FormularioAdmin.preencherSpinner(spnTipoTelefone, AdminPerfil.TIPOS_TELEFONE);
        FormularioAdmin.selecionar(spnTipoTelefone, perfilAtual.getTipoTelefone());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Dados do Perfil")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String novoEmail = edtEmail.getText().toString().trim();
                    String novoDdd = edtDdd.getText().toString().trim();
                    String novoNumero = edtNumeroTelefone.getText().toString().trim();
                    String tipoEscolhido = spnTipoTelefone.getSelectedItem() != null ? spnTipoTelefone.getSelectedItem().toString() : null;

                    final int[] operacoesConcluidas = {0};

                    Runnable verificarFim = () -> {
                        operacoesConcluidas[0]++;
                        if (operacoesConcluidas[0] == 2) {
                            Toast.makeText(activity_admin_perfil.this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarPerfil(); // Recarrega os dados atualizados na tela
                        }
                    };

                    // 1. Atualizar E-mail
                    repo.atualizarEmail(cpfAdmin, novoEmail, new RepositoryCallback<Void>() {
                        @Override
                        public void onSucesso(Void res) {
                            verificarFim.run();
                        }
                        @Override
                        public void onErro(String msg) {
                            Toast.makeText(activity_admin_perfil.this, "Erro ao atualizar e-mail: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // 2. Atualizar Telefone
                    repo.atualizarTelefone(cpfAdmin, perfilAtual.getDdd(), perfilAtual.getNumeroTelefone(),
                            novoDdd, novoNumero, tipoEscolhido, new RepositoryCallback<Void>() {
                                @Override
                                public void onSucesso(Void res) {
                                    verificarFim.run();
                                }
                                @Override
                                public void onErro(String msg) {
                                    Toast.makeText(activity_admin_perfil.this, "Erro ao atualizar telefone: " + msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void mostrarPerfil(AdminPerfil perfil) {
        ((TextView) findViewById(R.id.txtNomePerfil)).setText(perfil.getNomeCompleto());
        ((TextView) findViewById(R.id.txtTipoPerfil)).setText(perfil.getCargo());
        ((TextView) findViewById(R.id.txtEmailPerfil)).setText(
                perfil.getEmail() == null ? "Não cadastrado" : perfil.getEmail());
        String telefone = perfil.getTelefoneFormatado();
        ((TextView) findViewById(R.id.txtTelefonePerfil)).setText(telefone == null ? "Não cadastrado" : telefone);
    }
}
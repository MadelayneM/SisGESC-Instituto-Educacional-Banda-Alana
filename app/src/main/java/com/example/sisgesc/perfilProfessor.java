package com.example.sisgesc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sisgesc.DTOs.ProfessorPerfil;
import com.example.sisgesc.Repository.ProfessorRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

public class perfilProfessor extends AppCompatActivity {

    private String cpf;
    private ProfessorRepository repo;
    private ProfessorPerfil perfilAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_professor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuProfessor.configurar(this, R.id.navPerfil);

        cpf = getIntent().getStringExtra(MainActivity.EXTRA_CPF);
        repo = new ProfessorRepository();

        carregarPerfil();

        findViewById(R.id.btnSairPerfil).setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        findViewById(R.id.btnEditarDados).setOnClickListener(v -> abrirDialogEditarDados());
        findViewById(R.id.btnAlterarSenha).setOnClickListener(v -> abrirDialogAlterarSenha());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cpf != null) {
            carregarPerfil();
        }
    }

    private void carregarPerfil() {
        repo.buscarPerfil(cpf, new RepositoryCallback<ProfessorPerfil>() {
            @Override
            public void onSucesso(ProfessorPerfil perfil) {
                perfilAtual = perfil;
                atualizarTela(perfil);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(perfilProfessor.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atualizarTela(ProfessorPerfil perfil) {
        ImageView imgPerfil = findViewById(R.id.imgPerfilProfessor);
        TextView txtNome = findViewById(R.id.txtNomePerfil);
        TextView txtTipo = findViewById(R.id.txtTipoPerfil);
        TextView txtEmail = findViewById(R.id.txtEmailPerfil);
        TextView txtTelefone = findViewById(R.id.txtTelefonePerfil);

        txtNome.setText(perfil.getNomeCompleto());

        String funcoesStr = (perfil.getFuncoes() != null && !perfil.getFuncoes().isEmpty())
                ? TextUtils.join(", ", perfil.getFuncoes())
                : "";
        String tipoStr = perfil.getCargo() + (funcoesStr.isEmpty() ? "" : " • " + funcoesStr);
        txtTipo.setText(tipoStr);

        txtEmail.setText(perfil.getEmail() != null ? perfil.getEmail() : "Não informado");
        txtTelefone.setText(perfil.getTelefoneFormatado() != null ? perfil.getTelefoneFormatado() : "Não informado");

        if (perfil.getFotoUrl() != null && !perfil.getFotoUrl().isEmpty()) {
            Glide.with(this).load(perfil.getFotoUrl()).circleCrop().into(imgPerfil);
        }
    }

    private void abrirDialogEditarDados() {
        View janela = getLayoutInflater().inflate(R.layout.dialog_editar_dados_professor, null);
        EditText edtEmail = janela.findViewById(R.id.edtEmail);
        EditText edtDdd = janela.findViewById(R.id.edtDdd);
        EditText edtNumero = janela.findViewById(R.id.edtNumeroTelefone);
        Spinner spnTipo = janela.findViewById(R.id.spnTipoTelefone);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ProfessorPerfil.TIPOS_TELEFONE);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipo.setAdapter(adapter);

        if (perfilAtual != null) {
            if (perfilAtual.getEmail() != null) edtEmail.setText(perfilAtual.getEmail());
            if (perfilAtual.getDdd() != null) edtDdd.setText(perfilAtual.getDdd());
            if (perfilAtual.getNumeroTelefone() != null) edtNumero.setText(perfilAtual.getNumeroTelefone());
        }

        new AlertDialog.Builder(this)
                .setTitle("Editar dados")
                .setView(janela)
                .setPositiveButton("Salvar", (d, w) -> {
                    String novoEmail = edtEmail.getText().toString().trim();
                    String novoDdd = edtDdd.getText().toString().trim();
                    String novoNumero = edtNumero.getText().toString().trim();
                    String tipoEscolhido = (String) spnTipo.getSelectedItem();

                    repo.atualizarEmail(cpf, novoEmail, new RepositoryCallback<Void>() {
                        @Override
                        public void onSucesso(Void unused) {
                            repo.atualizarTelefone(cpf,
                                    perfilAtual != null ? perfilAtual.getDdd() : null,
                                    perfilAtual != null ? perfilAtual.getNumeroTelefone() : null,
                                    novoDdd, novoNumero, tipoEscolhido, new RepositoryCallback<Void>() {
                                        @Override
                                        public void onSucesso(Void unused) {
                                            Toast.makeText(perfilProfessor.this, "Dados salvos!", Toast.LENGTH_SHORT).show();
                                            carregarPerfil();
                                        }

                                        @Override
                                        public void onErro(String mensagem) {
                                            Toast.makeText(perfilProfessor.this, mensagem, Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                        @Override
                        public void onErro(String mensagem) {
                            Toast.makeText(perfilProfessor.this, mensagem, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirDialogAlterarSenha() {
        View janela = getLayoutInflater().inflate(R.layout.dialog_alterar_senha_professor, null);
        EditText edtSenhaAtual = janela.findViewById(R.id.edtSenhaAtual);
        EditText edtNovaSenha = janela.findViewById(R.id.edtNovaSenha);
        EditText edtConfirmar = janela.findViewById(R.id.edtConfirmarSenha);

        new AlertDialog.Builder(this)
                .setTitle("Alterar senha")
                .setView(janela)
                .setPositiveButton("Salvar", (d, w) -> {
                    String atual = edtSenhaAtual.getText().toString();
                    String nova = edtNovaSenha.getText().toString();
                    String confirmar = edtConfirmar.getText().toString();

                    if (!nova.equals(confirmar)) {
                        Toast.makeText(this, "A nova senha e a confirmação não conferem.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    repo.alterarSenha(cpf, atual, nova, new RepositoryCallback<Void>() {
                        @Override
                        public void onSucesso(Void unused) {
                            Toast.makeText(perfilProfessor.this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onErro(String mensagem) {
                            Toast.makeText(perfilProfessor.this, mensagem, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

package com.example.sisgesc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sisgesc.DTOs.AdminPerfil;
import com.example.sisgesc.Repository.AdminRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

// Perfil do Administrador: dados do banco (AdminRepository.buscarPerfil) e botão Sair.
//
// Para o back-end:
//   btnEditarDados   abrir dialog_editar_dados_admin -> atualizarEmail / atualizarTelefone
//   btnAlterarSenha  abrir dialog_alterar_senha_admin -> alterarSenha(cpf, atual, nova)
//   imgPerfilAdmin   escolher imagem -> atualizarFoto (opcional)
public class activity_admin_perfil extends AppCompatActivity {

    private final AdminRepository repository = new AdminRepository();

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

        // Sair: volta ao login e fecha todas as telas do Administrador
        findViewById(R.id.btnSairPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        repository.buscarPerfil(getIntent().getStringExtra(MainActivity.EXTRA_CPF), new RepositoryCallback<AdminPerfil>() {
            @Override
            public void onSucesso(AdminPerfil perfil) {
                mostrarPerfil(perfil);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_perfil.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
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

package com.example.bandaalana.administrador;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bandaalana.administrador.DTOs.UsuarioLogado;
import com.example.bandaalana.administrador.Repository.AuthRepository;
import com.example.bandaalana.administrador.Repository.RepositoryCallback;

// Tela de login única: confere o CPF e a senha e abre as telas do perfil de quem entrou.
public class LoginAdminActivity extends AppCompatActivity {

    // As outras telas leem o CPF de quem entrou com getIntent().getStringExtra(MainActivity.EXTRA_CPF)
    public static final String EXTRA_CPF = "cpf";

    // Telas iniciais de cada módulo, pelo nome. Assim este arquivo pode ser igual em todas as
    // branches, mesmo nas que ainda não têm a tela de algum módulo.
    private static final String TELA_ALUNO = "com.example.bandaalana.feature.aluno.dashboardaluno";
    private static final String TELA_PROFESSOR = "com.example.bandaalana.professor.dashboardProfessor";
    private static final String TELA_ADMINISTRADOR = "com.example.bandaalana.administrador.DashboardActivity";


    private EditText edtCpf, edtSenha;
    private AppCompatButton btnEntrar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_administrador);
        findViewById(R.id.btnVoltarPerfilAdmin).setOnClickListener(v -> finish());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtCpf = findViewById(R.id.edtCpf);
        edtSenha = findViewById(R.id.edtSenhaAdmin);
        btnEntrar = findViewById(R.id.btnEntrarAdmin);
        authRepository = new AuthRepository();

        btnEntrar.setOnClickListener(v -> tentarEntrar());

        findViewById(R.id.txtEsqueciSenhaAdmin).setOnClickListener(v -> {
            Intent intent = new Intent(LoginAdminActivity.this, RecuperarSenhaActivity.class);
            startActivity(intent);
        });
    }

    private void tentarEntrar() {
        String cpf = edtCpf.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        if (cpf.length() != 11) {
            edtCpf.setError("Digite os 11 números do CPF");
            return;
        }
        if (senha.isEmpty()) {
            edtSenha.setError("Digite a senha");
            return;
        }

        // Evita enviar o login duas vezes enquanto a resposta não chega
        btnEntrar.setEnabled(false);

        authRepository.entrar(cpf, senha, new RepositoryCallback<UsuarioLogado>() {
            @Override
            public void onSucesso(UsuarioLogado usuario) {
                btnEntrar.setEnabled(true);
                abrirTelaDoPerfil(usuario);
            }

            @Override
            public void onErro(String mensagem) {
                btnEntrar.setEnabled(true);
                Toast.makeText(LoginAdminActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void abrirTelaDoPerfil(UsuarioLogado usuario) {
        String tela;
        switch (usuario.getPerfil()) {
            case ALUNO:
                tela = TELA_ALUNO;
                break;
            case PROFESSOR:
                tela = TELA_PROFESSOR;
                break;
            default:
                tela = TELA_ADMINISTRADOR;
                break;
        }

        Intent intent = new Intent().setClassName(this, tela).putExtra(EXTRA_CPF, usuario.getCpf());
        try {
            startActivity(intent);
            finish();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Este módulo ainda não está disponível nesta versão.", Toast.LENGTH_LONG).show();
        }
    }
}

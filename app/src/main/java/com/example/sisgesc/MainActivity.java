package com.example.sisgesc;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sisgesc.DTOs.Aluno;
import com.example.sisgesc.DTOs.Funcionario;
import com.example.sisgesc.Repository.AuthRepository;

public class MainActivity extends AppCompatActivity {

    private EditText edtCpf, edtSenha;
    private AppCompatButton btnEntrar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtCpf = findViewById(R.id.edtCpf);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrarAluno); // ID do botão no XML

        // Inicializa o repositório que criamos anteriormente
        authRepository = new AuthRepository();

        // Ação ao clicar no botão Entrar
        btnEntrar.setOnClickListener(v -> {
            String cpfDigitado = edtCpf.getText().toString().trim();
            String senhaDigitada = edtSenha.getText().toString().trim();

            if (cpfDigitado.isEmpty()) {
                edtCpf.setError("Digite o CPF");
                return;
            }

            // Exemplo visual de carregamento ou feedback rápido
            Toast.makeText(MainActivity.this, "Verificando credenciais...", Toast.LENGTH_SHORT).show();

            // Dispara a verificação no Supabase usando o repositório
            authRepository.verificarUsuarioPorCpf(cpfDigitado, new AuthRepository.LoginCallback() {
                @Override
                public void onAlunoEncontrado(Aluno aluno) {
                    // O usuário é um Aluno!
                    // Aqui você fará a Intent para abrir a AlunoActivity
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Bem-vindo, Aluno " + aluno.getPrimeiro_nome(), Toast.LENGTH_LONG).show();
                        // TODO: Intent para Tela do Aluno
                    });
                }

                @Override
                public void onFuncionarioEncontrado(Funcionario funcionario, boolean isAdministrador) {
                    // O usuário é um Funcionário (Professor ou Administrador)
                    runOnUiThread(() -> {
                        if (isAdministrador) {
                            Toast.makeText(MainActivity.this, "Bem-vindo, Administrador!", Toast.LENGTH_LONG).show();
                            // TODO: Intent para Tela do ADM
                        } else {
                            Toast.makeText(MainActivity.this, "Bem-vindo, Professor!", Toast.LENGTH_LONG).show();
                            // TODO: Intent para Tela do Professor
                        }
                    });
                }

                @Override
                public void onError(String mensagemErro) {
                    // Caso o CPF não exista ou ocorra algum erro
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, mensagemErro, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }

}
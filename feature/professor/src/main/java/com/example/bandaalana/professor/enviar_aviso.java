package com.example.bandaalana.professor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bandaalana.professor.Repository.ProfessorRepository;
import com.example.bandaalana.professor.Repository.RepositoryCallback;

// Tela para o professor enviar avisos direcionados a uma turma específica.
public class enviar_aviso extends AppCompatActivity {

    private String cpf;
    private String nomeTurma;
    private ProfessorRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enviar_aviso);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recupera o CPF do professor logado e o nome da turma selecionada via Intent
        cpf = getIntent().getStringExtra(LoginProfessorActivity.EXTRA_CPF);
        nomeTurma = getIntent().getStringExtra("nomeTurma");
        repo = new ProfessorRepository();

        // Referências dos elementos de UI
        ImageButton btnVoltar = findViewById(R.id.btnVoltar);
        TextView txtTurma = findViewById(R.id.txtNomeTurma);
        EditText edtTitulo = findViewById(R.id.edtTitulo);
        EditText edtAviso = findViewById(R.id.edtAviso);
        TextView txtContador = findViewById(R.id.txtContador);
        Button btnEnviar = findViewById(R.id.btnEnviarAviso);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        // Exibe o nome da turma na tela (ou "Geral" se não houver turma específica)
        if (nomeTurma != null && !nomeTurma.isEmpty()) {
            txtTurma.setText(nomeTurma);
        } else {
            txtTurma.setText("Geral");
        }

        // Configuração dos botões de voltar e cancelar
        btnVoltar.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());

        // Atualiza o contador de caracteres (máximo 300) conforme o professor digita a mensagem
        edtAviso.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int tamanho = s != null ? s.length() : 0;
                txtContador.setText(tamanho + "/300");
            }
        });

        // Ação ao clicar em "Enviar aviso": valida o título e a mensagem e grava no banco via repositório
        btnEnviar.setOnClickListener(v -> {
            String titulo = edtTitulo.getText().toString().trim();
            String mensagem = edtAviso.getText().toString().trim();

            if (titulo.isEmpty()) {
                edtTitulo.setError("Digite o título do aviso");
                return;
            }

            repo.enviarAviso(cpf, titulo, mensagem, new RepositoryCallback<Void>() {
                @Override
                public void onSucesso(Void unused) {
                    Toast.makeText(enviar_aviso.this, "Aviso enviado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onErro(String mensagemErro) {
                    Toast.makeText(enviar_aviso.this, mensagemErro, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}

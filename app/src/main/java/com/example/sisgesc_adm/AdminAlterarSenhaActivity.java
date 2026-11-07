package com.example.sisgesc_adm;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class AdminAlterarSenhaActivity extends AppCompatActivity {

    private EditText etSenhaAtual, etNovaSenha, etConfirmarSenha;
    private MaterialButton btnSalvarSenha;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_alterar_senha);


        // Configuração do botão Voltar do cabeçalho
        View btnVoltar = findViewById(R.id.btnVoltar);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        // Mapeamento dos componentes do layout XML
        etSenhaAtual = findViewById(R.id.etSenhaAtual);
        etNovaSenha = findViewById(R.id.etNovaSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        btnSalvarSenha = findViewById(R.id.btnSalvarSenha);

        // Ação do botão de salvar nova senha
        if (btnSalvarSenha != null) {
            btnSalvarSenha.setOnClickListener(v -> {
                String senhaAtual = etSenhaAtual.getText().toString().trim();
                String novaSenha = etNovaSenha.getText().toString().trim();
                String confirmarSenha = etConfirmarSenha.getText().toString().trim();

                // Validações
                if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                    Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!novaSenha.equals(confirmarSenha)) {
                    Toast.makeText(this, "A nova senha e a confirmação não coincidem.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (novaSenha.length() < 6) {
                    Toast.makeText(this, "A nova senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sucesso
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Retorna para a tela de Perfil
            });
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminAlterarSenha), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
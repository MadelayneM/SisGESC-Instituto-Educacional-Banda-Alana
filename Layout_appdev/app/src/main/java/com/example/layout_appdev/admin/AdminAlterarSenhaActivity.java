package com.example.layout_appdev.admin;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.util.AdminUi;

public class AdminAlterarSenhaActivity extends AppCompatActivity {

    private static final int TAMANHO_MINIMO = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_alterar_senha);
        AdminUi.aplicarInsets(this);
        AdminUi.configurarCabecalho(this, "Alterar senha");

        EditText etAtual = findViewById(R.id.etSenhaAtual);
        EditText etNova = findViewById(R.id.etNovaSenha);
        EditText etConfirmar = findViewById(R.id.etConfirmarSenha);

        findViewById(R.id.btnSalvarSenha).setOnClickListener(v -> {
            String atual = etAtual.getText().toString();
            String nova = etNova.getText().toString();
            String confirmar = etConfirmar.getText().toString();

            if (atual.isEmpty()) {
                etAtual.setError("Informe a senha atual");
                etAtual.requestFocus();
                return;
            }
            if (nova.length() < TAMANHO_MINIMO) {
                etNova.setError("A senha deve ter pelo menos " + TAMANHO_MINIMO + " caracteres");
                etNova.requestFocus();
                return;
            }
            if (nova.equals(atual)) {
                etNova.setError("A nova senha deve ser diferente da atual");
                etNova.requestFocus();
                return;
            }
            if (!nova.equals(confirmar)) {
                etConfirmar.setError("As senhas não conferem");
                etConfirmar.requestFocus();
                return;
            }

            // TODO (back-end): conferir a senha atual e gravar a nova senha
            Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

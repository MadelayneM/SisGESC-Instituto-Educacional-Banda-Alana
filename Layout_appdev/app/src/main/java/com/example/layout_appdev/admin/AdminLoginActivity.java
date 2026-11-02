package com.example.layout_appdev.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.FormDialog;

public class AdminLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        AdminUi.aplicarInsets(this);

        EditText etUsuario = findViewById(R.id.etUsuario);
        EditText etSenha = findViewById(R.id.etSenha);

        findViewById(R.id.btnEntrar).setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String senha = etSenha.getText().toString();

            if (usuario.isEmpty()) {
                etUsuario.setError("Informe seu e-mail ou usuário");
                etUsuario.requestFocus();
                return;
            }
            if (senha.isEmpty()) {
                etSenha.setError("Informe sua senha");
                etSenha.requestFocus();
                return;
            }

            // TODO (back-end): validar usuário e senha na autenticação antes de entrar
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
        });

        findViewById(R.id.tvEsqueciSenha).setOnClickListener(v ->
                FormDialog.mostrar(this, "Recuperar senha", "Seu e-mail", null, "Enviar",
                        (email, ignorado) -> {
                            // TODO (back-end): disparar o e-mail de recuperação
                            Toast.makeText(this,
                                    "Se o e-mail estiver cadastrado, enviaremos as instruções.",
                                    Toast.LENGTH_LONG).show();
                        }));

        findViewById(R.id.tvVoltarPerfil).setOnClickListener(v -> finish());
    }
}

package com.example.layout_appdev.admin;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.model.PerfilAdmin;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MaskWatcher;
import com.example.layout_appdev.admin.util.MockData;

public class AdminEditarPerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_editar_perfil);
        AdminUi.aplicarInsets(this);
        AdminUi.configurarCabecalho(this, "Editar dados");

        EditText etNome = findViewById(R.id.etNome);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etTelefone = findViewById(R.id.etTelefone);

        PerfilAdmin perfil = MockData.perfil();
        etTelefone.addTextChangedListener(new MaskWatcher(etTelefone, "(##) ####-####", "(##) #####-####"));
        etNome.setText(perfil.nome);
        etEmail.setText(perfil.email);
        etTelefone.setText(AdminUi.soDigitos(perfil.telefone)); // a máscara formata sozinha

        findViewById(R.id.btnSalvar).setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String telefone = etTelefone.getText().toString();

            if (nome.isEmpty()) {
                etNome.setError("Informe o nome");
                etNome.requestFocus();
                return;
            }
            if (email.isEmpty() || !AdminUi.emailValido(email)) {
                etEmail.setError("Informe um e-mail válido");
                etEmail.requestFocus();
                return;
            }
            if (AdminUi.soDigitos(telefone).length() < 10) {
                etTelefone.setError("Telefone incompleto");
                etTelefone.requestFocus();
                return;
            }

            // TODO (back-end): atualizar os dados do usuário no banco de dados
            perfil.nome = nome;
            perfil.email = email;
            perfil.telefone = telefone;

            Toast.makeText(this, "Dados atualizados", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

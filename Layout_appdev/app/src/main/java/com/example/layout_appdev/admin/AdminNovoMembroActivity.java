package com.example.layout_appdev.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.model.Membro;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MaskWatcher;
import com.example.layout_appdev.admin.util.MockData;

public class AdminNovoMembroActivity extends AppCompatActivity {

    private EditText etNome, etTurmas, etTelefone, etEmail;
    private Spinner spCargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_novo_membro);
        AdminUi.aplicarInsets(this);
        AdminUi.configurarCabecalho(this, "Novo Membro");

        etNome = findViewById(R.id.etNome);
        etTurmas = findViewById(R.id.etTurmas);
        etTelefone = findViewById(R.id.etTelefone);
        etEmail = findViewById(R.id.etEmail);
        spCargo = findViewById(R.id.spCargo);

        etTelefone.addTextChangedListener(new MaskWatcher(etTelefone, "(##) ####-####", "(##) #####-####"));

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Membro.CARGOS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCargo.setAdapter(adapter);

        findViewById(R.id.btnCadastrarMembro).setOnClickListener(v -> cadastrar());
    }

    private void cadastrar() {
        String nome = etNome.getText().toString().trim();
        if (nome.isEmpty()) {
            etNome.setError("Informe o nome completo");
            etNome.requestFocus();
            return;
        }
        String turmas = etTurmas.getText().toString().trim();
        if (turmas.isEmpty()) {
            etTurmas.setError("Informe a(s) turma(s) ou \"Todas as turmas\"");
            etTurmas.requestFocus();
            return;
        }
        String tel = AdminUi.soDigitos(etTelefone.getText().toString());
        if (!tel.isEmpty() && tel.length() < 10) {
            etTelefone.setError("Telefone incompleto");
            etTelefone.requestFocus();
            return;
        }
        String email = etEmail.getText().toString().trim();
        if (!email.isEmpty() && !AdminUi.emailValido(email)) {
            etEmail.setError("E-mail inválido");
            etEmail.requestFocus();
            return;
        }

        Membro membro = new Membro(nome, turmas, (String) spCargo.getSelectedItem());
        membro.setTelefone(etTelefone.getText().toString());
        membro.setEmail(email);

        // TODO (back-end): salvar o membro (e criar o acesso dele) no banco de dados
        MockData.equipe().add(0, membro);

        Toast.makeText(this, "Membro cadastrado com sucesso", Toast.LENGTH_SHORT).show();
        finish();
    }
}

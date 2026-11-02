package com.example.layout_appdev.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.adapter.AlunoAdapter;
import com.example.layout_appdev.admin.util.AdminNav;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MockData;

public class AdminAlunosActivity extends AppCompatActivity {

    private AlunoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_alunos);
        AdminUi.aplicarInsets(this);
        AdminNav.configurar(this, R.id.nav_alunos);

        RecyclerView rvAlunos = findViewById(R.id.rvAlunos);
        EditText etBuscar = findViewById(R.id.etBuscar);

        adapter = new AlunoAdapter(MockData.alunos());
        rvAlunos.setAdapter(adapter);
        AdminUi.adicionarDivisor(rvAlunos);

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                adapter.filtrar(s.toString());
            }
        });

        findViewById(R.id.btnNovoAluno).setOnClickListener(v ->
                startActivity(new Intent(this, AdminNovoAlunoActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.atualizar(); // mostra o aluno recém-cadastrado
    }
}

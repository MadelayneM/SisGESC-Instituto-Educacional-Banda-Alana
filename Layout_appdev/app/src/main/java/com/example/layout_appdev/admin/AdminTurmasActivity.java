package com.example.layout_appdev.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.adapter.TurmaAdapter;
import com.example.layout_appdev.admin.util.AdminNav;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MockData;

public class AdminTurmasActivity extends AppCompatActivity {

    private TurmaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_turmas);
        AdminUi.aplicarInsets(this);
        AdminNav.configurar(this, R.id.nav_turmas);

        RecyclerView rvTurmas = findViewById(R.id.rvTurmas);
        adapter = new TurmaAdapter(MockData.turmas());
        rvTurmas.setAdapter(adapter);

        findViewById(R.id.btnNovaTurma).setOnClickListener(v ->
                startActivity(new Intent(this, AdminNovaTurmaActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.atualizar();
    }
}

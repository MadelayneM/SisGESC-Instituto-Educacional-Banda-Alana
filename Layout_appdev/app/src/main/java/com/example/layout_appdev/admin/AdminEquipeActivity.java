package com.example.layout_appdev.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.adapter.MembroAdapter;
import com.example.layout_appdev.admin.util.AdminNav;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MockData;

public class AdminEquipeActivity extends AppCompatActivity {

    private MembroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_equipe);
        AdminUi.aplicarInsets(this);
        AdminNav.configurar(this, R.id.nav_equipe);

        RecyclerView rvEquipe = findViewById(R.id.rvEquipe);
        adapter = new MembroAdapter(MockData.equipe());
        rvEquipe.setAdapter(adapter);
        AdminUi.adicionarDivisor(rvEquipe);

        findViewById(R.id.btnNovoMembro).setOnClickListener(v ->
                startActivity(new Intent(this, AdminNovoMembroActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.atualizar();
    }
}

package com.example.sisgesc_adm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminNovoAvisoActivity extends AppCompatActivity {


    private View btnVoltar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_novo_aviso);

        // Encontre o botão de voltar pelo ID (confirme se o ID no view_form_header.xml é btnBack ou similar)
        View btnVoltar = findViewById(R.id.btnVoltar);

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> {
                // Finaliza a activity atual e retorna para a anterior (AdminTurmasActivity)
                finish();
            });
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminNovoAviso), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
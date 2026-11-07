package com.example.sisgesc_adm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EquipeActivity extends AppCompatActivity {


    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_equipe);

        // 1. Inicializar o BottomNavigationView encontrando o ID do layout XML
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);


        // 3. Defina a aba "Equipe" como a selecionada por padrão nesta tela
        bottomNavigationView.setSelectedItemId(R.id.nav_equipe);

        // 4. Configure o listener de cliques para alternar entre as telas
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(getApplicationContext(), AdminDashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_alunos) {
                startActivity(new Intent(getApplicationContext(), AdminAlunosActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_turmas) {
                startActivity(new Intent(getApplicationContext(), AdminTurmasActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_equipe) {
                // Já está na tela de equipe, não precisa fazer nada
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(getApplicationContext(), AdminPerfilActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminEquipe), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
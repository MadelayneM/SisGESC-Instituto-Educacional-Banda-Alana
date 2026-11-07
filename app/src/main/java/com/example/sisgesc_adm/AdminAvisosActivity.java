package com.example.sisgesc_adm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class AdminAvisosActivity extends AppCompatActivity {

    private MaterialButton btnNovoAviso;
    private BottomNavigationView bottomNav;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_avisos);


        // Ajuste de margens utilizando o ID correto da raiz (R.id.Avisos)
        View mainLayout = findViewById(R.id.Avisos);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // Inicializar o botão "+ Novo aviso"
        btnNovoAviso = findViewById(R.id.btnNovoAviso);
        if (btnNovoAviso != null) {
            btnNovoAviso.setOnClickListener(v -> {
                Intent intent = new Intent(AdminAvisosActivity.this, AdminNovoAvisoActivity.class);
                startActivity(intent);
            });
        }


        // Configurar a barra de navegação inferior (BottomNavigationView)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            // Marca o item correspondente ou define a lógica de transição se necessário
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(this, AdminDashboardActivity.class));
                    finish();
                    return true;
                }
                // Adicione os outros redirecionamentos do menu inferior se desejar
                return true;
            });
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Avisos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
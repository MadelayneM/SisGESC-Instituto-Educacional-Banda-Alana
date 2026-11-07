package com.example.sisgesc_adm;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class AdminAlunosActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    private MaterialButton btnNovoAluno;

    private BottomNavigationView bottomNavigationView;
    //private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_alunos);

        // novo aluno
        MaterialButton btnNovoAluno = findViewById(R.id.btnNovoAluno);
        btnNovoAluno.setOnClickListener(ViewV->{
            Intent intent = new Intent(AdminAlunosActivity.this, AdminNovoAlunoActivity.class);
            startActivity(intent);
        });

        // 2. Inicialize o componente encontrando-o pelo ID no XML
        bottomNavigationView = findViewById(R.id.bottomNav); // Certifique-se de que o ID no XML seja id/bottomNav

        // 3. Defina a aba "Alunos" como a selecionada por padrão nesta tela
        bottomNavigationView.setSelectedItemId(R.id.nav_alunos);

        // 4. Configure o listener de cliques para alternar entre as telas
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(getApplicationContext(), AdminDashboardActivity.class)); // Substitua pela sua Activity de Início/Dashboard
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_alunos) {
                // Já está na tela de alunos, não precisa fazer nada
                return true;
            } else if (id == R.id.nav_turmas) {
                startActivity(new Intent(getApplicationContext(), AdminTurmasActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_equipe) {
                startActivity(new Intent(getApplicationContext(), EquipeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(getApplicationContext(), AdminPerfilActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminAlunos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
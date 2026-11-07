package com.example.sisgesc_adm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class AdminDashboardActivity extends AppCompatActivity {

    //components
    private MaterialCardView cardAlunos,cardTurmas, cardProfessores,cardConsultarFrequencia;
    private BottomNavigationView bottomNav;

    private MaterialCardView cardAvisos;
    private MaterialButton btnAddAvisoCard;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_dashboard);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav); // Certifique-se de que o ID no XML da barra é este

        // Inicialização dos componentes de avisos
        cardAvisos = findViewById(R.id.cardAvisos);
        btnAddAvisoCard = findViewById(R.id.btnAddAvisoCard);
        // Clique no Card Inteiro (Abre a lista/gerenciamento de avisos)
        if (cardAvisos != null) {
            cardAvisos.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminAvisosActivity.class);
                startActivity(intent);
            });
        }

// Clique no Botão "+ Add" (Abre direto a tela de novo aviso)
        if (btnAddAvisoCard != null) {
            btnAddAvisoCard.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminNovoAvisoActivity.class); // ou AdminAvisosActivity com extra
                startActivity(intent);
            });
        }

    // Define a aba inicial selecionada como Dashboard/Início
        bottomNavigationView.setSelectedItemId(R.id.nav_inicio);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                // Já está no dashboard, não precisa fazer nada ou pode recriar
                return true;
            } else if (id == R.id.nav_alunos) {
                startActivity(new Intent(getApplicationContext(), AdminAlunosActivity.class));
                overridePendingTransition(0, 0); // Remove animação de transição para ficar fluido
                finish(); // Fecha a activity atual se desejar não acumular pilha
                return true;
            } else if (id == R.id.nav_turmas) {
                // Crie e aponte para a activity de turmas quando tiver
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardAlunos = findViewById(R.id.cardAlunos);
        cardTurmas = findViewById(R.id.cardTurmas);
        cardProfessores = findViewById(R.id.cardProfessores);
        cardConsultarFrequencia = findViewById(R.id.cardConsultarFrequencia);





        // Exemplo para abrir a tela de Alunos ao clicar no card correspondente
        cardAlunos.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminAlunosActivity.class);
            startActivity(intent);
        });

        cardTurmas.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminTurmasActivity.class);
            startActivity(intent);
        });


     //abrir a tela de Equipe/Professores
        cardProfessores.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, EquipeActivity.class);
            startActivity(intent);
        });

        //abir a tela de aluno ao Consultarfrequecia
        cardConsultarFrequencia.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminAlunosActivity.class);
            startActivity(intent);
        });




    }
}
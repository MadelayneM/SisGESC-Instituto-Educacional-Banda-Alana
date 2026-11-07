package com.example.sisgesc_adm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class AdminPerfilActivity extends AppCompatActivity {


    private BottomNavigationView bottomNav;

    private MaterialButton btnEditarDados,btnAlterarSenha,btnSair;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_perfil);

        // 1. Inicializar o BottomNavigationView encontrando o ID do layout XML
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        // 2. Definir a aba "Perfil" como a selecionada por padrão nesta tela
        bottomNavigationView.setSelectedItemId(R.id.nav_perfil);

        // 3. Configurar o listener de cliques para alternar entre as telas
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
                startActivity(new Intent(getApplicationContext(), EquipeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_perfil) {
                // Já está na tela de perfil, não precisa fazer nada
                return true;
            }
            return false;
        });

        // 1. Botão Editar Dados -> Abre a tela de edição de perfil
        MaterialButton btnEditarDados = findViewById(R.id.btnEditarDados); // Ajuste o ID se necessário
        if (btnEditarDados != null) {
            btnEditarDados.setOnClickListener(v -> {
                Intent intent = new Intent(AdminPerfilActivity.this, AdminEditarPerfilActivity.class);
                startActivity(intent);
            });
        }

        // 2. Botão Alterar Senha -> Abre a tela de alteração de senha
        MaterialButton btnAlterarSenha = findViewById(R.id.btnAlterarSenha); // Ajuste o ID se necessário
        if (btnAlterarSenha != null) {
            btnAlterarSenha.setOnClickListener(v -> {
                Intent intent = new Intent(AdminPerfilActivity.this, AdminAlterarSenhaActivity.class);
                startActivity(intent);
            });
        }

        // 3. Botão Sair -> Encerra a sessão e retorna ao Login (ou tela inicial)
        MaterialButton btnSair = findViewById(R.id.btnSair); // Ajuste o ID se necessário
        if (btnSair != null) {
            btnSair.setOnClickListener(v -> {
                // Aqui você pode limpar sessões/SharedPreferences se necessário
                Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show();

                // Exemplo redirecionando para a Dashboard ou Login (substitua LoginActivity.class se tiver uma)
                Intent intent = new Intent(AdminPerfilActivity.this, AdminDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpa a pilha de telas
                startActivity(intent);
                finish();
            });
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminPerfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
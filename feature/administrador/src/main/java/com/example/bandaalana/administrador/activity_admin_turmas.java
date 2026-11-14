package com.example.bandaalana.administrador;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandaalana.administrador.Adapters.AdminTurmaAdapter;
import com.example.bandaalana.administrador.DTOs.AdminTurma;
import com.example.bandaalana.administrador.Repository.AdminRepository;
import com.example.bandaalana.administrador.Repository.RepositoryCallback;

import java.util.List;

// Tela Turmas do Administrador: lista do banco (AdminRepository.buscarTurmas).
// "Nova Turma" abre o formulário vazio; tocar numa turma abre o formulário de edição.
public class activity_admin_turmas extends AppCompatActivity {

    private final AdminRepository repository = new AdminRepository();
    private final AdminTurmaAdapter adapter = new AdminTurmaAdapter(turma ->
            FormularioAdmin.abrir(this, activity_admin_nova_turma.class,
                    FormularioAdmin.EXTRA_NOME_TURMA, turma.getNomeTurma()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_turmas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminTurmas), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAdmin.configurar(this, R.id.navTurmas);

        RecyclerView rvTurmas = findViewById(R.id.rvTurmas);
        rvTurmas.setAdapter(adapter);

        findViewById(R.id.btnNovaTurma).setOnClickListener(v ->
                FormularioAdmin.abrir(this, activity_admin_nova_turma.class, null, null));
    }

    // Recarrega ao voltar do formulário, para mostrar a turma criada ou editada
    @Override
    protected void onResume() {
        super.onResume();
        repository.buscarTurmas(new RepositoryCallback<List<AdminTurma>>() {
            @Override
            public void onSucesso(List<AdminTurma> turmas) {
                adapter.atualizar(turmas);
                if (turmas.isEmpty()) {
                    Toast.makeText(activity_admin_turmas.this, "Nenhuma turma cadastrada.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_turmas.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }
}

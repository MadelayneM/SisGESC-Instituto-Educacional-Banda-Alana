package com.example.sisgesc;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sisgesc.Adapters.AdminMembroAdapter;
import com.example.sisgesc.DTOs.AdminMembro;
import com.example.sisgesc.Repository.AdminRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

// Tela Equipe: lista do banco (AdminRepository.buscarEquipe).
// "Novo" abre o formulário vazio; tocar num membro abre o formulário de edição.
public class activity_admin_equipe extends AppCompatActivity {

    private final AdminRepository repository = new AdminRepository();
    private final AdminMembroAdapter adapter = new AdminMembroAdapter(membro ->
            FormularioAdmin.abrir(this, activity_admin_novo_membro.class,
                    FormularioAdmin.EXTRA_CPF_MEMBRO, membro.getCpf()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_equipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAdmin.configurar(this, R.id.navEquipe);

        RecyclerView rvEquipe = findViewById(R.id.rvEquipe);
        rvEquipe.setAdapter(adapter);

        findViewById(R.id.btnNovoMembro).setOnClickListener(v ->
                FormularioAdmin.abrir(this, activity_admin_novo_membro.class, null, null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        repository.buscarEquipe(new RepositoryCallback<List<AdminMembro>>() {
            @Override
            public void onSucesso(List<AdminMembro> membros) {
                adapter.atualizar(membros);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_equipe.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }
}

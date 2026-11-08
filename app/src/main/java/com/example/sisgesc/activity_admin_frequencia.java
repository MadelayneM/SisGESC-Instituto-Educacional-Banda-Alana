package com.example.sisgesc;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sisgesc.Adapters.AdminFrequenciaAdapter;
import com.example.sisgesc.DTOs.AdminTurma;
import com.example.sisgesc.Repository.AdminRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

// Tela "Consultar frequência" (atalho do Dashboard): frequência geral e de cada turma.
// Dados: AdminRepository.buscarTurmas (cada AdminTurma já traz presenças e faltas).
public class activity_admin_frequencia extends AppCompatActivity {

    private final AdminRepository repository = new AdminRepository();
    private final AdminFrequenciaAdapter adapter = new AdminFrequenciaAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_frequencia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminFrequencia), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FormularioAdmin.configurarCabecalho(this, "Consultar frequência");

        RecyclerView rvFrequencia = findViewById(R.id.rvFrequencia);
        rvFrequencia.setAdapter(adapter);

        repository.buscarTurmas(new RepositoryCallback<List<AdminTurma>>() {
            @Override
            public void onSucesso(List<AdminTurma> turmas) {
                mostrarFrequencia(turmas);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_frequencia.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarFrequencia(List<AdminTurma> turmas) {
        adapter.atualizar(turmas);
        findViewById(R.id.tvSemFrequencia).setVisibility(turmas.isEmpty() ? View.VISIBLE : View.GONE);

        // Visão geral: soma das chamadas de todas as turmas
        int presencas = 0;
        int faltas = 0;
        for (AdminTurma turma : turmas) {
            presencas += turma.getPresencas();
            faltas += turma.getFaltas();
        }
        int total = presencas + faltas;
        Integer geral = total == 0 ? null : Math.round(presencas * 100f / total);

        TextView tvFreqGeral = findViewById(R.id.tvFreqGeral);
        tvFreqGeral.setText(geral == null ? "--" : geral + "%");
        tvFreqGeral.setTextColor(ContextCompat.getColor(this, AdminFrequenciaAdapter.corDaFrequencia(geral)));
        ((TextView) findViewById(R.id.tvTotalPresencas)).setText(String.valueOf(presencas));
        ((TextView) findViewById(R.id.tvTotalFaltas)).setText(String.valueOf(faltas));
    }
}

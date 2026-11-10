package com.example.sisgesc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sisgesc.DTOs.TurmaFrequencia;
import com.example.sisgesc.Repository.ProfessorRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

public class frequenciaProfessor extends AppCompatActivity {

    private String cpf;
    private ProfessorRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_frequencia_professor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuProfessor.configurar(this, R.id.navFrequencia);

        cpf = getIntent().getStringExtra(MainActivity.EXTRA_CPF);
        repo = new ProfessorRepository();

        carregarFrequencia();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cpf != null) {
            carregarFrequencia();
        }
    }

    private void carregarFrequencia() {
        repo.buscarHistoricoFrequencia(cpf, new RepositoryCallback<List<TurmaFrequencia>>() {
            @Override
            public void onSucesso(List<TurmaFrequencia> frequencias) {
                mostrarFrequencias(frequencias);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(frequenciaProfessor.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarFrequencias(List<TurmaFrequencia> frequencias) {
        LinearLayout listaFrequencia = findViewById(R.id.listaFrequencia);
        listaFrequencia.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (TurmaFrequencia turmaFreq : frequencias) {
            View secaoTurma = inflater.inflate(R.layout.item_frequencia_turma_professor, listaFrequencia, false);
            TextView txtNomeTurma = secaoTurma.findViewById(R.id.txtNomeTurmaFrequencia);
            TextView txtSemAulas = secaoTurma.findViewById(R.id.txtSemAulas);
            LinearLayout listaAulas = secaoTurma.findViewById(R.id.listaAulas);

            txtNomeTurma.setText(turmaFreq.getNomeTurma());

            List<TurmaFrequencia.Aula> aulas = turmaFreq.getAulas();
            if (aulas == null || aulas.isEmpty()) {
                txtSemAulas.setVisibility(View.VISIBLE);
            } else {
                txtSemAulas.setVisibility(View.GONE);
                for (TurmaFrequencia.Aula aula : aulas) {
                    View linhaAula = inflater.inflate(R.layout.item_frequencia_aula_professor, listaAulas, false);
                    TextView txtData = linhaAula.findViewById(R.id.txtDataAula);
                    TextView txtPresentes = linhaAula.findViewById(R.id.txtPresentesAula);
                    TextView txtAusentes = linhaAula.findViewById(R.id.txtAusentesAula);

                    txtData.setText(aula.getDataFormatada());
                    txtPresentes.setText("🟢 " + aula.getPresentes());
                    txtAusentes.setText("🔴 " + aula.getAusentes());

                    linhaAula.setOnClickListener(v -> {
                        Intent i = new Intent(this, chamadaProfessor.class);
                        i.putExtra(MainActivity.EXTRA_CPF, cpf);
                        i.putExtra("nomeTurma", turmaFreq.getNomeTurma());
                        i.putExtra("dataAula", aula.getDataAula());
                        startActivity(i);
                    });

                    listaAulas.addView(linhaAula);
                }
            }

            listaFrequencia.addView(secaoTurma);
        }

        View txtSemFrequencia = findViewById(R.id.txtSemFrequencia);
        if (txtSemFrequencia != null) {
            txtSemFrequencia.setVisibility(frequencias.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}

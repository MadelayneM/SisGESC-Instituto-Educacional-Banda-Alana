package com.example.sisgesc;

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

import com.example.sisgesc.DTOs.ProfessorTurma;
import com.example.sisgesc.Repository.ProfessorRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

public class turmasProfessor extends AppCompatActivity {

    private String cpf;
    private ProfessorRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_turmas_professor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuProfessor.configurar(this, R.id.navTurmas);

        cpf = getIntent().getStringExtra(MainActivity.EXTRA_CPF);
        repo = new ProfessorRepository();

        carregarTurmas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cpf != null) {
            carregarTurmas();
        }
    }

    private void carregarTurmas() {
        repo.buscarTurmas(cpf, new RepositoryCallback<List<ProfessorTurma>>() {
            @Override
            public void onSucesso(List<ProfessorTurma> turmas) {
                mostrarTurmas(turmas);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(turmasProfessor.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarTurmas(List<ProfessorTurma> turmas) {
        LinearLayout lista = findViewById(R.id.listaTurmas);
        lista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ProfessorTurma turma : turmas) {
            View cartao = inflater.inflate(R.layout.item_turma_professor, lista, false);
            TextView txtNome = cartao.findViewById(R.id.txtNomeTurma);
            TextView txtInfo = cartao.findViewById(R.id.txtInfoTurma);
            TextView txtDia = cartao.findViewById(R.id.txtDiaHorario);
            TextView txtStatus = cartao.findViewById(R.id.txtStatusTurma);

            txtNome.setText(turma.getNomeTurma());
            txtInfo.setText(turma.getTotalAlunos() + " alunos • " + turma.getNomeCurso());
            txtDia.setText("📅 " + turma.getDiaSemana() + " • 🕒 " + turma.getHorarioInicio() + " às " + turma.getHorarioFim());
            txtStatus.setText(turma.isAtiva() ? "● Ativa" : "○ Sem alunos");

            lista.addView(cartao);
        }

        View txtSemTurmas = findViewById(R.id.txtSemTurmas);
        if (txtSemTurmas != null) {
            txtSemTurmas.setVisibility(turmas.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}

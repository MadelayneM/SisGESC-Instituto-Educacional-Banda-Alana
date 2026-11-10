package com.example.sisgesc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sisgesc.DTOs.ProfessorInicio;
import com.example.sisgesc.DTOs.ProfessorTurma;
import com.example.sisgesc.Repository.ProfessorRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class dashboardProfessor extends AppCompatActivity {

    private String cpf;
    private ProfessorRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_professor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuProfessor.configurar(this, R.id.navInicio);

        cpf = getIntent().getStringExtra(MainActivity.EXTRA_CPF);
        repo = new ProfessorRepository();

        carregarDados();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cpf != null) {
            carregarDados();
        }
    }

    private void carregarDados() {
        repo.buscarInicio(cpf, new RepositoryCallback<ProfessorInicio>() {
            @Override
            public void onSucesso(ProfessorInicio inicio) {
                TextView txtNome = findViewById(R.id.txtNomeProfessor);
                txtNome.setText(inicio.getPrimeiroNome() + "! 👋");
                mostrarTurmas(inicio.getTurmas());
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(dashboardProfessor.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarTurmas(List<ProfessorTurma> turmas) {
        LinearLayout lista = findViewById(R.id.listaTurmasInicio);
        lista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        String hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        for (ProfessorTurma turma : turmas) {
            View cartao = inflater.inflate(R.layout.item_turma_inicio_professor, lista, false);
            TextView txtNome = cartao.findViewById(R.id.txtNomeTurma);
            TextView txtInfo = cartao.findViewById(R.id.txtInfoTurma);
            TextView txtDia = cartao.findViewById(R.id.txtDiaHorario);
            Button btnChamada = cartao.findViewById(R.id.btnChamada);
            Button btnVerAlunos = cartao.findViewById(R.id.btnVerAlunos);

            txtNome.setText(turma.getNomeTurma());
            txtInfo.setText(turma.getTotalAlunos() + " alunos • " + turma.getNomeCurso());
            txtDia.setText("📅 " + turma.getDiaSemana() + " • 🕒 " + turma.getHorarioInicio());

            btnChamada.setOnClickListener(v -> {
                Intent i = new Intent(this, chamadaProfessor.class);
                i.putExtra(MainActivity.EXTRA_CPF, cpf);
                i.putExtra("nomeTurma", turma.getNomeTurma());
                i.putExtra("dataAula", hoje);
                startActivity(i);
            });

            btnVerAlunos.setOnClickListener(v -> {
                Intent i = new Intent(this, alunosProfessor.class);
                i.putExtra(MainActivity.EXTRA_CPF, cpf);
                i.putExtra("nomeTurma", turma.getNomeTurma());
                startActivity(i);
            });

            lista.addView(cartao);
        }

        View txtSemTurmas = findViewById(R.id.txtSemTurmas);
        if (txtSemTurmas != null) {
            txtSemTurmas.setVisibility(turmas.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}

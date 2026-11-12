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

import com.example.sisgesc.DTOs.ProfessorTurma;
import com.example.sisgesc.Repository.ProfessorRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

// Tela "Minhas Turmas" do Professor: exibe todas as turmas vinculadas ao professor
// com informações detalhadas e atalho para enviar avisos.
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

        // Configura o menu inferior do professor
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

    // Busca a lista de turmas do professor no Supabase
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

    // Infla os cartões de cada turma na tela "Minhas Turmas"
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
            Button btnEnviarAviso = cartao.findViewById(R.id.btnEnviarAvisoTurma);

            txtNome.setText(turma.getNomeTurma());
            txtInfo.setText(turma.getTotalAlunos() + " alunos • " + turma.getNomeCurso());
            txtDia.setText("📅 " + turma.getDiaSemana() + " • 🕒 " + turma.getHorarioInicio() + " às " + turma.getHorarioFim());
            txtStatus.setText(turma.isAtiva() ? "● Ativa" : "○ Sem alunos");

            // Ação do botão Enviar Aviso para a turma
            btnEnviarAviso.setOnClickListener(v -> {
                Intent i = new Intent(this, enviar_aviso.class);
                i.putExtra(MainActivity.EXTRA_CPF, cpf);
                i.putExtra("nomeTurma", turma.getNomeTurma());
                startActivity(i);
            });

            lista.addView(cartao);
        }

        // Exibe mensagem se a lista de turmas estiver vazia
        View txtSemTurmas = findViewById(R.id.txtSemTurmas);
        if (txtSemTurmas != null) {
            txtSemTurmas.setVisibility(turmas.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}

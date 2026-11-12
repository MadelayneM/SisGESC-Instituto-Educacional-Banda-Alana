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

// Tela Início (Dashboard) do Professor: exibe a saudação com o primeiro nome
// e a lista de turmas do professor com atalhos para chamada, ver alunos e enviar aviso.
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

        // Configura o menu inferior do professor
        MenuProfessor.configurar(this, R.id.navInicio);

        // Recupera o CPF do professor logado e instancia o repositório
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

    // Busca os dados da tela Início no Supabase através do ProfessorRepository
    private void carregarDados() {
        repo.buscarInicio(cpf, new RepositoryCallback<ProfessorInicio>() {
            @Override
            public void onSucesso(ProfessorInicio inicio) {
                // Exibe o primeiro nome do professor na saudação
                TextView txtNome = findViewById(R.id.txtNomeProfessor);
                txtNome.setText(inicio.getPrimeiroNome() + "! 👋");
                // Preenche a lista de turmas
                mostrarTurmas(inicio.getTurmas());
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(dashboardProfessor.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Infla dinamicamente os cartões de cada turma na tela Início
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
            Button btnEnviarAviso = cartao.findViewById(R.id.btnEnviarAvisoTurma);

            txtNome.setText(turma.getNomeTurma());
            txtInfo.setText(turma.getTotalAlunos() + " alunos • " + turma.getNomeCurso());
            txtDia.setText("📅 " + turma.getDiaSemana() + " • 🕒 " + turma.getHorarioInicio());

            // Ação do botão Fazer Chamada
            btnChamada.setOnClickListener(v -> {
                Intent i = new Intent(this, chamadaProfessor.class);
                i.putExtra(MainActivity.EXTRA_CPF, cpf);
                i.putExtra("nomeTurma", turma.getNomeTurma());
                i.putExtra("dataAula", hoje);
                startActivity(i);
            });

            // Ação do botão Ver Alunos
            btnVerAlunos.setOnClickListener(v -> {
                Intent i = new Intent(this, alunosProfessor.class);
                i.putExtra(MainActivity.EXTRA_CPF, cpf);
                i.putExtra("nomeTurma", turma.getNomeTurma());
                startActivity(i);
            });

            // Ação do botão Enviar Aviso
            btnEnviarAviso.setOnClickListener(v -> {
                Intent i = new Intent(this, enviar_aviso.class);
                i.putExtra(MainActivity.EXTRA_CPF, cpf);
                i.putExtra("nomeTurma", turma.getNomeTurma());
                startActivity(i);
            });

            lista.addView(cartao);
        }

        // Exibe mensagem caso o professor não tenha turmas cadastradas
        View txtSemTurmas = findViewById(R.id.txtSemTurmas);
        if (txtSemTurmas != null) {
            txtSemTurmas.setVisibility(turmas.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}

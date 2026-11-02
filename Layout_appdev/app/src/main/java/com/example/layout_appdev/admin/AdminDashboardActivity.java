package com.example.layout_appdev.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.adapter.AlunoAdapter;
import com.example.layout_appdev.admin.model.Aluno;
import com.example.layout_appdev.admin.model.Membro;
import com.example.layout_appdev.admin.util.AdminNav;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MockData;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout llAlunosRecentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        AdminUi.aplicarInsets(this);
        AdminNav.configurar(this, R.id.nav_inicio);

        llAlunosRecentes = findViewById(R.id.llAlunosRecentes);

        // Atalhos de gerenciamento
        findViewById(R.id.cardCadastrarAluno).setOnClickListener(v ->
                startActivity(new Intent(this, AdminNovoAlunoActivity.class)));
        findViewById(R.id.cardNovaTurma).setOnClickListener(v ->
                startActivity(new Intent(this, AdminNovaTurmaActivity.class)));
        findViewById(R.id.cardCadastrarProfessor).setOnClickListener(v ->
                startActivity(new Intent(this, AdminNovoMembroActivity.class)));

        // TODO (integração): abrir a tela de frequência quando o grupo responsável a entregar
        findViewById(R.id.cardConsultarFrequencia).setOnClickListener(v ->
                Toast.makeText(this, "Consulta de frequência em desenvolvimento", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Totais calculados a partir dos dados atuais
        int professores = 0;
        for (Membro m : MockData.equipe()) if (m.isProfessor()) professores++;
        ((TextView) findViewById(R.id.tvTotalAlunos)).setText(String.valueOf(MockData.alunos().size()));
        ((TextView) findViewById(R.id.tvTotalTurmas)).setText(String.valueOf(MockData.turmas().size()));
        ((TextView) findViewById(R.id.tvTotalProfessores)).setText(String.valueOf(professores));
        // TODO (back-end): calcular a frequência geral (tvFrequenciaGeral) com os registros reais

        // Alunos mais recentes (os 4 primeiros da lista)
        llAlunosRecentes.removeAllViews();
        List<Aluno> todos = MockData.alunos();
        int quantidade = Math.min(4, todos.size());
        for (int i = 0; i < quantidade; i++) {
            View item = getLayoutInflater().inflate(R.layout.item_admin_aluno_recente, llAlunosRecentes, false);
            AlunoAdapter.vincular(item, todos.get(i));
            llAlunosRecentes.addView(item);
        }
    }
}

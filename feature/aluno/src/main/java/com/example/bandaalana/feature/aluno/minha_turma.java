package com.example.bandaalana.feature.aluno;

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

import com.example.bandaalana.feature.aluno.DTOs.AlunoTurma;
import com.example.bandaalana.feature.aluno.Repository.AlunoRepository;
import com.example.bandaalana.feature.aluno.Repository.RepositoryCallback;

import java.util.List;

public class minha_turma extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_minha_turma);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAluno.configurar(this, R.id.navTurma);

        String cpf = SessaoAluno.cpfOuVoltarAoLogin(this);
        if (cpf == null) {
            return;
        }

        new AlunoRepository().buscarTurma(cpf, new RepositoryCallback<AlunoTurma>() {
            @Override
            public void onSucesso(AlunoTurma turma) {
                if (SessaoAluno.telaFechada(minha_turma.this)) return;
                mostrarTurma(turma);
            }

            @Override
            public void onErro(String mensagem) {
                if (SessaoAluno.telaFechada(minha_turma.this)) return;
                Toast.makeText(minha_turma.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarTurma(AlunoTurma turma) {
        texto(R.id.txtTurmaTitulo, turma.getNomeTurma(), "Sem turma");
        texto(R.id.txtProfessorTurma, turma.getProfessor(), "-");
        texto(R.id.txtMonitorTurma, turma.getMonitor(), "-");
        texto(R.id.txtDiaTurma, turma.getDiaSemana(), "-");

        // Ex.: "14:00 - 15:30"
        String inicio = turma.getHorarioInicio();
        String fim = turma.getHorarioFim();
        String horario = inicio == null ? null : (fim == null ? inicio : inicio + " - " + fim);
        texto(R.id.txtHorarioTurma, horario, "-");

        Integer qtd = turma.getQtdAlunos();
        texto(R.id.txtQuantidadeAlunos, qtd == null ? null : String.valueOf(qtd), "-");

        mostrarColegas(turma.getColegas());
    }

    // Escreve o valor na tela, ou o texto alternativo quando o banco não tem o dado
    private void texto(int idDoCampo, String valor, String alternativa) {
        ((TextView) findViewById(idDoCampo)).setText(valor == null || valor.isEmpty() ? alternativa : valor);
    }

    // Monta a lista de colegas na tela. Chamar com turma.getColegas() dentro do onSucesso de buscarTurma.
    void mostrarColegas(List<String> nomes) {
        LinearLayout lista = findViewById(R.id.listaColegas);
        lista.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (String nome : nomes) {
            View linha = inflater.inflate(R.layout.item_colega, lista, false);
            TextView txtNome = linha.findViewById(R.id.txtNomeColega);
            txtNome.setText(nome);
            lista.addView(linha);
        }

        findViewById(R.id.txtSemColegas).setVisibility(nomes.isEmpty() ? View.VISIBLE : View.GONE);
    }
}

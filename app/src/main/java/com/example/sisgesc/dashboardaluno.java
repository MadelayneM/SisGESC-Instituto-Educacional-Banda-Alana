package com.example.sisgesc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sisgesc.DTOs.AlunoDashboard;
import com.example.sisgesc.DTOs.AlunoFrequencia;
import com.example.sisgesc.DTOs.AlunoTurma;
import com.example.sisgesc.DTOs.Aviso;
import com.example.sisgesc.Repository.AlunoRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

public class dashboardaluno extends AppCompatActivity {

    private static final int LIMITE_AVISOS = 5;

    private AlunoRepository repository;
    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_aluno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAluno.configurar(this, R.id.navInicio);

        cpf = SessaoAluno.cpfOuVoltarAoLogin(this);
        if (cpf == null) {
            return;
        }
        repository = new AlunoRepository();
        carregarDashboard();
        carregarAvisos();
    }

    private void carregarDashboard() {
        repository.buscarDashboard(cpf, new RepositoryCallback<AlunoDashboard>() {
            @Override
            public void onSucesso(AlunoDashboard dashboard) {
                if (SessaoAluno.telaFechada(dashboardaluno.this)) return;
                mostrarDashboard(dashboard);
            }

            @Override
            public void onErro(String mensagem) {
                if (SessaoAluno.telaFechada(dashboardaluno.this)) return;
                Toast.makeText(dashboardaluno.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void carregarAvisos() {
        repository.buscarAvisos(LIMITE_AVISOS, new RepositoryCallback<List<Aviso>>() {
            @Override
            public void onSucesso(List<Aviso> avisos) {
                if (SessaoAluno.telaFechada(dashboardaluno.this)) return;
                mostrarAvisos(avisos);
            }

            @Override
            public void onErro(String mensagem) {
                if (SessaoAluno.telaFechada(dashboardaluno.this)) return;
                Toast.makeText(dashboardaluno.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDashboard(AlunoDashboard dashboard) {
        AlunoTurma turma = dashboard.getTurma();
        AlunoFrequencia frequencia = dashboard.getFrequencia();

        String primeiroNome = dashboard.getPrimeiroNome() == null ? "" : dashboard.getPrimeiroNome();
        ((TextView) findViewById(R.id.txtNomeBoasVindas)).setText(primeiroNome + "! 👋");
        ((TextView) findViewById(R.id.txtNomeAluno)).setText(dashboard.getNomeCompleto());

        ImageView foto = findViewById(R.id.imgFotoAluno);
        if (dashboard.getFotoUrl() != null && !dashboard.getFotoUrl().isEmpty()) {
            Glide.with(this).load(dashboard.getFotoUrl()).circleCrop()
                    .placeholder(R.drawable.bg_foto_aluno).into(foto);
        }

        String nomeTurma = turma.getNomeTurma();
        ((TextView) findViewById(R.id.txtTurmaAluno))
                .setText(nomeTurma == null ? "Sem turma" : "Turma " + nomeTurma);
        ((TextView) findViewById(R.id.txtNomeTurma)).setText(nomeTurma == null ? "-" : nomeTurma);
        ((TextView) findViewById(R.id.txtCursoAluno))
                .setText(turma.getNomeCurso() == null ? "🎵 -" : "🎵 " + turma.getNomeCurso());
        ((TextView) findViewById(R.id.txtProfessorAluno))
                .setText(turma.getProfessor() == null
                        ? "👨‍🏫 Sem professor definido" : "👨‍🏫 Prof. " + turma.getProfessor());

        int colegas = turma.getColegas().size();
        ((TextView) findViewById(R.id.txtColegas))
                .setText(colegas + (colegas == 1 ? " colega" : " colegas"));

        int presencas = frequencia.getTotalPresencas();
        ((TextView) findViewById(R.id.txtPorcentagem)).setText(frequencia.getPercentual() + "%");
        ((TextView) findViewById(R.id.txtPresencas))
                .setText(presencas + (presencas == 1 ? " presença" : " presenças"));

        // Ex.: "Sexta-feira — 14:00"
        String dia = turma.getDiaSemana();
        String hora = turma.getHorarioInicio();
        String proxima;
        if (dia == null && hora == null) {
            proxima = "Sem aula marcada";
        } else if (hora == null) {
            proxima = dia;
        } else if (dia == null) {
            proxima = hora;
        } else {
            proxima = dia + " — " + hora;
        }
        ((TextView) findViewById(R.id.txtDataAula)).setText(proxima);
    }

    // Monta a lista de avisos, uma cópia de item_aviso.xml para cada um
    private void mostrarAvisos(List<Aviso> avisos) {
        LinearLayout lista = findViewById(R.id.listaAvisos);
        lista.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Aviso aviso : avisos) {
            View linha = inflater.inflate(R.layout.item_aviso, lista, false);
            ((TextView) linha.findViewById(R.id.txtDescricaoAviso)).setText(textoDoAviso(aviso));
            ((TextView) linha.findViewById(R.id.txtDataAviso)).setText(aviso.getDataFormatada());
            lista.addView(linha);
        }
    }

    // O item mostra uma linha de texto só, então junta o título e a descrição
    private String textoDoAviso(Aviso aviso) {
        boolean temTitulo = aviso.getTitulo() != null && !aviso.getTitulo().trim().isEmpty();
        boolean temDescricao = aviso.getDescricao() != null && !aviso.getDescricao().trim().isEmpty();
        if (temTitulo && temDescricao) {
            return aviso.getTitulo().trim() + ": " + aviso.getDescricao().trim();
        }
        if (temTitulo) return aviso.getTitulo().trim();
        return temDescricao ? aviso.getDescricao().trim() : "";
    }
}

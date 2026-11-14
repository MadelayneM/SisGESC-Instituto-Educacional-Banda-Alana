package com.example.bandaalana.feature.aluno;

import android.graphics.Color;
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

import com.example.bandaalana.feature.aluno.DTOs.AlunoFrequencia;
import com.example.bandaalana.feature.aluno.Repository.AlunoRepository;
import com.example.bandaalana.feature.aluno.Repository.RepositoryCallback;

import java.util.List;

public class minha_frequencia extends AppCompatActivity {

    // Cores do status, as mesmas indicadas no comentário de item_aula.xml
    private static final int COR_PRESENTE = Color.parseColor("#27864A");
    private static final int COR_AUSENTE = Color.parseColor("#D83232");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_minha_frequencia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAluno.configurar(this, R.id.navFrequencia);

        String cpf = SessaoAluno.cpfOuVoltarAoLogin(this);
        if (cpf == null) {
            return;
        }

        new AlunoRepository().buscarFrequencia(cpf, new RepositoryCallback<AlunoFrequencia>() {
            @Override
            public void onSucesso(AlunoFrequencia frequencia) {
                if (SessaoAluno.telaFechada(minha_frequencia.this)) return;
                mostrarFrequencia(frequencia);
            }

            @Override
            public void onErro(String mensagem) {
                if (SessaoAluno.telaFechada(minha_frequencia.this)) return;
                Toast.makeText(minha_frequencia.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarFrequencia(AlunoFrequencia frequencia) {
        ((TextView) findViewById(R.id.txtPercentualFrequencia)).setText(frequencia.getPercentual() + "%");
        ((TextView) findViewById(R.id.txtTotalPresencas)).setText(String.valueOf(frequencia.getTotalPresencas()));
        ((TextView) findViewById(R.id.txtTotalAusencias)).setText(String.valueOf(frequencia.getTotalAusencias()));
        ((TextView) findViewById(R.id.txtTotalAulas)).setText(String.valueOf(frequencia.getTotalAulas()));

        mostrarHistorico(frequencia.getHistorico());
    }

    // Uma cópia de item_aula.xml para cada aula, da mais recente para a mais antiga
    private void mostrarHistorico(List<AlunoFrequencia.RegistroAula> historico) {
        LinearLayout lista = findViewById(R.id.listaHistorico);
        lista.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = historico.size() - 1; i >= 0; i--) {
            AlunoFrequencia.RegistroAula aula = historico.get(i);
            View linha = inflater.inflate(R.layout.item_aula, lista, false);

            ((TextView) linha.findViewById(R.id.txtDataHistorico)).setText(aula.getDataFormatada());

            TextView status = linha.findViewById(R.id.txtStatusAula);
            View ponto = linha.findViewById(R.id.pontoStatusAula);
            if (aula.isPresente()) {
                status.setText("Presente");
                status.setTextColor(COR_PRESENTE);
                status.setBackgroundResource(R.drawable.bg_status_presente);
                ponto.setBackgroundResource(R.drawable.bg_ponto_presente);
            } else {
                status.setText("Ausente");
                status.setTextColor(COR_AUSENTE);
                status.setBackgroundResource(R.drawable.bg_status_ausente);
                ponto.setBackgroundResource(R.drawable.bg_ponto_ausente);
            }
            lista.addView(linha);
        }
    }
}

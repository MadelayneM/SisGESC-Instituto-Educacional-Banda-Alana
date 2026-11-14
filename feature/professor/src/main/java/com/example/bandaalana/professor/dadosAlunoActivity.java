package com.example.bandaalana.professor;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

// Tela de detalhes e dados do aluno (modo somente leitura), exibindo foto,
// dados pessoais, contatos de emergência e observações.
public class dadosAlunoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dados_aluno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botão voltar no topo da tela
        findViewById(R.id.btnVoltarAluno).setOnClickListener(v -> finish());

        // Recupera os dados do aluno passados pela Intent da tela anterior (lista de alunos)
        String nome = getIntent().getStringExtra("nome");
        String fotoUrl = getIntent().getStringExtra("fotoUrl");
        String nomeTurma = getIntent().getStringExtra("nomeTurma");
        String nomeCurso = getIntent().getStringExtra("nomeCurso");

        // Referências das views no layout activity_dados_aluno.xml
        TextView txtNome = findViewById(R.id.txtNomeDetalheAluno);
        TextView txtCurso = findViewById(R.id.txtCursoDetalheAluno);
        TextView txtInstrumentoBanner = findViewById(R.id.txtInstrumentoBanner);
        TextView txtDataNascimento = findViewById(R.id.txtDataNascimentoAluno);
        TextView txtInstrumento = findViewById(R.id.txtInstrumentoAluno);
        TextView txtTurmaDetalhe = findViewById(R.id.txtTurmaDetalhe);
        TextView txtMae = findViewById(R.id.txtMaeAluno);
        TextView txtTelMae = findViewById(R.id.txtTelefoneMae);
        TextView txtPai = findViewById(R.id.txtPaiAluno);
        TextView txtTelPai = findViewById(R.id.txtTelefonePai);
        TextView txtObs = findViewById(R.id.txtObservacoesAluno);
        ImageView imgFoto = findViewById(R.id.imgFotoDetalheAluno);

        // Preenche os campos principais com os dados recebidos
        if (nome != null) txtNome.setText(nome);
        if (nomeCurso != null) {
            txtCurso.setText(nomeCurso);
            txtInstrumento.setText(nomeCurso);
            txtInstrumentoBanner.setText("🎵 " + nomeCurso);
        }
        if (nomeTurma != null) {
            txtTurmaDetalhe.setText(nomeTurma);
        }

        // Dados complementares de exemplo/mockup para contatos e observações
        txtDataNascimento.setText("12/03/2014");
        txtMae.setText("Patrícia Costa");
        txtTelMae.setText("(11) 98001-1111");
        txtPai.setText("Ricardo Costa");
        txtTelPai.setText("(11) 97002-2222");
        txtObs.setText("Aluna dedicada, participa ativamente.");

        // Carrega a foto do aluno usando Glide (se houver URL válida)
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this).load(fotoUrl).circleCrop().into(imgFoto);
        }
    }
}

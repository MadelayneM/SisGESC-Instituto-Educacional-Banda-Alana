package com.example.sisgesc;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

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

        findViewById(R.id.btnVoltarAluno).setOnClickListener(v -> finish());

        String nome = getIntent().getStringExtra("nome");
        String fotoUrl = getIntent().getStringExtra("fotoUrl");
        String nomeTurma = getIntent().getStringExtra("nomeTurma");
        String nomeCurso = getIntent().getStringExtra("nomeCurso");

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

        if (nome != null) txtNome.setText(nome);
        if (nomeCurso != null) {
            txtCurso.setText(nomeCurso);
            txtInstrumento.setText(nomeCurso);
            txtInstrumentoBanner.setText("🎵 " + nomeCurso);
        }
        if (nomeTurma != null) {
            txtTurmaDetalhe.setText(nomeTurma);
        }

        // Placeholder data for demonstration as in Figma mockup
        txtDataNascimento.setText("12/03/2014");
        txtMae.setText("Patrícia Costa");
        txtTelMae.setText("(11) 98001-1111");
        txtPai.setText("Ricardo Costa");
        txtTelPai.setText("(11) 97002-2222");
        txtObs.setText("Aluna dedicada, participa ativamente.");

        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this).load(fotoUrl).circleCrop().into(imgFoto);
        }
    }
}

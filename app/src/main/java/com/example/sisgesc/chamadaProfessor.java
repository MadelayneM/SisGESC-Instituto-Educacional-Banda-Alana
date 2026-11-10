package com.example.sisgesc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sisgesc.DTOs.ChamadaAluno;
import com.example.sisgesc.DTOs.FormatoData;
import com.example.sisgesc.Repository.ProfessorRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

public class chamadaProfessor extends AppCompatActivity {

    private String nomeTurma;
    private String dataAula;
    private ProfessorRepository repo;
    private List<ChamadaAluno> listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chamada_professor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeTurma = getIntent().getStringExtra("nomeTurma");
        dataAula = getIntent().getStringExtra("dataAula");
        repo = new ProfessorRepository();

        findViewById(R.id.btnVoltarChamada).setOnClickListener(v -> finish());

        TextView txtTurma = findViewById(R.id.txtTurmaChamada);
        TextView txtData = findViewById(R.id.txtDataChamada);
        Button btnSalvar = findViewById(R.id.btnSalvarChamada);

        if (nomeTurma != null) txtTurma.setText(nomeTurma);
        if (dataAula != null) txtData.setText("📅 " + FormatoData.diaComSemana(dataAula));

        carregarChamada();

        btnSalvar.setOnClickListener(v -> salvarChamada());
    }

    private void carregarChamada() {
        repo.buscarChamada(nomeTurma, dataAula, new RepositoryCallback<List<ChamadaAluno>>() {
            @Override
            public void onSucesso(List<ChamadaAluno> alunos) {
                listaAlunos = alunos;
                mostrarChamada(alunos);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(chamadaProfessor.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarChamada(List<ChamadaAluno> alunos) {
        LinearLayout lista = findViewById(R.id.listaChamada);
        lista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ChamadaAluno aluno : alunos) {
            View linha = inflater.inflate(R.layout.item_chamada_aluno_professor, lista, false);
            ImageView imgFoto = linha.findViewById(R.id.imgFotoChamada);
            TextView txtNome = linha.findViewById(R.id.txtNomeChamada);
            RadioButton rbPresente = linha.findViewById(R.id.rbPresente);
            RadioButton rbAusente = linha.findViewById(R.id.rbAusente);
            EditText edtObs = linha.findViewById(R.id.edtObservacao);

            txtNome.setText(aluno.getNome());

            if (aluno.getFotoUrl() != null && !aluno.getFotoUrl().isEmpty()) {
                Glide.with(this).load(aluno.getFotoUrl()).circleCrop().into(imgFoto);
            }

            if (aluno.getPresente() != null) {
                if (aluno.getPresente()) {
                    rbPresente.setChecked(true);
                } else {
                    rbAusente.setChecked(true);
                }
            }

            if (aluno.getObservacao() != null) {
                edtObs.setText(aluno.getObservacao());
            }

            lista.addView(linha);
        }

        View txtSemAlunos = findViewById(R.id.txtSemAlunosChamada);
        if (txtSemAlunos != null) {
            txtSemAlunos.setVisibility(alunos.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void salvarChamada() {
        if (listaAlunos == null || listaAlunos.isEmpty()) return;

        LinearLayout listaChamada = findViewById(R.id.listaChamada);
        for (int i = 0; i < listaAlunos.size(); i++) {
            View linha = listaChamada.getChildAt(i);
            if (linha == null) continue;
            ChamadaAluno aluno = listaAlunos.get(i);
            RadioButton rbPresente = linha.findViewById(R.id.rbPresente);
            RadioButton rbAusente = linha.findViewById(R.id.rbAusente);
            EditText edtObs = linha.findViewById(R.id.edtObservacao);

            if (rbPresente.isChecked()) {
                aluno.setPresente(true);
            } else if (rbAusente.isChecked()) {
                aluno.setPresente(false);
            } else {
                aluno.setPresente(null);
            }

            if (edtObs != null) {
                aluno.setObservacao(edtObs.getText().toString());
            }
        }

        repo.salvarChamada(dataAula, listaAlunos, new RepositoryCallback<Void>() {
            @Override
            public void onSucesso(Void unused) {
                Toast.makeText(chamadaProfessor.this, "Chamada salva!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(chamadaProfessor.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }
}

package com.example.sisgesc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.example.sisgesc.DTOs.ProfessorAluno;
import com.example.sisgesc.Repository.ProfessorRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

// Tela "Alunos" do Professor: exibe a lista de alunos (geral ou filtrada por turma)
// com campo de pesquisa em tempo real e clique para visualizar os detalhes do aluno.
public class alunosProfessor extends AppCompatActivity {

    private String cpf;
    private String nomeTurma;
    private ProfessorRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alunos_professor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configura o menu inferior do professor
        MenuProfessor.configurar(this, R.id.navAlunos);

        cpf = getIntent().getStringExtra(MainActivity.EXTRA_CPF);
        nomeTurma = getIntent().getStringExtra("nomeTurma");
        repo = new ProfessorRepository();

        EditText pesquisarAluno = findViewById(R.id.pesquisarAluno);
        TextView txtFiltroTurma = findViewById(R.id.txtFiltroTurma);

        // Se a tela foi aberta a partir de "Ver alunos" de uma turma específica, exibe o filtro
        if (nomeTurma != null && !nomeTurma.isEmpty()) {
            txtFiltroTurma.setVisibility(View.VISIBLE);
            txtFiltroTurma.setText("Turma: " + nomeTurma);
        } else {
            txtFiltroTurma.setVisibility(View.GONE);
        }

        // Listener de texto para buscar alunos em tempo real conforme o professor digita
        pesquisarAluno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                carregarAlunos(s.toString());
            }
        });

        carregarAlunos(null);
    }

    // Busca os alunos no Supabase (geral ou filtrado por turma e texto de busca)
    private void carregarAlunos(String busca) {
        RepositoryCallback<List<ProfessorAluno>> callback = new RepositoryCallback<List<ProfessorAluno>>() {
            @Override
            public void onSucesso(List<ProfessorAluno> alunos) {
                mostrarAlunos(alunos);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(alunosProfessor.this, mensagem, Toast.LENGTH_LONG).show();
            }
        };

        if (nomeTurma != null && !nomeTurma.isEmpty()) {
            repo.buscarAlunos(cpf, nomeTurma, busca, callback);
        } else {
            repo.buscarAlunos(cpf, busca, callback);
        }
    }

    // Infla as linhas de cada aluno na lista da tela
    private void mostrarAlunos(List<ProfessorAluno> alunos) {
        LinearLayout lista = findViewById(R.id.listaAlunos);
        lista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ProfessorAluno aluno : alunos) {
            View linha = inflater.inflate(R.layout.item_aluno_professor, lista, false);
            ImageView imgFoto = linha.findViewById(R.id.imgFotoAluno);
            TextView txtNome = linha.findViewById(R.id.txtNomeAluno);
            TextView txtTurma = linha.findViewById(R.id.txtTurmaAluno);

            txtNome.setText(aluno.getNome());
            txtTurma.setText(aluno.getNomeTurma() + " • " + aluno.getNomeCurso());

            // Carrega a foto do aluno com Glide, se houver URL
            if (aluno.getFotoUrl() != null && !aluno.getFotoUrl().isEmpty()) {
                Glide.with(this).load(aluno.getFotoUrl()).circleCrop().into(imgFoto);
            }

            // Clique na linha do aluno abre a tela de detalhes (dadosAlunoActivity)
            linha.setOnClickListener(v -> {
                Intent i = new Intent(this, dadosAlunoActivity.class);
                i.putExtra(MainActivity.EXTRA_CPF, cpf);
                i.putExtra("nome", aluno.getNome());
                i.putExtra("fotoUrl", aluno.getFotoUrl());
                i.putExtra("nomeTurma", aluno.getNomeTurma());
                i.putExtra("nomeCurso", aluno.getNomeCurso());
                startActivity(i);
            });

            lista.addView(linha);
        }

        // Exibe mensagem caso nenhum aluno seja encontrado
        View txtSemAlunos = findViewById(R.id.txtSemAlunos);
        if (txtSemAlunos != null) {
            txtSemAlunos.setVisibility(alunos.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}

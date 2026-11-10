package com.example.sisgesc;

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

import com.example.sisgesc.Adapters.AdminAlunoAdapter;
import com.example.sisgesc.DTOs.AdminAluno;
import com.example.sisgesc.DTOs.AdminResumo;
import com.example.sisgesc.Repository.AdminRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import java.util.List;

// Início do Administrador: "Visão geral" (AdminRepository.buscarResumo),
// atalhos de "Gerenciamento" e "Alunos recentes" (buscarAlunosRecentes).
public class DashboardActivity extends AppCompatActivity {

    private static final int QTD_ALUNOS_RECENTES = 5;

    private final AdminRepository repository = new AdminRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_admin_dashboard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAdmin.configurar(this, R.id.navInicio);

        // Atalhos de "Gerenciamento"
        findViewById(R.id.cardCadastrarAluno).setOnClickListener(v ->
                FormularioAdmin.abrir(this, activity_admin_novo_aluno.class, null, null));
        findViewById(R.id.cardNovaTurma).setOnClickListener(v ->
                FormularioAdmin.abrir(this, activity_admin_nova_turma.class, null, null));
        findViewById(R.id.cardCadastrarProfessor).setOnClickListener(v ->
                FormularioAdmin.abrir(this, activity_admin_novo_membro.class, null, null));
        findViewById(R.id.cardConsultarFrequencia).setOnClickListener(v ->
                FormularioAdmin.abrir(this, activity_admin_frequencia.class, null, null));
    }

    // Recarrega ao voltar de um cadastro, para os números e a lista ficarem atualizados
    @Override
    protected void onResume() {
        super.onResume();
        String cpf = getIntent().getStringExtra(MainActivity.EXTRA_CPF);

        repository.buscarResumo(cpf, new RepositoryCallback<AdminResumo>() {
            @Override
            public void onSucesso(AdminResumo resumo) {
                mostrarResumo(resumo);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DashboardActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });

        repository.buscarAlunosRecentes(QTD_ALUNOS_RECENTES, new RepositoryCallback<List<AdminAluno>>() {
            @Override
            public void onSucesso(List<AdminAluno> alunos) {
                mostrarAlunosRecentes(alunos);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DashboardActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarResumo(AdminResumo resumo) {
        if (resumo.getPrimeiroNome() != null) {
            ((TextView) findViewById(R.id.tvNomeUsuario)).setText(resumo.getPrimeiroNome() + "! 🎓");
        }
        ((TextView) findViewById(R.id.tvTotalAlunos)).setText(String.valueOf(resumo.getTotalAlunos()));
        ((TextView) findViewById(R.id.tvTotalTurmas)).setText(String.valueOf(resumo.getTotalTurmas()));
        ((TextView) findViewById(R.id.tvTotalProfessores)).setText(String.valueOf(resumo.getTotalProfessores()));
        ((TextView) findViewById(R.id.tvFrequenciaGeral)).setText(resumo.getFrequenciaTexto());
    }

    // A lista é curta e fica dentro do ScrollView, por isso usa LinearLayout em vez de RecyclerView
    private void mostrarAlunosRecentes(List<AdminAluno> alunos) {
        LinearLayout lista = findViewById(R.id.llAlunosRecentes);
        lista.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (AdminAluno aluno : alunos) {
            View linha = inflater.inflate(R.layout.activity_item_admin_aluno_recente, lista, false);
            AdminAlunoAdapter.preencher(linha, aluno);
            linha.setOnClickListener(v -> FormularioAdmin.abrir(this, activity_admin_novo_aluno.class,
                    FormularioAdmin.EXTRA_CPF_ALUNO, aluno.getCpf()));
            lista.addView(linha);
        }
    }
}

package com.example.sisgesc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Formulário de turma: cadastrar (sem extra) ou editar (com FormularioAdmin.EXTRA_NOME_TURMA).
//
// Para o back-end:
//   spCurso            AdminRepository.buscarOpcoesCursos       -> FormularioAdmin.preencherSpinner
//   spProfessor        AdminRepository.buscarOpcoesProfessores  (incluir uma opção "Sem professor")
//   spFuncaoProfessor  AdminRepository.buscarOpcoesTiposMonitor
//   btnCriarTurma      montar CadastroTurma -> criarTurma (novo) ou editarTurma (edição)
//   btnExcluir         AdminRepository.excluirTurma(nomeTurma)
//   Edição: AdminRepository.buscarCadastroTurma(nomeTurma) e preencher os campos.
//   Datas na tela são dd/mm/aaaa: converter com FormularioAdmin.dataParaBanco / dataParaTela.
public class activity_admin_nova_turma extends AppCompatActivity {

    // Nome da turma em edição, ou null quando é um cadastro novo
    private String nomeTurmaEdicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_nova_turma);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeTurmaEdicao = getIntent().getStringExtra(FormularioAdmin.EXTRA_NOME_TURMA);
        boolean edicao = nomeTurmaEdicao != null;

        FormularioAdmin.configurarCabecalho(this, edicao ? "Editar turma" : "Nova turma");

        if (edicao) {
            // O nome é a chave da turma no banco e não pode mudar
            findViewById(R.id.etNomeTurma).setEnabled(false);
            ((Button) findViewById(R.id.btnCriarTurma)).setText("Salvar alterações");
            findViewById(R.id.btnExcluir).setVisibility(View.VISIBLE);
        }
    }
}

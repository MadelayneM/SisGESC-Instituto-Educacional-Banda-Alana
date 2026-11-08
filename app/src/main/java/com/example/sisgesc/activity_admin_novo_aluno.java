package com.example.sisgesc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sisgesc.DTOs.AdminPerfil;
import com.example.sisgesc.DTOs.CadastroAluno;

// Formulário de aluno: cadastrar (sem extra) ou editar (com FormularioAdmin.EXTRA_CPF_ALUNO).
//
// Para o back-end:
//   spTurma        AdminRepository.buscarOpcoesTurmas -> FormularioAdmin.preencherSpinner
//   btnCadastrar   montar CadastroAluno -> cadastrarAluno (novo) ou editarAluno (edição)
//                  (o status escolhido está em getStatusSelecionado())
//   btnExcluir     AdminRepository.excluirAluno(cpf)
//   flFoto         escolher imagem -> AdminRepository.atualizarFoto (opcional)
//   Edição: AdminRepository.buscarCadastroAluno(cpf), preencher os campos com
//           FormularioAdmin.selecionar / dataParaTela e chamar selecionarStatus(...).
//   CPF e datas: converter com FormularioAdmin.somenteDigitos e dataParaBanco.
public class activity_admin_novo_aluno extends AppCompatActivity {

    // CPF do aluno em edição, ou null quando é um cadastro novo
    private String cpfEdicao;
    private String statusSelecionado = "Ativo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_novo_aluno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cpfEdicao = getIntent().getStringExtra(FormularioAdmin.EXTRA_CPF_ALUNO);
        boolean edicao = cpfEdicao != null;

        FormularioAdmin.configurarCabecalho(this, edicao ? "Editar aluno" : "Cadastrar aluno");

        // Spinners com os valores fixos dos enums do banco
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spPeriodoEscola), CadastroAluno.PERIODOS_ESCOLA);
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spTipoTelefone), AdminPerfil.TIPOS_TELEFONE);
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spGrauParentesco), CadastroAluno.GRAUS_PARENTESCO);
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spPeriodo), CadastroAluno.PERIODOS);

        // Status da matrícula: os dois botões funcionam como uma escolha única
        findViewById(R.id.statusAtivo).setOnClickListener(v -> selecionarStatus("Ativo"));
        findViewById(R.id.statusInativo).setOnClickListener(v -> selecionarStatus("Inativo"));
        selecionarStatus(statusSelecionado);

        if (edicao) {
            // O CPF não pode mudar (o banco não deixa)
            findViewById(R.id.etCpf).setEnabled(false);
            ((Button) findViewById(R.id.btnCadastrar)).setText("Salvar alterações");
            findViewById(R.id.btnExcluir).setVisibility(View.VISIBLE);
        }
    }

    // status é um de CadastroAluno.STATUS_MATRICULA
    void selecionarStatus(String status) {
        statusSelecionado = status;
        findViewById(R.id.statusAtivo).setSelected("Ativo".equals(status));
        findViewById(R.id.statusInativo).setSelected("Inativo".equals(status));
    }

    String getStatusSelecionado() {
        return statusSelecionado;
    }
}

package com.example.sisgesc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sisgesc.DTOs.AdminOpcao;
import com.example.sisgesc.DTOs.AdminPerfil;
import com.example.sisgesc.DTOs.CadastroMembro;

import java.util.ArrayList;
import java.util.List;

// Formulário de membro da equipe: cadastrar (sem extra) ou editar (com FormularioAdmin.EXTRA_CPF_MEMBRO).
//
// Para o back-end:
//   spCargo             AdminRepository.buscarOpcoesCargos -> FormularioAdmin.preencherSpinner
//   Linhas de turma     buscarOpcoesTurmas e buscarOpcoesTiposMonitor -> setOpcoesTurmas(...)
//                       (cada linha nova já recebe essas opções)
//   btnCadastrarMembro  montar CadastroMembro (turmas com lerTurmas) -> cadastrarMembro / editarMembro
//   btnDesligar         AdminRepository.desligarMembro(cpf)
//   btnExcluir          AdminRepository.excluirMembro(cpf)
//   Edição: AdminRepository.buscarCadastroMembro(cpf), preencher os campos e chamar
//           adicionarLinhaTurma(nomeTurma, codigoTipoMonitor) para cada turma.
public class activity_admin_novo_membro extends AppCompatActivity {

    // CPF do membro em edição, ou null quando é um cadastro novo
    private String cpfEdicao;
    private List<AdminOpcao> opcoesTurmas = new ArrayList<>();
    private List<AdminOpcao> opcoesFuncoes = new ArrayList<>();
    private LinearLayout llTurmas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_novo_membro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cpfEdicao = getIntent().getStringExtra(FormularioAdmin.EXTRA_CPF_MEMBRO);
        boolean edicao = cpfEdicao != null;

        FormularioAdmin.configurarCabecalho(this, edicao ? "Editar membro" : "Novo membro");
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spTipoTelefone), AdminPerfil.TIPOS_TELEFONE);

        llTurmas = findViewById(R.id.llTurmas);
        findViewById(R.id.btnAdicionarTurma).setOnClickListener(v -> adicionarLinhaTurma(null, null));

        if (edicao) {
            // O CPF não pode mudar (o banco não deixa)
            findViewById(R.id.etCpf).setEnabled(false);
            ((Button) findViewById(R.id.btnCadastrarMembro)).setText("Salvar alterações");
            findViewById(R.id.btnDesligar).setVisibility(View.VISIBLE);
            findViewById(R.id.btnExcluir).setVisibility(View.VISIBLE);
        }
    }

    // Opções dos Spinners de cada linha de turma (vêm do banco)
    void setOpcoesTurmas(List<AdminOpcao> turmas, List<AdminOpcao> funcoes) {
        opcoesTurmas = turmas;
        opcoesFuncoes = funcoes;
        for (int i = 0; i < llTurmas.getChildCount(); i++) {
            View linha = llTurmas.getChildAt(i);
            FormularioAdmin.preencherSpinner((Spinner) linha.findViewById(R.id.spTurmaMembro), opcoesTurmas);
            FormularioAdmin.preencherSpinner((Spinner) linha.findViewById(R.id.spFuncaoMembro), opcoesFuncoes);
        }
    }

    // Uma linha "turma + função" (activity_item_admin_turma_membro). Valores null = linha vazia.
    void adicionarLinhaTurma(String nomeTurma, String codigoTipoMonitor) {
        View linha = LayoutInflater.from(this).inflate(R.layout.activity_item_admin_turma_membro, llTurmas, false);
        Spinner spTurma = linha.findViewById(R.id.spTurmaMembro);
        Spinner spFuncao = linha.findViewById(R.id.spFuncaoMembro);
        FormularioAdmin.preencherSpinner(spTurma, opcoesTurmas);
        FormularioAdmin.preencherSpinner(spFuncao, opcoesFuncoes);
        FormularioAdmin.selecionar(spTurma, nomeTurma);
        FormularioAdmin.selecionar(spFuncao, codigoTipoMonitor);

        linha.findViewById(R.id.btnRemoverTurma).setOnClickListener(v -> llTurmas.removeView(linha));
        llTurmas.addView(linha);
    }

    // Passa as linhas da tela para o CadastroMembro (substitui as turmas que ele tinha)
    void lerTurmas(CadastroMembro membro) {
        membro.limparTurmas();
        for (int i = 0; i < llTurmas.getChildCount(); i++) {
            View linha = llTurmas.getChildAt(i);
            Object turma = ((Spinner) linha.findViewById(R.id.spTurmaMembro)).getSelectedItem();
            Object funcao = ((Spinner) linha.findViewById(R.id.spFuncaoMembro)).getSelectedItem();
            if (turma instanceof AdminOpcao && funcao instanceof AdminOpcao) {
                membro.adicionarTurma(((AdminOpcao) turma).getValor(), ((AdminOpcao) funcao).getValor());
            }
        }
    }
}

package com.example.sisgesc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sisgesc.DTOs.AdminOpcao;
import com.example.sisgesc.DTOs.AdminPerfil;
import com.example.sisgesc.DTOs.CadastroMembro;
import com.example.sisgesc.Repository.AdminRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

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

    private Spinner spCargo;
    private LinearLayout llTurmas;
    private AdminRepository repo;
    private CadastroMembro membroAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_novo_membro);

        spCargo = findViewById(R.id.spCargo);
        repo = new AdminRepository();
        llTurmas = findViewById(R.id.llTurmas);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cpfEdicao = getIntent().getStringExtra(FormularioAdmin.EXTRA_CPF_MEMBRO);
        boolean edicao = cpfEdicao != null;

        FormularioAdmin.configurarCabecalho(this, edicao ? "Editar membro" : "Novo membro");
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spTipoTelefone), AdminPerfil.TIPOS_TELEFONE);

        findViewById(R.id.btnAdicionarTurma).setOnClickListener(v -> adicionarLinhaTurma(null, null));

        if (edicao) {
            // O CPF não pode mudar (o banco não deixa)
            findViewById(R.id.etCpf).setEnabled(false);
            ((Button) findViewById(R.id.btnCadastrarMembro)).setText("Salvar alterações");
            findViewById(R.id.btnDesligar).setVisibility(View.VISIBLE);
            findViewById(R.id.btnExcluir).setVisibility(View.VISIBLE);
        }

        // 1. Carregar spCargo
        repo.buscarOpcoesCargos(new RepositoryCallback<List<AdminOpcao>>() {
            @Override
            public void onSucesso(List<AdminOpcao> opcoesCargos) {
                FormularioAdmin.preencherSpinner(spCargo, opcoesCargos);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(getApplicationContext(),
                        "Erro ao carregar cargos: " + mensagem,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Opções das linhas (turmas -> tipos -> edição)
        repo.buscarOpcoesTurmas(new RepositoryCallback<List<AdminOpcao>>() {
            @Override public void onSucesso(List<AdminOpcao> turmas) {
                opcoesTurmas = turmas;
                carregarTipos();
            }
            @Override public void onErro(String msg) { erro("turmas", msg); }
        });

        // =========================================
        //  CONFIGURAÇÃO DOS BOTÕES NO onCreate()
        // ==========================================
        // Cole esta parte dentro do seu onCreate(), logo após configurar os spinners:


    // 2. DatePicker para Data de Nascimento
    FormularioAdmin.configurarDatePicker(this, findViewById(R.id.etNascimento));

    // 3. Botão de Cadastrar / Salvar alterações
    findViewById(R.id.btnCadastrarMembro).setOnClickListener(v -> salvarOuEditarMembro(edicao));

    // 4. Botões de Desligar e Excluir
    findViewById(R.id.btnDesligar).setOnClickListener(v -> confirmarDesligarMembro());
    findViewById(R.id.btnExcluir).setOnClickListener(v -> confirmarExclusaoMembro());
    }

    private void carregarTipos() {
        repo.buscarOpcoesTiposMonitor(new RepositoryCallback<List<AdminOpcao>>() {
            @Override public void onSucesso(List<AdminOpcao> tipos) {
                opcoesFuncoes = tipos;
                if (cpfEdicao != null) carregarEdicao();
                else adicionarLinhaTurma(null, null); // linha vazia no cadastro novo
            }
            @Override public void onErro(String msg) { msg("tipos de monitor", msg); }
        });
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

    private void msg(String context, String mensagem) {
        Toast.makeText(this, "Erro ao carregar " + context + ": " + mensagem, Toast.LENGTH_SHORT).show();
    }

    private void erro(String context, String mensagem) {
        msg(context, mensagem);
    }



    // =========================================
    //  MÉTODO carregarEdicao (Preencher campos)
    // ==========================================
    private void carregarEdicao() {
        repo.buscarCadastroMembro(cpfEdicao, new RepositoryCallback<CadastroMembro>() {
            @Override
            public void onSucesso(CadastroMembro cadastro) {
                // Guarda ou usa este 'membroDoBanco' na activity
                membroAtual = cadastro;
                // Preencher campos de texto do membro
                ((EditText) findViewById(R.id.etNome)).setText(cadastro.getPrimeiroNome());
                ((EditText) findViewById(R.id.etSobrenome)).setText(cadastro.getSobrenome());
                ((EditText) findViewById(R.id.etCpf)).setText(cadastro.getCpf());
                ((EditText) findViewById(R.id.etEmail)).setText(cadastro.getEmail());
                ((EditText) findViewById(R.id.etTelefone)).setText(cadastro.getNumeroTelefone());
                FormularioAdmin.selecionar((Spinner) findViewById(R.id.spTipoTelefone), cadastro.getTipoTelefone());
                ((EditText) findViewById(R.id.etNascimento)).setText(FormularioAdmin.dataParaTela(cadastro.getDataNascimento()));

                // Selecionar o cargo no Spinner
                FormularioAdmin.selecionar(spCargo, cadastro.getCodigoCargo());

                // Adicionar as linhas de turmas vinculadas ao membro
                llTurmas.removeAllViews(); // Limpa linha vazia padrão se houver
                if (cadastro.getTurmas() != null && !cadastro.getTurmas().isEmpty()) {
                    for (CadastroMembro.Turma t : cadastro.getTurmas()) {
                        adicionarLinhaTurma(t.getNomeTurma(), t.getCodigoTipoMonitor());
                    }
                } else {
                    adicionarLinhaTurma(null, null);
                }
            }

            @Override
            public void onErro(String msg) {
                msg("cadastro", msg);
            }
        });
    }

    // =========================================
    //  MÉTODO salvarOuEditarMembro
    // ==========================================
    private void salvarOuEditarMembro(boolean edicao) {
        EditText etPrimeiroNome = findViewById(R.id.etNome);
        EditText etSobrenome = findViewById(R.id.etSobrenome);

        EditText etCpf = findViewById(R.id.etCpf);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etDdd = findViewById(R.id.etDdd);
        EditText etTelefone = findViewById(R.id.etTelefone);
        Spinner spTipoTelefone = findViewById(R.id.spTipoTelefone);
        EditText etNascimento = findViewById(R.id.etNascimento);

        String ddd = etDdd.getText().toString().trim();
        String primeiroNome = etPrimeiroNome.getText().toString().trim();
        String sobrenome = etSobrenome.getText().toString().trim();
        String cpf = etCpf.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String tipoTel = spTipoTelefone.getSelectedItem() != null ? spTipoTelefone.getSelectedItem().toString() : null;
        String dataNascBanco = FormularioAdmin.dataParaBanco(etNascimento.getText().toString().trim());
        EditText etSenha = findViewById(R.id.etSenha); // Ajuste o ID conforme o seu XML se necessário
        String senha = etSenha.getText().toString().trim();


        // Validação obrigatória para nome e sobrenome
        if (primeiroNome.isEmpty()) {
            etPrimeiroNome.setError("Informe o primeiro nome");
            etPrimeiroNome.requestFocus();
            return;
        }

        if (sobrenome.isEmpty()) {
            etSobrenome.setError("Informe o sobrenome");
            etSobrenome.requestFocus();
            return;
        }

        if (cpf.isEmpty()) {
            etCpf.setError("Informe o CPF");
            etCpf.requestFocus();
            return;
        }
        // Validação da senha (obrigatória apenas no cadastro)
        if (!edicao && senha.isEmpty()) {
            etSenha.setError("Defina a senha de acesso");
            etSenha.requestFocus();
            return;
        }

// Passar para o DTO
        CadastroMembro membro = null;
        //membro.setSenha(senha);

        membro = new CadastroMembro();
        membro.setPrimeiroNome(primeiroNome);
        membro.setSobrenome(sobrenome);

        membro.setCpf(cpf);
        membro.setEmail(email);
        //membro.setTelefone(telefone);
        String numeroTelefone = telefone;
        membro.setTelefone(ddd, numeroTelefone, tipoTel);

        membro.setDataNascimento(dataNascBanco);

        // Obter o cargo selecionado do Spinner
        Object cargoObj = spCargo.getSelectedItem();
        if (cargoObj instanceof AdminOpcao) {
            membro.setCodigoCargo(((AdminOpcao) cargoObj).getValor());
        }

        // Ler as turmas dinâmicas da tela
        lerTurmas(membro);

        if (edicao) {
            // MODO EDIÇÃO: Usa o objeto já carregado do banco (membroAtual)
            if (membroAtual == null) {
                Toast.makeText(this, "Erro: dados do membro não carregados.", Toast.LENGTH_SHORT).show();
                return;
            }

            membroAtual.setPrimeiroNome(primeiroNome);
            membroAtual.setSobrenome(sobrenome);
            membroAtual.setEmail(email);
            membroAtual.setTelefone(ddd, numeroTelefone, tipoTel);
            membroAtual.setDataNascimento(dataNascBanco);

            cargoObj = spCargo.getSelectedItem();
            if (cargoObj instanceof AdminOpcao) {
                membroAtual.setCodigoCargo(((AdminOpcao) cargoObj).getValor());
            }

            // Atualiza a senha apenas se o usuário digitou uma nova
            /*if (!senha.isEmpty()) {
                membroAtual.setSenha(senha);
            }*/

            membroAtual.limparTurmas();
            lerTurmas(membroAtual);
            repo.editarMembro(membroAtual, new RepositoryCallback<Void>() {
                @Override
                public void onSucesso(Void resultado) {
                    Toast.makeText(activity_admin_novo_membro.this, "Membro atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onErro(String mensagem) {
                    Toast.makeText(activity_admin_novo_membro.this, "Erro ao atualizar: " + mensagem, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // MODO CADASTRO NOVO: Cria um novo objeto do zero
            CadastroMembro membroNovo = new CadastroMembro();
            membroNovo.setPrimeiroNome(primeiroNome);
            membroNovo.setSobrenome(sobrenome);
            membroNovo.setCpf(cpf);
            membroNovo.setEmail(email);
            membroNovo.setSenha(senha);
            membroNovo.setTelefone(ddd, numeroTelefone, tipoTel);
            membroNovo.setDataNascimento(dataNascBanco);

            cargoObj = spCargo.getSelectedItem();
            if (cargoObj instanceof AdminOpcao) {
                membroNovo.setCodigoCargo(((AdminOpcao) cargoObj).getValor());
            }

            lerTurmas(membroNovo);
            repo.cadastrarMembro(membroNovo, new RepositoryCallback<Void>() {
                @Override
                public void onSucesso(Void resultado) {
                    Toast.makeText(activity_admin_novo_membro.this, "Membro cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onErro(String mensagem) {
                    Toast.makeText(activity_admin_novo_membro.this, "Erro ao cadastrar: " + mensagem, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    // =========================================
    //  MÉTODOS DE DESLIGAR E EXCLUIR
    // ==========================================
    private void confirmarDesligarMembro() {
        if (cpfEdicao == null) return;

        new android.app.AlertDialog.Builder(this)
                .setTitle("Desligar membro")
                .setMessage("O desligamento remove o membro das turmas e bloqueia o acesso ao app, mas preserva todo o histórico de atividades.\n\nEsta é a opção recomendada para o dia a dia.\nDeseja continuar?")
                .setPositiveButton("Sim, desligar", (dialog, which) -> {
                    repo.desligarMembro(cpfEdicao, new RepositoryCallback<Void>() {
                        @Override
                        public void onSucesso(Void resultado) {
                            Toast.makeText(activity_admin_novo_membro.this, "Membro desligado com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onErro(String mensagem) {
                            Toast.makeText(activity_admin_novo_membro.this, "Erro ao desligar: " + mensagem, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarExclusaoMembro() {
        if (cpfEdicao == null) return;

        new android.app.AlertDialog.Builder(this)
                .setTitle("ATENÇÃO: Excluir membro")
                .setMessage("A exclusão apaga permanentemente todos os dados e o histórico do membro.\n\n⚠️ Nota: Se o membro já possuir avisos, avaliações ou registos associados, o banco de dados impedirá a exclusão. Nesses casos, utilize a opção **Desligar**.\n\nDeseja realmente excluir tudo?")
                .setPositiveButton("Excluir definitivamente", (dialog, which) -> {
                    repo.excluirMembro(cpfEdicao, new RepositoryCallback<Void>() {
                        @Override
                        public void onSucesso(Void resultado) {
                            Toast.makeText(activity_admin_novo_membro.this, "Membro excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onErro(String mensagem) {
                            Toast.makeText(activity_admin_novo_membro.this, "Não foi possível excluir (há histórico vinculado). Utilize a opção Desligar. Erro: " + mensagem, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

package com.example.bandaalana.administrador;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bandaalana.administrador.DTOs.AdminOpcao;
import com.example.bandaalana.administrador.DTOs.AdminPerfil;
import com.example.bandaalana.administrador.DTOs.CadastroAluno;
import com.example.bandaalana.administrador.Repository.AdminRepository;
import com.example.bandaalana.administrador.Repository.RepositoryCallback;

import java.util.List;

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
    private CadastroAluno alunoEdicao = null;

    private Button btnCadastrar,btnExcluir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_novo_aluno);

        // 1. Ajuste de margens do sistema (padrão)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Captura se é modo de edição através do Intent
        cpfEdicao = getIntent().getStringExtra(FormularioAdmin.EXTRA_CPF_ALUNO);
        boolean edicao = (cpfEdicao != null);

        // Configuração visual do cabeçalho
        FormularioAdmin.configurarCabecalho(this, edicao ? "Editar aluno" : "Cadastrar aluno");

        // Preencher Spinners com os valores fixos
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spPeriodoEscola), CadastroAluno.PERIODOS_ESCOLA);
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spTipoTelefone), AdminPerfil.TIPOS_TELEFONE);
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spGrauParentesco), CadastroAluno.GRAUS_PARENTESCO);
        FormularioAdmin.preencherSpinner((Spinner) findViewById(R.id.spPeriodo), CadastroAluno.PERIODOS);

        // Configuração dos botões de Status
        findViewById(R.id.statusAtivo).setOnClickListener(v -> selecionarStatus("Ativo"));
        findViewById(R.id.statusInativo).setOnClickListener(v -> selecionarStatus("Inativo"));
        selecionarStatus(statusSelecionado);

        // Configurar DatePickers
        FormularioAdmin.configurarDatePicker(this, findViewById(R.id.etNascimento));
        FormularioAdmin.configurarDatePicker(this, findViewById(R.id.etNascimentoResponsavel));

        // Configuração específica para Modo Edição vs Cadastro Novo
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        Button btnExcluir = findViewById(R.id.btnExcluir);

        if (edicao) {
            findViewById(R.id.etCpf).setEnabled(false); // CPF não pode ser alterado
            btnCadastrar.setText("Salvar alterações");
            btnExcluir.setVisibility(View.VISIBLE);
            btnExcluir.setOnClickListener(v -> confirmarExclusao());
        } else {
            btnCadastrar.setText("Cadastrar aluno");
            btnExcluir.setVisibility(View.GONE);
            alunoEdicao = new CadastroAluno(); // Instancia um objeto vazio para novo cadastro
        }

        // Ação do Botão Principal (Cadastrar ou Salvar)
        btnCadastrar.setOnClickListener(v -> salvarOuEditarAluno(edicao));

        // Carregar turmas do banco e preencher dados se for edição
        carregarTurmas(edicao);
    }

    // Carregar turmas no Spinner spTurma
    private void carregarTurmas(boolean edicao) {
        AdminRepository repo = new AdminRepository();
        repo.buscarOpcoesTurmas(new RepositoryCallback<List<AdminOpcao>>() {
            @Override
            public void onSucesso(List<AdminOpcao> opcoesTurmas) {
                Spinner spTurma = findViewById(R.id.spTurma);
                FormularioAdmin.preencherSpinner(spTurma, opcoesTurmas);

                // Se for edição, busca os dados do aluno após as turmas estarem prontas
                if (edicao && cpfEdicao != null) {
                    carregarDadosEdicao(repo, cpfEdicao);
                }
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_novo_aluno.this, "Erro ao carregar turmas: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Carregar dados do aluno em modo edição
    private void carregarDadosEdicao(AdminRepository repo, String cpf) {
        repo.buscarCadastroAluno(cpf, new RepositoryCallback<CadastroAluno>() {
            @Override
            public void onSucesso(CadastroAluno aluno) {
                alunoEdicao = aluno; // Mantém a referência oficial vinda do banco (isCarregadoDoBanco() == true)

                // Preencher campos pessoais
                ((EditText) findViewById(R.id.etNome)).setText(aluno.getPrimeiroNome());
                ((EditText) findViewById(R.id.etSobrenome)).setText(aluno.getSobrenome());
                ((EditText) findViewById(R.id.etCpf)).setText(aluno.getCpf());
                ((EditText) findViewById(R.id.etNascimento)).setText(FormularioAdmin.dataParaTela(aluno.getDataNascimento()));

                // Escola e Contato
                ((EditText) findViewById(R.id.etEscola)).setText(aluno.getNomeEscola());
                FormularioAdmin.selecionar((Spinner) findViewById(R.id.spPeriodoEscola), aluno.getPeriodoEscola());

                ((EditText) findViewById(R.id.etDdd)).setText(aluno.getDdd());
                ((EditText) findViewById(R.id.etTelefone)).setText(aluno.getNumeroTelefone());
                FormularioAdmin.selecionar((Spinner) findViewById(R.id.spTipoTelefone), aluno.getTipoTelefone());
                ((EditText) findViewById(R.id.etEmail)).setText(aluno.getEmail());

                // Dados do Responsável
                ((EditText) findViewById(R.id.etNomeResponsavel)).setText(aluno.getPrimeiroNomeResponsavel());
                ((EditText) findViewById(R.id.etSobrenomeResponsavel)).setText(aluno.getSobrenomeResponsavel());
                ((EditText) findViewById(R.id.etCpfResponsavel)).setText(aluno.getCpfResponsavel());
                ((EditText) findViewById(R.id.etNascimentoResponsavel)).setText(FormularioAdmin.dataParaTela(aluno.getDataNascimentoResponsavel()));

                FormularioAdmin.selecionar((Spinner) findViewById(R.id.spGrauParentesco), aluno.getGrauParentesco());
                ((EditText) findViewById(R.id.etDddResponsavel)).setText(aluno.getDddResponsavel());
                ((EditText) findViewById(R.id.etTelefoneResponsavel)).setText(aluno.getNumeroTelefoneResponsavel());

                // Autorização de busca
                ((androidx.appcompat.widget.SwitchCompat) findViewById(R.id.swAutorizadoBusca)).setChecked(aluno.isAutorizadoBusca());

                // Turma, Período e Status
                FormularioAdmin.selecionar((Spinner) findViewById(R.id.spTurma), aluno.getNomeTurma());
                FormularioAdmin.selecionar((Spinner) findViewById(R.id.spPeriodo), aluno.getPeriodo());
                selecionarStatus(aluno.getStatus());
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_novo_aluno.this, "Erro ao buscar aluno: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Ação do Botão Cadastrar / Salvar Alterações
    private void salvarOuEditarAluno(boolean edicao) {
        if (edicao && alunoEdicao == null) {
            Toast.makeText(this, "Aguarde o carregamento dos dados do aluno.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Se for edição, utilizamos o objeto alunoEdicao carregado do banco. Se for novo, criamos um.
        CadastroAluno aluno = edicao ? alunoEdicao : new CadastroAluno();

        // Coletar dados dos campos da tela
        aluno.setPrimeiroNome(((EditText) findViewById(R.id.etNome)).getText().toString().trim());
        aluno.setSobrenome(((EditText) findViewById(R.id.etSobrenome)).getText().toString().trim());
        aluno.setCpf(FormularioAdmin.somenteDigitos(((EditText) findViewById(R.id.etCpf)).getText().toString()));
        aluno.setDataNascimento(FormularioAdmin.dataParaBanco(((EditText) findViewById(R.id.etNascimento)).getText().toString()));

        aluno.setNomeEscola(((EditText) findViewById(R.id.etEscola)).getText().toString().trim());
        aluno.setPeriodoEscola(((Spinner) findViewById(R.id.spPeriodoEscola)).getSelectedItem().toString());
        // Telefone e E-mail (opcionais: se apagados, o banco remove/limpa)
        String dddAluno = FormularioAdmin.somenteDigitos(((EditText) findViewById(R.id.etDdd)).getText().toString());
        String numAluno = FormularioAdmin.somenteDigitos(((EditText) findViewById(R.id.etTelefone)).getText().toString());
        String tipoTelAluno = ((Spinner) findViewById(R.id.spTipoTelefone)).getSelectedItem().toString();
        aluno.setTelefone(dddAluno, numAluno, tipoTelAluno);
        aluno.setEmail(((EditText) findViewById(R.id.etEmail)).getText().toString().trim());

        aluno.setPrimeiroNomeResponsavel(((EditText) findViewById(R.id.etNomeResponsavel)).getText().toString().trim());
        aluno.setSobrenomeResponsavel(((EditText) findViewById(R.id.etSobrenomeResponsavel)).getText().toString().trim());
        aluno.setCpfResponsavel(FormularioAdmin.somenteDigitos(((EditText) findViewById(R.id.etCpfResponsavel)).getText().toString()));
        aluno.setDataNascimentoResponsavel(FormularioAdmin.dataParaBanco(((EditText) findViewById(R.id.etNascimentoResponsavel)).getText().toString()));

        aluno.setGrauParentesco(((Spinner) findViewById(R.id.spGrauParentesco)).getSelectedItem().toString());
        String dddResp = FormularioAdmin.somenteDigitos(((EditText) findViewById(R.id.etDddResponsavel)).getText().toString());
        String numResp = FormularioAdmin.somenteDigitos(((EditText) findViewById(R.id.etTelefoneResponsavel)).getText().toString());
        aluno.setTelefoneResponsavel(dddResp, numResp);

        aluno.setAutorizadoBusca(((androidx.appcompat.widget.SwitchCompat) findViewById(R.id.swAutorizadoBusca)).isChecked());
        //aluno.setSenhaLogin(((EditText) findViewById(R.id.etSenha)).getText().toString().trim());
        //aluno.setNomeTurma(((Spinner) findViewById(R.id.spTurma)).getSelectedItem().toString());
        aluno.setPeriodo(((Spinner) findViewById(R.id.spPeriodo)).getSelectedItem().toString());
        aluno.setStatus(getStatusSelecionado());

        // Regra da Senha: obrigatória no cadastro; na edição, se estiver vazia, mantém a atual
        String senhaDigitada = ((EditText) findViewById(R.id.etSenha)).getText().toString().trim();
        if (!edicao || !senhaDigitada.isEmpty()) {
            aluno.setSenhaLogin(senhaDigitada);
        }

        if (aluno.getPrimeiroNome().isEmpty() || aluno.getSobrenome().isEmpty()) {
            Toast.makeText(this, "Preencha nome e sobrenome do aluno.", Toast.LENGTH_LONG).show();
            return;
        }
        if (aluno.getCpf().length() != 11) {
            Toast.makeText(this, "O CPF do aluno deve ter 11 dígitos.", Toast.LENGTH_LONG).show();
            return;
        }
        if (aluno.getCpfResponsavel().length() != 11) {
            Toast.makeText(this, "O CPF do responsável deve ter 11 dígitos.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!edicao && aluno.getSenhaLogin().length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_LONG).show();
            return;
        }


        // Leitura correta do Spinner de Turma vindo do BANCO (AdminOpcao)
        Spinner spTurma = findViewById(R.id.spTurma);
        if (spTurma.getSelectedItem() instanceof AdminOpcao) {
            aluno.setNomeTurma(((AdminOpcao) spTurma.getSelectedItem()).getValor());
        } else if (spTurma.getSelectedItem() != null) {
            aluno.setNomeTurma(spTurma.getSelectedItem().toString());
        }

        // Spinner de valor fixo (Período)
        aluno.setPeriodo(((Spinner) findViewById(R.id.spPeriodo)).getSelectedItem().toString());
        aluno.setStatus(getStatusSelecionado());

        // Evitar clique duplo: desliga o botão antes de chamar o repositório
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        btnCadastrar.setEnabled(false);

        AdminRepository repo = new AdminRepository();

        if (!edicao) {
            repo.cadastrarAluno(aluno, new RepositoryCallback<Void>() {
                @Override
                public void onSucesso(Void resultado) {
                    Toast.makeText(activity_admin_novo_aluno.this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onErro(String mensagem) {
                    btnCadastrar.setEnabled(true);
                    android.util.Log.e("SisGESC", "Falha ao salvar aluno: " + mensagem);
                    Toast.makeText(activity_admin_novo_aluno.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            repo.editarAluno(aluno, new RepositoryCallback<Void>() {
                @Override
                public void onSucesso(Void resultado) {
                    Toast.makeText(activity_admin_novo_aluno.this, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onErro(String mensagem) {
                    btnCadastrar.setEnabled(true);
                    android.util.Log.e("SisGESC", "Falha ao salvar aluno: " + mensagem);
                    Toast.makeText(activity_admin_novo_aluno.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Ação do Botão Excluir com Diálogo de Confirmação
    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Aluno")
                .setMessage("Tem certeza que deseja excluir este aluno do sistema?")
                .setPositiveButton("Sim, excluir", (dialog, which) -> {
                    AdminRepository repo = new AdminRepository();
                    repo.excluirAluno(cpfEdicao, new RepositoryCallback<Void>(){
                        @Override
                        public void onSucesso(Void resultado) {
                            Toast.makeText(activity_admin_novo_aluno.this, "Aluno excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override
                        public void onErro(String mensagem) {
                            Toast.makeText(activity_admin_novo_aluno.this, "Erro ao excluir: " + mensagem, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    void selecionarStatus(String status) {
        statusSelecionado = status;
        findViewById(R.id.statusAtivo).setSelected("Ativo".equals(status));
        findViewById(R.id.statusInativo).setSelected("Inativo".equals(status));
    }

    String getStatusSelecionado() {
        return statusSelecionado;
    }
}
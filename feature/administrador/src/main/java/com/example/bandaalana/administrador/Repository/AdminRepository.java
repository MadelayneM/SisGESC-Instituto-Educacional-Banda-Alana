package com.example.bandaalana.administrador.Repository;

import com.example.bandaalana.administrador.DTOs.AdminAluno;
import com.example.bandaalana.administrador.DTOs.AdminMembro;
import com.example.bandaalana.administrador.DTOs.AdminOpcao;
import com.example.bandaalana.administrador.DTOs.AdminPerfil;
import com.example.bandaalana.administrador.DTOs.AdminResponse;
import com.example.bandaalana.administrador.DTOs.AdminResumo;
import com.example.bandaalana.administrador.DTOs.AdminTurma;
import com.example.bandaalana.administrador.DTOs.Aviso;
import com.example.bandaalana.administrador.DTOs.CadastroAluno;
import com.example.bandaalana.administrador.DTOs.CadastroMembro;
import com.example.bandaalana.administrador.DTOs.CadastroTurma;
import com.example.bandaalana.administrador.DTOs.MatriculaResponse;
import com.example.bandaalana.administrador.DTOs.NovoAviso;
import com.example.bandaalana.administrador.Supabase.AdminService;
import com.example.bandaalana.administrador.Supabase.SupabaseClient;
import com.example.bandaalana.administrador.Supabase.SupabaseService;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Todos os dados de que as telas do perfil Administrador precisam:
// Dashboard, Alunos, Turmas, Equipe e Perfil, com cadastrar, editar e excluir.
//
// Os cadastros e edições gravam uma tabela de cada vez, na ordem certa. Antes de começar,
// o Repository confere os campos (e o CPF, nos cadastros), para não deixar nada pela metade
// por erro de digitação. Se o banco recusar uma etapa no meio, as seguintes não são feitas
// e a mensagem de erro diz qual etapa falhou.
public class AdminRepository {

    private static final String BUCKET_FOTOS = "fotos_perfil";

    private static final String SELECT_PESSOA = "tb_pessoa(primeiro_nome,sobrenome,imagem_perfil)";
    private static final String SELECT_PESSOA_COMPLETA =
            "tb_pessoa(primeiro_nome,sobrenome,data_nasc,imagem_perfil,tb_email(pk_email),tb_telefone(pk_ddd,pk_numero,tipo_telefone))";

    private static final String SELECT_ALUNOS =
            "fk_cpf," + SELECT_PESSOA + ","
            + "tb_matricula(id_matricula,status_matricula,tb_turma(pk_nome_turma),tb_curso(nome_curso))";

    private static final String SELECT_CADASTRO_ALUNO =
            "fk_cpf,nome_escola,periodo_escola," + SELECT_PESSOA_COMPLETA + ","
            + "tb_aluno_responsavel(fk_cpf_responsavel,grau_parentesco,autorizado_busca,"
            + "tb_responsavel(tb_pessoa(primeiro_nome,sobrenome,data_nasc,tb_telefone(pk_ddd,pk_numero,tipo_telefone)))),"
            + "tb_matricula(id_matricula,periodo,status_matricula,tb_turma(pk_nome_turma))";

    private static final String SELECT_RECENTES =
            "id_matricula,fk_cpf,status_matricula,tb_aluno(" + SELECT_PESSOA + "),"
            + "tb_turma(pk_nome_turma),tb_curso(nome_curso)";

    private static final String SELECT_CARGOS = "tb_funcionario_cargo(fk_cod_cargo,data_saida,tb_cargo(cargo))";

    private static final String SELECT_EQUIPE =
            "fk_cpf_func," + SELECT_PESSOA + "," + SELECT_CARGOS + ","
            + "tb_turma_funcionario(fk_nome_turma,tb_tipo_monitor(tipo_monitor))";

    private static final String SELECT_CADASTRO_MEMBRO =
            "fk_cpf_func," + SELECT_PESSOA_COMPLETA + "," + SELECT_CARGOS + ","
            + "tb_turma_funcionario(fk_nome_turma,fk_cod_tipo)";

    private static final String SELECT_PERFIL = "fk_cpf_func," + SELECT_PESSOA_COMPLETA + "," + SELECT_CARGOS;

    private static final String SELECT_TURMAS =
            "pk_nome_turma,pk_horario,fk_cod_curso,qtd_alunos,data_da_aula,horario_inicio,horario_fim,"
            + "tb_curso(nome_curso),"
            + "tb_turma_funcionario(tb_funcionario(tb_pessoa(primeiro_nome,sobrenome))),"
            + "tb_matricula(status_matricula,tb_frequencia(presenca))";

    private static final String SELECT_CADASTRO_TURMA =
            "pk_nome_turma,fk_cod_curso,qtd_alunos,data_da_aula,horario_inicio,horario_fim,"
            + "tb_turma_funcionario(fk_cpf_funcionario,fk_cod_tipo)";

    private final AdminService admin;
    private final SupabaseService service;

    public AdminRepository() {
        // Mesmo endereço e chave do SupabaseClient, mas enviando os campos null:
        // assim a edição consegue limpar um campo (ex.: tirar a escola do aluno).
        Retrofit.Builder construtor = SupabaseClient.getClient().newBuilder();
        construtor.converterFactories().clear();
        construtor.addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()));
        this.admin = construtor.build().create(AdminService.class);
        this.service = SupabaseClient.getClient().create(SupabaseService.class);
    }

    // =====================================================================
    // Dashboard (Início)
    // =====================================================================

    // Cards da "Visão geral": total de alunos, turmas, professores e a frequência geral,
    // além do primeiro nome para o "Bem-vindo".
    public void buscarResumo(String cpf, RepositoryCallback<AdminResumo> callback) {
        buscarFuncionario(cpf, "fk_cpf_func," + SELECT_PESSOA, callback, eu ->
            contar("tb_aluno", filtros(), callback, totalAlunos ->
                contar("tb_turma", filtros(), callback, totalTurmas ->
                    executar(admin.buscarFuncionarios(filtros("select", "fk_cpf_func," + SELECT_CARGOS)), callback, funcionarios ->
                        contar("tb_frequencia", filtros(), callback, chamadas ->
                            contar("tb_frequencia", filtros("presenca", "eq.true"), callback, presencas -> {
                                Integer frequencia = chamadas == 0 ? null : Math.round(presencas * 100f / chamadas);
                                callback.onSucesso(new AdminResumo(
                                        eu.pessoa == null ? null : eu.pessoa.primeiroNome,
                                        totalAlunos,
                                        totalTurmas,
                                        AdminMapper.soProfessores(funcionarios).size(),
                                        frequencia));
                            }))))));
    }

    // Lista "Alunos recentes": as últimas matrículas feitas, da mais nova para a mais antiga
    public void buscarAlunosRecentes(int limite, RepositoryCallback<List<AdminAluno>> callback) {
        Map<String, String> filtros = filtros("select", SELECT_RECENTES);
        filtros.put("order", "id_matricula.desc");
        filtros.put("limit", String.valueOf(limite));
        executar(admin.buscarMatriculas(filtros), callback,
                matriculas -> callback.onSucesso(AdminMapper.paraAlunosRecentes(matriculas)));
    }

    // =====================================================================
    // Alunos
    // =====================================================================

    // Tela Alunos. textoBusca é o que foi digitado em "Pesquisar aluno"; null ou vazio traz todos.
    public void buscarAlunos(String textoBusca, RepositoryCallback<List<AdminAluno>> callback) {
        Map<String, String> filtros = filtros("select", SELECT_ALUNOS);
        filtros.put("tb_matricula.order", "id_matricula.desc");
        executar(admin.buscarAlunos(filtros), callback,
                alunos -> callback.onSucesso(AdminMapper.paraAlunos(alunos, textoBusca)));
    }

    // Abre o formulário de edição já preenchido
    public void buscarCadastroAluno(String cpf, RepositoryCallback<CadastroAluno> callback) {
        Map<String, String> filtros = filtros("fk_cpf", "eq." + cpf);
        filtros.put("select", SELECT_CADASTRO_ALUNO);
        filtros.put("tb_matricula.order", "id_matricula.desc");
        executar(admin.buscarAlunos(filtros), callback, alunos -> {
            if (alunos.isEmpty()) callback.onErro("Aluno não encontrado.");
            else callback.onSucesso(AdminMapper.paraCadastroAluno(alunos.get(0)));
        });
    }

    // Botão "Cadastrar aluno": cria a pessoa, o aluno, os contatos, o responsável e a matrícula
    public void cadastrarAluno(CadastroAluno aluno, RepositoryCallback<Void> callback) {
        String erro = validarAluno(aluno, true);
        if (erro != null) {
            callback.onErro(erro);
            return;
        }
        verificarCpfLivre(aluno.getCpf(), callback, () ->
            buscarCursoDaTurma(aluno.getNomeTurma(), callback, codCurso -> {
                String cpf = aluno.getCpf();
                List<Etapa> etapas = new ArrayList<>();
                etapas.add(inserir("tb_pessoa", pessoa(cpf, aluno.getPrimeiroNome(), aluno.getSobrenome(),
                        aluno.getDataNascimento()), "dados pessoais do aluno"));
                etapas.add(inserir("tb_aluno", dadosEscolares(cpf, aluno), "aluno"));
                etapasEmail(etapas, cpf, null, aluno.getEmail());
                etapasTelefone(etapas, cpf, null, null, aluno.getDdd(), aluno.getNumeroTelefone(), aluno.getTipoTelefone());
                etapasNovoResponsavel(etapas, aluno);
                etapas.add(inserir("tb_matricula", novaMatricula(aluno, codCurso), "matrícula"));
                gravarEmOrdem(etapas, callback, () -> excluirAluno(cpf, semAviso()));
            }));
    }

    // Botão "Salvar" do formulário aberto por buscarCadastroAluno.
    // Grava os dados pessoais, escolares, contatos, responsável e matrícula.
    public void editarAluno(CadastroAluno aluno, RepositoryCallback<Void> callback) {
        if (!aluno.isCarregadoDoBanco()) {
            callback.onErro("Abra o aluno com buscarCadastroAluno antes de editar.");
            return;
        }
        String erro = validarAluno(aluno, aluno.getIdMatricula() == null);
        if (erro != null) {
            callback.onErro(erro);
            return;
        }
        buscarCursoDaTurma(aluno.getNomeTurma(), callback, codCurso -> {
            String cpf = aluno.getCpf();
            List<Etapa> etapas = new ArrayList<>();

            Map<String, Object> pessoa = pessoa(cpf, aluno.getPrimeiroNome(), aluno.getSobrenome(), aluno.getDataNascimento());
            pessoa.remove("pk_cpf");
            etapas.add(atualizar("tb_pessoa", filtros("pk_cpf", "eq." + cpf), pessoa, "dados pessoais do aluno"));

            Map<String, Object> escola = dadosEscolares(cpf, aluno);
            escola.remove("fk_cpf");
            etapas.add(atualizar("tb_aluno", filtros("fk_cpf", "eq." + cpf), escola, "dados escolares"));

            etapasEmail(etapas, cpf, aluno.getEmailOriginal(), aluno.getEmail());
            etapasTelefone(etapas, cpf, aluno.getDddOriginal(), aluno.getNumeroTelefoneOriginal(),
                    aluno.getDdd(), aluno.getNumeroTelefone(), aluno.getTipoTelefone());

            // Responsável: o mesmo é atualizado; um diferente substitui o vínculo antigo
            String cpfResp = aluno.getCpfResponsavel();
            if (cpfResp.equals(aluno.getCpfResponsavelOriginal())) {
                Map<String, Object> resp = pessoa(cpfResp, aluno.getPrimeiroNomeResponsavel(),
                        aluno.getSobrenomeResponsavel(), aluno.getDataNascimentoResponsavel());
                resp.remove("pk_cpf");
                etapas.add(atualizar("tb_pessoa", filtros("pk_cpf", "eq." + cpfResp), resp, "dados do responsável"));
                etapasTelefone(etapas, cpfResp, aluno.getDddResponsavelOriginal(), aluno.getNumeroTelefoneResponsavelOriginal(),
                        aluno.getDddResponsavel(), aluno.getNumeroTelefoneResponsavel(), "Celular");
                Map<String, Object> vinculo = new HashMap<>();
                vinculo.put("grau_parentesco", aluno.getGrauParentesco());
                vinculo.put("autorizado_busca", aluno.isAutorizadoBusca());
                etapas.add(atualizar("tb_aluno_responsavel", filtrosVinculoResponsavel(cpf, cpfResp), vinculo,
                        "vínculo com o responsável"));
            } else {
                if (aluno.getCpfResponsavelOriginal() != null) {
                    etapas.add(excluir("tb_aluno_responsavel",
                            filtrosVinculoResponsavel(cpf, aluno.getCpfResponsavelOriginal()), "responsável antigo", true));
                }
                etapasNovoResponsavel(etapas, aluno);
            }

            // Matrícula: altera a carregada; se o aluno não tinha, cria uma
            if (aluno.getIdMatricula() == null) {
                etapas.add(inserir("tb_matricula", novaMatricula(aluno, codCurso), "matrícula"));
            } else {
                Map<String, Object> matricula = new HashMap<>();
                matricula.put("fk_nome_turma", aluno.getNomeTurma());
                matricula.put("fk_cod_curso", codCurso);
                matricula.put("periodo", aluno.getPeriodo());
                matricula.put("status_matricula", aluno.getStatus());
                if (!estaVazio(aluno.getSenhaLogin())) matricula.put("senha_login", aluno.getSenhaLogin());
                etapas.add(atualizar("tb_matricula", filtros("id_matricula", "eq." + aluno.getIdMatricula()),
                        matricula, "matrícula"));
            }
            gravarEmOrdem(etapas, callback);
        });
    }


    //gerencia AVISOS
    public void buscarAvisos(RepositoryCallback<List<Aviso>> callback) {
        service.listarAvisos().enqueue(new Callback<List<Aviso>>() {
            @Override
            public void onResponse(Call<List<Aviso>> call, Response<List<Aviso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSucesso(response.body());
                } else {
                    callback.onErro("Erro ao carregar avisos (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<Aviso>> call, Throwable t) {
                callback.onErro("Falha de conexão: " + t.getMessage());
            }
        });
    }

    public void publicarAviso(String cpfCoordenador, String titulo, String descricao, RepositoryCallback<Void> callback) {
        // Remove pontos e traços caso o CPF venha formatado da tela anterior
        String cpfLimpo = (cpfCoordenador != null) ? cpfCoordenador.replaceAll("[^0-9]", "") : "";
        String cpfFinal = cpfLimpo.isEmpty() ? "30000000001" : cpfLimpo;

        NovoAviso novo = new NovoAviso(cpfFinal, titulo, descricao);

        service.criarAviso(novo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSucesso(null);
                } else {
                    String detalhe = "";
                    try {
                        if (response.errorBody() != null) {
                            detalhe = " - " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    callback.onErro("Erro ao publicar (" + response.code() + ")" + detalhe);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onErro("Falha de conexão: " + t.getMessage());
            }
        });
    }

    public void atualizarAviso(int idAviso, String titulo, String descricao, RepositoryCallback<Void> callback) {
        java.util.Map<String, String> campos = new java.util.HashMap<>();
        campos.put("titulo_aviso", titulo);
        campos.put("descricao_aviso", descricao);

        service.editarAviso("eq." + idAviso, campos).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSucesso(null);
                } else {
                    callback.onErro("Erro ao editar aviso (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onErro("Falha de conexão: " + t.getMessage());
            }
        });
    }

    public void deletarAviso(int idAviso, RepositoryCallback<Void> callback) {
        service.excluirAviso("eq." + idAviso).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSucesso(null);
                } else {
                    callback.onErro("Erro ao excluir aviso (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onErro("Falha de conexão: " + t.getMessage());
            }
        });
    }

    // Ação rápida da lista: ativar, deixar pendente ou desativar uma matrícula.
    // status é um de CadastroAluno.STATUS_MATRICULA; idMatricula vem de AdminAluno.getIdMatricula().
    public void alterarStatusMatricula(int idMatricula, String status, RepositoryCallback<Void> callback) {
        if (estaVazio(status)) {
            callback.onErro("Escolha o status da matrícula.");
            return;
        }
        List<Etapa> etapas = new ArrayList<>();
        etapas.add(atualizar("tb_matricula", filtros("id_matricula", "eq." + idMatricula),
                linha("status_matricula", status), "status da matrícula"));
        gravarEmOrdem(etapas, callback);
    }

    // Exclui o aluno e tudo o que é só dele: frequências, matrículas, vínculo com o responsável,
    // contatos, endereço e a pessoa. O responsável continua cadastrado (pode ter outro filho).
    // Para manter o histórico, prefira alterarStatusMatricula(id, "Inativo").
    // Não existe "alterar CPF": o banco não atualiza as tabelas ligadas em cascata.
    // Para corrigir um CPF digitado errado, exclua o cadastro e cadastre de novo.
    public void excluirAluno(String cpf, RepositoryCallback<Void> callback) {
        if (!cpfValido(cpf)) {
            callback.onErro("CPF inválido.");
            return;
        }
        Map<String, String> busca = filtros("fk_cpf", "eq." + cpf);
        busca.put("select", "id_matricula");
        executar(admin.buscarMatriculas(busca), callback, matriculas -> {
            List<Etapa> etapas = new ArrayList<>();
            if (!matriculas.isEmpty()) {
                StringBuilder ids = new StringBuilder();
                for (MatriculaResponse m : matriculas) {
                    if (ids.length() > 0) ids.append(',');
                    ids.append(m.idMatricula);
                }
                etapas.add(excluir("tb_frequencia", filtros("id_matricula", "in.(" + ids + ")"), "frequências", false));
                etapas.add(excluir("tb_matricula", filtros("fk_cpf", "eq." + cpf), "matrículas", true));
            }
            etapas.add(excluir("tb_aluno_responsavel", filtros("fk_cpf_aluno", "eq." + cpf), "vínculo com o responsável", false));
            etapas.add(excluir("tb_aluno", filtros("fk_cpf", "eq." + cpf), "aluno", true));
            etapasExcluirPessoa(etapas, cpf);
            gravarEmOrdem(etapas, callback);
        });
    }



    // =====================================================================
    // Turmas
    // =====================================================================

    // Tela Turmas e "Consultar frequência": cada turma com curso, equipe, alunos ativos e frequência
    public void buscarTurmas(RepositoryCallback<List<AdminTurma>> callback) {
        executar(admin.buscarTurmas(filtros("select", SELECT_TURMAS)), callback,
                turmas -> callback.onSucesso(AdminMapper.paraTurmas(turmas)));
    }

    // Abre o formulário de edição já preenchido
    public void buscarCadastroTurma(String nomeTurma, RepositoryCallback<CadastroTurma> callback) {
        Map<String, String> filtros = filtros("pk_nome_turma", "eq." + nomeTurma);
        filtros.put("select", SELECT_CADASTRO_TURMA);
        executar(admin.buscarTurmas(filtros), callback, turmas -> {
            if (turmas.isEmpty()) callback.onErro("Turma não encontrada.");
            else callback.onSucesso(AdminMapper.paraCadastroTurma(turmas.get(0)));
        });
    }

    // Botão "Criar turma"
    public void criarTurma(CadastroTurma turma, RepositoryCallback<Void> callback) {
        String erro = validarTurma(turma);
        if (erro != null) {
            callback.onErro(erro);
            return;
        }
        String nome = turma.getNomeTurma().trim();
        List<Etapa> etapas = new ArrayList<>();
        Map<String, Object> linha = dadosTurma(turma);
        linha.put("pk_nome_turma", nome);
        etapas.add(inserir("tb_turma", linha, "turma"));
        if (!estaVazio(turma.getCpfProfessor())) {
            etapas.add(inserir("tb_turma_funcionario",
                    vinculoTurma(nome, turma.getCpfProfessor(), turma.getCodigoTipoMonitor()), "professor da turma"));
        }
        gravarEmOrdem(etapas, callback, () -> excluirTurma(nome, semAviso()));
    }

    // Botão "Salvar" do formulário aberto por buscarCadastroTurma. O nome da turma não muda.
    public void editarTurma(CadastroTurma turma, RepositoryCallback<Void> callback) {
        if (!turma.isCarregadoDoBanco()) {
            callback.onErro("Abra a turma com buscarCadastroTurma antes de editar.");
            return;
        }
        String erro = validarTurma(turma);
        if (erro != null) {
            callback.onErro(erro);
            return;
        }
        String nome = turma.getNomeTurma();
        List<Etapa> etapas = new ArrayList<>();
        etapas.add(atualizar("tb_turma", filtros("pk_nome_turma", "eq." + nome), dadosTurma(turma), "turma"));

        String novo = vazioParaNull(turma.getCpfProfessor());
        String antigo = turma.getCpfProfessorOriginal();
        if (novo != null && novo.equals(antigo)) {
            etapas.add(atualizar("tb_turma_funcionario", filtrosVinculoTurma(nome, novo),
                    linha("fk_cod_tipo", Integer.valueOf(turma.getCodigoTipoMonitor())), "função do professor"));
        } else {
            if (antigo != null) {
                etapas.add(excluir("tb_turma_funcionario", filtrosVinculoTurma(nome, antigo), "professor antigo", true));
            }
            if (novo != null) {
                etapas.add(inserir("tb_turma_funcionario",
                        vinculoTurma(nome, novo, turma.getCodigoTipoMonitor()), "professor da turma"));
            }
        }
        gravarEmOrdem(etapas, callback);
    }

    // Exclui a turma e a equipe dela. Só funciona se a turma não tiver nenhuma matrícula:
    // antes, mude os alunos de turma (editarAluno) ou exclua as matrículas.
    public void excluirTurma(String nomeTurma, RepositoryCallback<Void> callback) {
        if (estaVazio(nomeTurma)) {
            callback.onErro("Escolha a turma.");
            return;
        }
        contar("tb_matricula", filtros("fk_nome_turma", "eq." + nomeTurma), callback, matriculas -> {
            if (matriculas > 0) {
                callback.onErro("A turma tem " + matriculas + " matrícula(s). Mude os alunos de turma antes de excluir.");
                return;
            }
            List<Etapa> etapas = new ArrayList<>();
            etapas.add(excluir("tb_turma_funcionario", filtros("fk_nome_turma", "eq." + nomeTurma), "equipe da turma", false));
            etapas.add(excluir("tb_turma", filtros("pk_nome_turma", "eq." + nomeTurma), "turma", true));
            gravarEmOrdem(etapas, callback);
        });
    }

    // =====================================================================
    // Equipe (professores, monitores e coordenadores)
    // =====================================================================

    // Tela Equipe: funcionários com cargo ativo, com as turmas de cada um
    public void buscarEquipe(RepositoryCallback<List<AdminMembro>> callback) {
        executar(admin.buscarFuncionarios(filtros("select", SELECT_EQUIPE)), callback,
                funcionarios -> callback.onSucesso(AdminMapper.paraEquipe(funcionarios)));
    }

    // Abre o formulário de edição já preenchido
    public void buscarCadastroMembro(String cpf, RepositoryCallback<CadastroMembro> callback) {
        buscarFuncionario(cpf, SELECT_CADASTRO_MEMBRO, callback,
                funcionario -> callback.onSucesso(AdminMapper.paraCadastroMembro(funcionario)));
    }

    // Botão "Cadastrar professor": cria a pessoa, o funcionário, o cargo, os contatos e as turmas
    public void cadastrarMembro(CadastroMembro membro, RepositoryCallback<Void> callback) {
        String erro = validarMembro(membro, true);
        if (erro != null) {
            callback.onErro(erro);
            return;
        }
        verificarCpfLivre(membro.getCpf(), callback, () -> {
            String cpf = membro.getCpf();
            List<Etapa> etapas = new ArrayList<>();
            etapas.add(inserir("tb_pessoa", pessoa(cpf, membro.getPrimeiroNome(), membro.getSobrenome(),
                    membro.getDataNascimento()), "dados pessoais"));
            Map<String, Object> funcionario = linha("fk_cpf_func", cpf);
            funcionario.put("senha_funcionario", membro.getSenha());
            etapas.add(inserir("tb_funcionario", funcionario, "funcionário"));
            etapas.add(inserir("tb_funcionario_cargo", novoCargo(cpf, membro.getCodigoCargo()), "cargo"));
            etapasEmail(etapas, cpf, null, membro.getEmail());
            etapasTelefone(etapas, cpf, null, null, membro.getDdd(), membro.getNumeroTelefone(), membro.getTipoTelefone());
            etapasTurmasDoMembro(etapas, cpf, membro);
            gravarEmOrdem(etapas, callback, () -> excluirMembro(cpf, semAviso()));
        });
    }

    // Botão "Salvar" do formulário aberto por buscarCadastroMembro.
    // Trocar o cargo encerra o cargo atual hoje e começa o novo; a lista de turmas substitui a antiga.
    public void editarMembro(CadastroMembro membro, RepositoryCallback<Void> callback) {
        if (!membro.isCarregadoDoBanco()) {
            callback.onErro("Abra o funcionário com buscarCadastroMembro antes de editar.");
            return;
        }
        String erro = validarMembro(membro, false);
        if (erro != null) {
            callback.onErro(erro);
            return;
        }
        String cpf = membro.getCpf();
        List<Etapa> etapas = new ArrayList<>();
        Map<String, Object> pessoa = pessoa(cpf, membro.getPrimeiroNome(), membro.getSobrenome(), membro.getDataNascimento());
        pessoa.remove("pk_cpf");
        etapas.add(atualizar("tb_pessoa", filtros("pk_cpf", "eq." + cpf), pessoa, "dados pessoais"));

        if (!estaVazio(membro.getSenha())) {
            etapas.add(atualizar("tb_funcionario", filtrosSemSenha("fk_cpf_func", cpf),
                    linha("senha_funcionario", membro.getSenha()), "senha"));
        }
        if (!membro.getCodigoCargo().equals(membro.getCodigoCargoOriginal())) {
            if (membro.getCodigoCargoOriginal() != null) etapas.add(encerrarCargoAtual(cpf));
            etapas.add(inserir("tb_funcionario_cargo", novoCargo(cpf, membro.getCodigoCargo()), "novo cargo"));
        }
        etapasEmail(etapas, cpf, membro.getEmailOriginal(), membro.getEmail());
        etapasTelefone(etapas, cpf, membro.getDddOriginal(), membro.getNumeroTelefoneOriginal(),
                membro.getDdd(), membro.getNumeroTelefone(), membro.getTipoTelefone());
        etapas.add(excluir("tb_turma_funcionario", filtros("fk_cpf_funcionario", "eq." + cpf), "turmas antigas", false));
        etapasTurmasDoMembro(etapas, cpf, membro);
        gravarEmOrdem(etapas, callback);
    }

    // Desliga o funcionário sem apagar o histórico: o cargo ganha data de saída (hoje)
    // e ele sai das turmas. Ele some da Equipe e não entra mais no app.
    public void desligarMembro(String cpf, RepositoryCallback<Void> callback) {
        if (!cpfValido(cpf)) {
            callback.onErro("CPF inválido.");
            return;
        }
        List<Etapa> etapas = new ArrayList<>();
        etapas.add(encerrarCargoAtual(cpf));
        etapas.add(excluir("tb_turma_funcionario", filtros("fk_cpf_funcionario", "eq." + cpf), "turmas", false));
        gravarEmOrdem(etapas, callback);
    }

    // Exclui o funcionário de vez (turmas, cargos, contatos e a pessoa).
    // Se ele tiver avisos ou avaliações, o banco não deixa: use desligarMembro.
    public void excluirMembro(String cpf, RepositoryCallback<Void> callback) {
        if (!cpfValido(cpf)) {
            callback.onErro("CPF inválido.");
            return;
        }
        List<Etapa> etapas = new ArrayList<>();
        etapas.add(excluir("tb_turma_funcionario", filtros("fk_cpf_funcionario", "eq." + cpf), "turmas", false));
        etapas.add(excluir("tb_funcionario_cargo", filtros("fk_cpf_funcionario", "eq." + cpf), "cargos", false));
        etapas.add(excluir("tb_funcionario", filtrosSemSenha("fk_cpf_func", cpf), "funcionário", true));
        etapasExcluirPessoa(etapas, cpf);
        gravarEmOrdem(etapas, callback);
    }

    // =====================================================================
    // Opções dos Spinners dos cadastros
    // =====================================================================

    // "Instrumento / Atividade" e o curso da Nova turma
    public void buscarOpcoesCursos(RepositoryCallback<List<AdminOpcao>> callback) {
        executar(admin.buscarCursos(filtros("select", "pk_cod_curso,nome_curso")), callback,
                cursos -> callback.onSucesso(AdminMapper.paraOpcoesCursos(cursos)));
    }

    // Turma do Cadastrar aluno e as turmas do Cadastrar professor
    public void buscarOpcoesTurmas(RepositoryCallback<List<AdminOpcao>> callback) {
        executar(admin.buscarTurmas(filtros("select", "pk_nome_turma")), callback,
                turmas -> callback.onSucesso(AdminMapper.paraOpcoesTurmas(turmas)));
    }

    // "Professor responsável" da Nova turma
    public void buscarOpcoesProfessores(RepositoryCallback<List<AdminOpcao>> callback) {
        executar(admin.buscarFuncionarios(filtros("select", "fk_cpf_func," + SELECT_PESSOA + "," + SELECT_CARGOS)),
                callback, funcionarios -> callback.onSucesso(AdminMapper.paraOpcoesProfessores(funcionarios)));
    }

    // "Cargo" do Cadastrar professor
    public void buscarOpcoesCargos(RepositoryCallback<List<AdminOpcao>> callback) {
        executar(admin.buscarCargos(filtros("select", "pk_cod_cargo,cargo")), callback,
                cargos -> callback.onSucesso(AdminMapper.paraOpcoesCargos(cargos)));
    }

    // Função do professor na turma (Monitor Auxiliar, Monitor Multiplicador...)
    public void buscarOpcoesTiposMonitor(RepositoryCallback<List<AdminOpcao>> callback) {
        executar(admin.buscarTiposMonitor(filtros("select", "pk_cod_tipo,tipo_monitor")), callback,
                tipos -> callback.onSucesso(AdminMapper.paraOpcoesTiposMonitor(tipos)));
    }

    // =====================================================================
    // Perfil do próprio administrador
    // =====================================================================

    // Tela Perfil (e os valores atuais para a janela "Editar dados")
    public void buscarPerfil(String cpf, RepositoryCallback<AdminPerfil> callback) {
        buscarFuncionario(cpf, SELECT_PERFIL, callback, eu -> callback.onSucesso(AdminMapper.paraPerfil(eu)));
    }

    // Janela "Alterar senha": o próprio PATCH confere a senha atual
    public void alterarSenha(String cpf, String senhaAtual, String novaSenha, RepositoryCallback<Void> callback) {
        if (estaVazio(novaSenha)) {
            callback.onErro("A nova senha não pode ficar em branco.");
            return;
        }
        Map<String, String> filtros = filtrosSemSenha("fk_cpf_func", cpf);
        filtros.put("senha_funcionario", "eq." + senhaAtual);
        executar(admin.atualizar("tb_funcionario", filtros, linha("senha_funcionario", novaSenha)), callback,
                alteradas -> {
                    if (alteradas.isEmpty()) callback.onErro("Senha atual incorreta ou alteração não permitida.");
                    else callback.onSucesso(null);
                });
    }

    // Envia a foto para o Storage, salva o endereço em tb_pessoa e devolve esse endereço.
    // tipoImagem é "image/jpeg" ou "image/png". A tela é quem transforma a imagem em bytes.
    // Serve para qualquer pessoa (o admin, um aluno ou um professor).
    public void atualizarFoto(String cpf, byte[] foto, String tipoImagem, RepositoryCallback<String> callback) {
        String arquivo = cpf + ("image/png".equals(tipoImagem) ? ".png" : ".jpg");
        RequestBody corpo = RequestBody.create(MediaType.parse(tipoImagem), foto);

        executar(service.enviarFotoPerfil(arquivo, "true", corpo), callback, enviada -> {
            // O "?v=" muda o endereço a cada troca, senão o app pode continuar mostrando a foto antiga
            String url = SupabaseClient.urlPublicaStorage(BUCKET_FOTOS, arquivo) + "?v=" + System.currentTimeMillis();
            Map<String, String> campos = new HashMap<>();
            campos.put("imagem_perfil", url);
            executar(service.atualizarImagemPerfil("eq." + cpf, "pk_cpf", campos), callback, alteradas -> {
                if (alteradas.isEmpty()) callback.onErro("A foto foi enviada, mas o perfil não foi alterado.");
                else callback.onSucesso(url);
            });
        });
    }

    // Janela "Editar dados": altera o e-mail, ou cadastra se ainda não tiver um
    public void atualizarEmail(String cpf, String novoEmail, RepositoryCallback<Void> callback) {
        if (estaVazio(novoEmail)) {
            callback.onErro("O e-mail não pode ficar em branco.");
            return;
        }
        executar(admin.atualizar("tb_email", filtros("fk_cpf", "eq." + cpf), linha("pk_email", novoEmail.trim())),
                callback, alteradas -> {
                    if (!alteradas.isEmpty()) {
                        callback.onSucesso(null);
                        return;
                    }
                    List<Etapa> etapas = new ArrayList<>();
                    etapasEmail(etapas, cpf, null, novoEmail);
                    gravarEmOrdem(etapas, callback);
                });
    }

    // Janela "Editar dados". dddAtual e numeroAtual vêm de buscarPerfil (null = não tinha telefone).
    // tipo é um dos valores de AdminPerfil.TIPOS_TELEFONE
    public void atualizarTelefone(String cpf, String dddAtual, String numeroAtual,
                                  String novoDdd, String novoNumero, String tipo,
                                  RepositoryCallback<Void> callback) {
        if (estaVazio(novoDdd) || estaVazio(novoNumero) || estaVazio(tipo)) {
            callback.onErro("Preencha o DDD, o número e o tipo do telefone.");
            return;
        }
        List<Etapa> etapas = new ArrayList<>();
        etapasTelefone(etapas, cpf, dddAtual, numeroAtual, novoDdd, novoNumero, tipo);
        gravarEmOrdem(etapas, callback);
    }

    // =====================================================================
    // Validações (devolvem a mensagem de erro, ou null se estiver tudo certo)
    // =====================================================================

    // senhaObrigatoria: true no cadastro (na edição, senha vazia mantém a atual)
    private String validarAluno(CadastroAluno a, boolean senhaObrigatoria) {
        if (!cpfValido(a.getCpf())) return "O CPF do aluno precisa ter 11 dígitos, sem pontos nem traço.";
        if (estaVazio(a.getPrimeiroNome()) || estaVazio(a.getSobrenome())) return "Preencha o nome e o sobrenome do aluno.";
        if (!dataValida(a.getDataNascimento())) return "Data de nascimento inválida. Use ano-mês-dia, ex.: 2014-03-12.";
        if (estaVazio(a.getNomeEscola())) return "Preencha a escola do aluno.";
        if (estaVazio(a.getPeriodoEscola())) return "Escolha o período da escola.";
        if (!telefoneCompletoOuVazio(a.getDdd(), a.getNumeroTelefone())) return "Preencha o DDD e o número do telefone, ou deixe os dois vazios.";
        if (!cpfValido(a.getCpfResponsavel())) return "O CPF do responsável precisa ter 11 dígitos, sem pontos nem traço.";
        if (a.getCpfResponsavel().equals(a.getCpf())) return "O CPF do responsável não pode ser o mesmo do aluno.";
        if (estaVazio(a.getPrimeiroNomeResponsavel()) || estaVazio(a.getSobrenomeResponsavel())) return "Preencha o nome e o sobrenome do responsável.";
        if (!dataValida(a.getDataNascimentoResponsavel())) return "Data de nascimento do responsável inválida. Use ano-mês-dia, ex.: 1985-08-30.";
        if (!telefoneCompletoOuVazio(a.getDddResponsavel(), a.getNumeroTelefoneResponsavel())) return "Preencha o DDD e o número do telefone do responsável, ou deixe os dois vazios.";
        if (estaVazio(a.getGrauParentesco())) return "Escolha o grau de parentesco do responsável.";
        if (estaVazio(a.getNomeTurma())) return "Escolha a turma.";
        if (estaVazio(a.getPeriodo())) return "Escolha o período da matrícula.";
        if (estaVazio(a.getStatus())) return "Escolha o status da matrícula.";
        if (senhaObrigatoria && estaVazio(a.getSenhaLogin())) return "Defina a senha de acesso do aluno.";
        return null;
    }

    private String validarMembro(CadastroMembro m, boolean senhaObrigatoria) {
        if (!cpfValido(m.getCpf())) return "O CPF precisa ter 11 dígitos, sem pontos nem traço.";
        if (estaVazio(m.getPrimeiroNome()) || estaVazio(m.getSobrenome())) return "Preencha o nome e o sobrenome.";
        if (!dataValida(m.getDataNascimento())) return "Data de nascimento inválida. Use ano-mês-dia, ex.: 1990-05-20.";
        if (senhaObrigatoria && estaVazio(m.getSenha())) return "Defina a senha de acesso.";
        if (!numeroValido(m.getCodigoCargo())) return "Escolha o cargo.";
        if (!telefoneCompletoOuVazio(m.getDdd(), m.getNumeroTelefone())) return "Preencha o DDD e o número do telefone, ou deixe os dois vazios.";
        for (CadastroMembro.Turma turma : m.getTurmas()) {
            if (estaVazio(turma.getNomeTurma()) || !numeroValido(turma.getCodigoTipoMonitor())) {
                return "Escolha a turma e a função de cada vínculo.";
            }
        }
        return null;
    }

    private String validarTurma(CadastroTurma t) {
        if (estaVazio(t.getNomeTurma())) return "Preencha o nome da turma.";
        if (!numeroValido(t.getCodigoCurso())) return "Escolha o curso.";
        if (t.getVagas() <= 0) return "A quantidade de vagas precisa ser maior que zero.";
        if (!dataValida(t.getDataAula())) return "Data da aula inválida. Use ano-mês-dia, ex.: 2026-11-10.";
        if (!horaValida(t.getHorarioInicio()) || !horaValida(t.getHorarioFim())) return "Horário inválido. Use hora:minuto, ex.: 14:00.";
        if (t.getHorarioFim().compareTo(t.getHorarioInicio()) <= 0) return "O horário de fim precisa ser depois do início.";
        if (!estaVazio(t.getCpfProfessor()) && !numeroValido(t.getCodigoTipoMonitor())) return "Escolha a função do professor na turma.";
        return null;
    }

    // =====================================================================
    // Montagem das etapas de gravação
    // =====================================================================

    // E-mail: cadastra, troca ou apaga, conforme o que havia antes e o que foi digitado
    private void etapasEmail(List<Etapa> etapas, String cpf, String original, String novo) {
        String email = vazioParaNull(novo);
        if (original == null && email != null) {
            Map<String, Object> linha = linha("fk_cpf", cpf);
            linha.put("pk_email", email);
            etapas.add(inserir("tb_email", linha, "e-mail"));
        } else if (original != null && email == null) {
            etapas.add(excluir("tb_email", filtros("fk_cpf", "eq." + cpf), "e-mail", true));
        } else if (original != null && !original.equals(email)) {
            etapas.add(atualizar("tb_email", filtros("fk_cpf", "eq." + cpf), linha("pk_email", email), "e-mail"));
        }
    }

    // Telefone: cadastra, troca ou apaga só o telefone que estava no formulário
    private void etapasTelefone(List<Etapa> etapas, String cpf, String dddOriginal, String numeroOriginal,
                                String ddd, String numero, String tipo) {
        boolean tinha = dddOriginal != null && numeroOriginal != null;
        boolean tem = !estaVazio(ddd) && !estaVazio(numero);
        if (!tinha && tem) {
            etapas.add(inserir("tb_telefone", telefone(cpf, ddd, numero, tipo), "telefone"));
        } else if (tinha && !tem) {
            etapas.add(excluir("tb_telefone", filtrosTelefone(cpf, dddOriginal, numeroOriginal), "telefone", true));
        } else if (tinha) {
            Map<String, Object> campos = telefone(cpf, ddd, numero, tipo);
            campos.remove("fk_cpf");
            etapas.add(atualizar("tb_telefone", filtrosTelefone(cpf, dddOriginal, numeroOriginal), campos, "telefone"));
        }
    }

    // Responsável novo para o aluno. Se a pessoa já existir (irmão), ela é reaproveitada.
    private void etapasNovoResponsavel(List<Etapa> etapas, CadastroAluno aluno) {
        String cpfResp = aluno.getCpfResponsavel();
        etapas.add(inserirSeNaoExistir("tb_pessoa", pessoa(cpfResp, aluno.getPrimeiroNomeResponsavel(),
                aluno.getSobrenomeResponsavel(), aluno.getDataNascimentoResponsavel())));
        etapas.add(inserirSeNaoExistir("tb_responsavel", linha("fk_cpf", cpfResp)));
        if (!estaVazio(aluno.getDddResponsavel()) && !estaVazio(aluno.getNumeroTelefoneResponsavel())) {
            etapas.add(inserirSeNaoExistir("tb_telefone", telefone(cpfResp,
                    aluno.getDddResponsavel(), aluno.getNumeroTelefoneResponsavel(), "Celular")));
        }
        Map<String, Object> vinculo = linha("fk_cpf_aluno", aluno.getCpf());
        vinculo.put("fk_cpf_responsavel", cpfResp);
        vinculo.put("grau_parentesco", aluno.getGrauParentesco());
        vinculo.put("autorizado_busca", aluno.isAutorizadoBusca());
        etapas.add(inserir("tb_aluno_responsavel", vinculo, "vínculo com o responsável"));
    }

    private void etapasTurmasDoMembro(List<Etapa> etapas, String cpf, CadastroMembro membro) {
        if (membro.getTurmas().isEmpty()) return;
        List<Map<String, Object>> turmas = new ArrayList<>();
        for (CadastroMembro.Turma turma : membro.getTurmas()) {
            turmas.add(vinculoTurma(turma.getNomeTurma(), cpf, turma.getCodigoTipoMonitor()));
        }
        etapas.add(inserir("tb_turma_funcionario", turmas, "turmas"));
    }

    // Contatos, endereço e a própria pessoa. O e-mail, o telefone e o endereço podem não existir.
    private void etapasExcluirPessoa(List<Etapa> etapas, String cpf) {
        etapas.add(excluir("tb_email", filtros("fk_cpf", "eq." + cpf), "e-mail", false));
        etapas.add(excluir("tb_telefone", filtros("fk_cpf", "eq." + cpf), "telefones", false));
        etapas.add(excluir("tb_endereco", filtros("pk_cpf", "eq." + cpf), "endereço", false));
        etapas.add(excluir("tb_pessoa", filtros("pk_cpf", "eq." + cpf), "dados pessoais", true));
    }

    private Etapa encerrarCargoAtual(String cpf) {
        Map<String, String> filtros = filtros("fk_cpf_funcionario", "eq." + cpf);
        filtros.put("data_saida", "is.null");
        return atualizar("tb_funcionario_cargo", filtros, linha("data_saida", hojeNoBanco()), "cargo atual");
    }

    private Map<String, Object> novaMatricula(CadastroAluno aluno, Integer codCurso) {
        Calendar hoje = Calendar.getInstance();
        Map<String, Object> matricula = linha("fk_cpf", aluno.getCpf());
        matricula.put("ano", hoje.get(Calendar.YEAR));
        matricula.put("semestre", hoje.get(Calendar.MONTH) < Calendar.JULY ? 1 : 2);
        matricula.put("fk_nome_turma", aluno.getNomeTurma());
        matricula.put("fk_cod_curso", codCurso);
        matricula.put("data_matricula", hojeNoBanco());
        matricula.put("periodo", aluno.getPeriodo());
        matricula.put("status_matricula", aluno.getStatus());
        matricula.put("senha_login", aluno.getSenhaLogin());
        return matricula;
    }

    private Map<String, Object> dadosEscolares(String cpf, CadastroAluno aluno) {
        Map<String, Object> linha = linha("fk_cpf", cpf);
        linha.put("nome_escola", aluno.getNomeEscola().trim());
        linha.put("periodo_escola", aluno.getPeriodoEscola());
        return linha;
    }

    private Map<String, Object> dadosTurma(CadastroTurma turma) {
        String inicio = turma.getHorarioInicio() + ":00";
        Map<String, Object> linha = linha("pk_horario", inicio);
        linha.put("fk_cod_curso", Integer.valueOf(turma.getCodigoCurso()));
        linha.put("qtd_alunos", turma.getVagas());
        linha.put("data_da_aula", turma.getDataAula());
        linha.put("horario_inicio", inicio);
        linha.put("horario_fim", turma.getHorarioFim() + ":00");
        return linha;
    }

    private Map<String, Object> novoCargo(String cpf, String codigoCargo) {
        Map<String, Object> cargo = linha("fk_cpf_funcionario", cpf);
        cargo.put("fk_cod_cargo", Integer.valueOf(codigoCargo));
        cargo.put("data_entrada", hojeNoBanco());
        return cargo;
    }

    private Map<String, Object> pessoa(String cpf, String primeiroNome, String sobrenome, String dataNascimento) {
        Map<String, Object> pessoa = linha("pk_cpf", cpf);
        pessoa.put("primeiro_nome", primeiroNome.trim());
        pessoa.put("sobrenome", sobrenome.trim());
        pessoa.put("data_nasc", dataNascimento);
        return pessoa;
    }

    private Map<String, Object> telefone(String cpf, String ddd, String numero, String tipo) {
        Map<String, Object> telefone = linha("fk_cpf", cpf);
        telefone.put("pk_ddd", ddd.trim());
        telefone.put("pk_numero", numero.trim());
        telefone.put("tipo_telefone", estaVazio(tipo) ? "Celular" : tipo);
        return telefone;
    }

    private Map<String, Object> vinculoTurma(String nomeTurma, String cpf, String codigoTipoMonitor) {
        Map<String, Object> vinculo = linha("fk_nome_turma", nomeTurma);
        vinculo.put("fk_cpf_funcionario", cpf);
        vinculo.put("fk_cod_tipo", Integer.valueOf(codigoTipoMonitor));
        return vinculo;
    }

    private Map<String, String> filtrosTelefone(String cpf, String ddd, String numero) {
        Map<String, String> filtros = filtros("fk_cpf", "eq." + cpf);
        filtros.put("pk_ddd", "eq." + ddd);
        filtros.put("pk_numero", "eq." + numero);
        return filtros;
    }

    private Map<String, String> filtrosVinculoResponsavel(String cpfAluno, String cpfResponsavel) {
        Map<String, String> filtros = filtros("fk_cpf_aluno", "eq." + cpfAluno);
        filtros.put("fk_cpf_responsavel", "eq." + cpfResponsavel);
        return filtros;
    }

    private Map<String, String> filtrosVinculoTurma(String nomeTurma, String cpf) {
        Map<String, String> filtros = filtros("fk_nome_turma", "eq." + nomeTurma);
        filtros.put("fk_cpf_funcionario", "eq." + cpf);
        return filtros;
    }

    // Filtro por CPF em tb_funcionario pedindo de volta só o CPF, para a senha não trafegar
    private Map<String, String> filtrosSemSenha(String coluna, String cpf) {
        Map<String, String> filtros = filtros(coluna, "eq." + cpf);
        filtros.put("select", "fk_cpf_func");
        return filtros;
    }

    // =====================================================================
    // Execução
    // =====================================================================

    // Uma gravação do cadastro/edição. exigeLinha: se o banco não devolver nenhuma linha,
    // é erro (o filtro não achou nada ou o RLS bloqueou sem avisar).
    private static final class Etapa {
        final Call<List<Object>> chamada;
        final String descricao;
        final boolean exigeLinha;

        Etapa(Call<List<Object>> chamada, String descricao, boolean exigeLinha) {
            this.chamada = chamada;
            this.descricao = descricao;
            this.exigeLinha = exigeLinha;
        }
    }

    private Etapa inserir(String tabela, Object linhas, String descricao) {
        return new Etapa(admin.inserir(tabela, selectSemSenha(tabela), linhas), descricao, true);
    }

    private Etapa inserirSeNaoExistir(String tabela, Map<String, Object> linha) {
        return new Etapa(admin.inserirSeNaoExistir(tabela, selectSemSenha(tabela), linha), "responsável", false);
    }

    private Etapa atualizar(String tabela, Map<String, String> filtros, Map<String, Object> campos, String descricao) {
        if (!filtros.containsKey("select")) filtros.putAll(selectSemSenha(tabela));
        return new Etapa(admin.atualizar(tabela, filtros, campos), descricao, true);
    }

    private Etapa excluir(String tabela, Map<String, String> filtros, String descricao, boolean exigeLinha) {
        if (!filtros.containsKey("select")) filtros.putAll(selectSemSenha(tabela));
        return new Etapa(admin.excluir(tabela, filtros), descricao, exigeLinha);
    }

    // As tabelas com senha devolvem só a chave, para a senha não voltar na resposta
    private Map<String, String> selectSemSenha(String tabela) {
        if ("tb_funcionario".equals(tabela)) return filtros("select", "fk_cpf_func");
        if ("tb_matricula".equals(tabela)) return filtros("select", "id_matricula");
        return filtros();
    }

    // Executa as etapas uma depois da outra. Se uma falhar, as seguintes não são feitas.
    private void gravarEmOrdem(List<Etapa> etapas, RepositoryCallback<Void> callback) {
        gravarEmOrdem(etapas, 0, callback, null);
    }

    // Nos cadastros: se uma etapa falhar, "desfazer" apaga o que as anteriores gravaram,
    // para o cadastro não ficar pela metade (e o CPF poder ser usado de novo).
    private void gravarEmOrdem(List<Etapa> etapas, RepositoryCallback<Void> callback, Runnable desfazer) {
        gravarEmOrdem(etapas, 0, callback, desfazer);
    }

    private void gravarEmOrdem(List<Etapa> etapas, int indice, RepositoryCallback<Void> callback, Runnable desfazer) {
        if (indice == etapas.size()) {
            callback.onSucesso(null);
            return;
        }
        RepositoryCallback<Void> aoFalhar = desfazer == null || indice == 0 ? callback : new RepositoryCallback<Void>() {
            @Override public void onSucesso(Void r) { callback.onSucesso(r); }
            @Override public void onErro(String mensagem) {
                desfazer.run();
                callback.onErro(mensagem + " Nada foi salvo.");
            }
        };
        Etapa etapa = etapas.get(indice);
        etapa.chamada.enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                if (!response.isSuccessful()) {
                    aoFalhar.onErro("Erro em " + etapa.descricao + ": " + mensagemDeErro(response));
                } else if (etapa.exigeLinha && (response.body() == null || response.body().isEmpty())) {
                    aoFalhar.onErro("Nada foi gravado em " + etapa.descricao
                            + ". O registro não existe ou o banco bloqueou a alteração (RLS).");
                } else {
                    gravarEmOrdem(etapas, indice + 1, callback, desfazer);
                }
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                aoFalhar.onErro("Falha na comunicação com o servidor: " + t.getMessage());
            }
        });
    }

    // Callback vazio para o "desfazer", que roda escondido
    private RepositoryCallback<Void> semAviso() {
        return new RepositoryCallback<Void>() {
            @Override public void onSucesso(Void r) { }
            @Override public void onErro(String mensagem) { }
        };
    }

    private void buscarFuncionario(String cpf, String select, RepositoryCallback<?> callback,
                                   AoReceber<AdminResponse.Funcionario> aoEncontrar) {
        Map<String, String> filtros = filtros("fk_cpf_func", "eq." + cpf);
        filtros.put("select", select);
        executar(admin.buscarFuncionarios(filtros), callback, funcionarios -> {
            if (funcionarios.isEmpty()) callback.onErro("Funcionário não encontrado.");
            else aoEncontrar.tratar(funcionarios.get(0));
        });
    }

    // Segue só se ninguém (aluno, responsável ou funcionário) tiver esse CPF
    private void verificarCpfLivre(String cpf, RepositoryCallback<?> callback, Runnable seLivre) {
        executar(service.buscarPessoaPorCpf("eq." + cpf), callback, pessoas -> {
            if (pessoas.isEmpty()) seLivre.run();
            else callback.onErro("Já existe uma pessoa cadastrada com esse CPF.");
        });
    }

    // A matrícula guarda a turma e o curso dela
    private void buscarCursoDaTurma(String nomeTurma, RepositoryCallback<?> callback, AoReceber<Integer> aoEncontrar) {
        Map<String, String> filtros = filtros("pk_nome_turma", "eq." + nomeTurma);
        filtros.put("select", "pk_nome_turma,fk_cod_curso");
        executar(admin.buscarTurmas(filtros), callback, turmas -> {
            if (turmas.isEmpty()) callback.onErro("Turma não encontrada.");
            else aoEncontrar.tratar(turmas.get(0).codCurso);
        });
    }

    // Lê o total do cabeçalho Content-Range, ex.: "0-12/13" ou "*/0"
    private void contar(String tabela, Map<String, String> filtros, RepositoryCallback<?> callback,
                        AoReceber<Integer> aoContar) {
        admin.contar(tabela, filtros).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                String intervalo = response.headers().get("Content-Range");
                if (!response.isSuccessful() || intervalo == null || !intervalo.contains("/")) {
                    callback.onErro(maiuscula(mensagemDeErro(response)));
                    return;
                }
                try {
                    aoContar.tratar(Integer.parseInt(intervalo.substring(intervalo.indexOf('/') + 1)));
                } catch (NumberFormatException e) {
                    callback.onErro("Não foi possível contar os registros de " + tabela + ".");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onErro("Falha na comunicação com o servidor: " + t.getMessage());
            }
        });
    }

    // Faz a chamada em segundo plano e trata os erros iguais para todos os métodos
    private <T> void executar(Call<T> chamada, RepositoryCallback<?> callback, AoReceber<T> aoReceber) {
        chamada.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) aoReceber.tratar(response.body());
                else callback.onErro(maiuscula(mensagemDeErro(response)));
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onErro("Falha na comunicação com o servidor: " + t.getMessage());
            }
        });
    }

    // Traduz os códigos de erro do Postgres que aparecem nos cadastros
    private String mensagemDeErro(Response<?> response) {
        String corpo = "";
        try {
            if (response.errorBody() != null) corpo = response.errorBody().string();
        } catch (IOException ignorada) {
            // sem corpo, fica só o código HTTP
        }
        if (corpo.contains("\"23505\"")) return "já existe um cadastro com esses dados (CPF, e-mail, telefone ou nome repetido).";
        if (corpo.contains("\"23503\"")) return "há outros registros ligados a este (ex.: matrículas, avisos, turmas) ou um código que não existe.";
        if (corpo.contains("\"23502\"")) return "faltou um campo obrigatório: " + mensagemDoBanco(corpo);
        if (corpo.contains("\"22P02\"")) return "valor não aceito pelo banco: " + mensagemDoBanco(corpo);
        if (corpo.contains("\"42501\"") || response.code() == 401 || response.code() == 403) {
            return "o banco não permitiu a gravação (falta policy de RLS).";
        }
        String detalhe = mensagemDoBanco(corpo);
        return "o servidor recusou a operação (HTTP " + response.code() + ")" + (detalhe.isEmpty() ? "." : ": " + detalhe);
    }

    private String maiuscula(String texto) {
        return texto.isEmpty() ? texto : texto.substring(0, 1).toUpperCase(Locale.ROOT) + texto.substring(1);
    }

    private String mensagemDoBanco(String corpo) {
        // O texto do banco pode ter aspas escapadas (\"), ex.: null value in column \"data_nasc\"
        Matcher m = Pattern.compile("\"message\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(corpo);
        return m.find() ? m.group(1).replace("\\\"", "\"") : "";
    }

    private Map<String, Object> linha(String coluna, Object valor) {
        Map<String, Object> linha = new HashMap<>();
        linha.put(coluna, valor);
        return linha;
    }

    private Map<String, String> filtros() {
        return new HashMap<>();
    }

    private Map<String, String> filtros(String coluna, String valor) {
        Map<String, String> filtros = new HashMap<>();
        filtros.put(coluna, valor);
        return filtros;
    }

    private String hojeNoBanco() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
    }

    private boolean cpfValido(String cpf) {
        return cpf != null && cpf.matches("\\d{11}");
    }

    private boolean dataValida(String data) {
        return data != null && data.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private boolean horaValida(String hora) {
        return hora != null && hora.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    private boolean numeroValido(String texto) {
        return texto != null && texto.matches("\\d+");
    }

    private boolean telefoneCompletoOuVazio(String ddd, String numero) {
        return estaVazio(ddd) == estaVazio(numero);
    }

    private boolean estaVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private String vazioParaNull(String texto) {
        return estaVazio(texto) ? null : texto.trim();
    }

    private interface AoReceber<T> {
        void tratar(T corpo);
    }
}

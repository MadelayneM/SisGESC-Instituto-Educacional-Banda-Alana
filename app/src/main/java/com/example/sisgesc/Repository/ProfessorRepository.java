package com.example.sisgesc.Repository;

import com.example.sisgesc.DTOs.ChamadaAluno;
import com.example.sisgesc.DTOs.FuncionarioResponse;
import com.example.sisgesc.DTOs.ProfessorAluno;
import com.example.sisgesc.DTOs.ProfessorInicio;
import com.example.sisgesc.DTOs.ProfessorPerfil;
import com.example.sisgesc.DTOs.ProfessorTurma;
import com.example.sisgesc.DTOs.TurmaFrequencia;
import com.example.sisgesc.Supabase.SupabaseClient;
import com.example.sisgesc.Supabase.SupabaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Todos os dados de que as telas do perfil Professor precisam.
// O cpf é o do professor que entrou: 11 dígitos, sem pontos nem traço.
public class ProfessorRepository {

    private static final String BUCKET_FOTOS = "fotos_perfil";

    private static final String SELECT_TURMA =
            "pk_nome_turma,qtd_alunos,data_da_aula,horario_inicio,horario_fim,tb_curso(nome_curso),"
            + "tb_matricula(id_matricula,status_matricula)";

    private static final String SELECT_TURMAS =
            "fk_cpf_func,tb_turma_funcionario(tb_tipo_monitor(tipo_monitor),tb_turma(" + SELECT_TURMA + "))";

    private static final String SELECT_INICIO = SELECT_TURMAS + ",tb_pessoa(primeiro_nome)";

    private static final String SELECT_ALUNOS =
            "fk_cpf_func,tb_turma_funcionario(tb_turma(pk_nome_turma,tb_curso(nome_curso),"
            + "tb_matricula(id_matricula,fk_cpf,status_matricula,tb_aluno(tb_pessoa(primeiro_nome,sobrenome,imagem_perfil)))))";

    private static final String SELECT_FREQUENCIA =
            "fk_cpf_func,tb_turma_funcionario(tb_turma(pk_nome_turma,tb_curso(nome_curso),"
            + "tb_matricula(id_matricula,tb_frequencia(data_aula,presenca))))";

    private static final String SELECT_PERFIL =
            "fk_cpf_func,"
            + "tb_pessoa(primeiro_nome,sobrenome,imagem_perfil,tb_email(pk_email),tb_telefone(pk_ddd,pk_numero,tipo_telefone)),"
            + "tb_funcionario_cargo(data_saida,tb_cargo(cargo)),"
            + "tb_turma_funcionario(tb_tipo_monitor(tipo_monitor))";

    private static final String SELECT_CHAMADA =
            "id_matricula,tb_aluno(tb_pessoa(primeiro_nome,sobrenome,imagem_perfil)),"
            + "tb_frequencia(id_frequencia,data_aula,presenca,observacao_frequencia)";

    // Campos gravados na chamada: com e sem id_frequencia (linha que já existe ou linha nova)
    private static final String COLUNAS_FREQUENCIA_NOVA = "id_matricula,data_aula,presenca,observacao_frequencia";
    private static final String COLUNAS_FREQUENCIA_EXISTENTE = "id_frequencia," + COLUNAS_FREQUENCIA_NOVA;

    private final SupabaseService service;

    public ProfessorRepository() {
        this.service = SupabaseClient.getClient().create(SupabaseService.class);
    }

    // Tela Início: primeiro nome do professor e as turmas dele
    public void buscarInicio(String cpf, RepositoryCallback<ProfessorInicio> callback) {
        buscarFuncionario(cpf, SELECT_INICIO, callback,
                professor -> callback.onSucesso(ProfessorMapper.paraInicio(professor)));
    }

    // Tela Minhas Turmas
    public void buscarTurmas(String cpf, RepositoryCallback<List<ProfessorTurma>> callback) {
        buscarFuncionario(cpf, SELECT_TURMAS, callback,
                professor -> callback.onSucesso(ProfessorMapper.paraTurmas(professor)));
    }

    // Tela Alunos. textoBusca é o que foi digitado em "Pesquisar aluno"; null ou vazio traz todos.
    public void buscarAlunos(String cpf, String textoBusca, RepositoryCallback<List<ProfessorAluno>> callback) {
        buscarAlunos(cpf, null, textoBusca, callback);
    }

    // Tela Alunos aberta pelo botão "Ver alunos" de uma turma: só os alunos dessa turma.
    // nomeTurma vem de ProfessorTurma.getNomeTurma(); null traz os alunos de todas as turmas.
    public void buscarAlunos(String cpf, String nomeTurma, String textoBusca,
                             RepositoryCallback<List<ProfessorAluno>> callback) {
        buscarFuncionario(cpf, SELECT_ALUNOS, callback,
                professor -> callback.onSucesso(ProfessorMapper.paraAlunos(professor, nomeTurma, textoBusca)));
    }

    // Tela Histórico de Frequência: uma lista de aulas para cada turma do professor
    public void buscarHistoricoFrequencia(String cpf, RepositoryCallback<List<TurmaFrequencia>> callback) {
        buscarFuncionario(cpf, SELECT_FREQUENCIA, callback,
                professor -> callback.onSucesso(ProfessorMapper.paraFrequencia(professor)));
    }

    // Tela Perfil (e os valores atuais para a tela "Editar dados")
    public void buscarPerfil(String cpf, RepositoryCallback<ProfessorPerfil> callback) {
        buscarFuncionario(cpf, SELECT_PERFIL, callback,
                professor -> callback.onSucesso(ProfessorMapper.paraPerfil(professor)));
    }

    // Botão "Fazer chamada": alunos ativos da turma, com a presença já salva nessa data (se houver).
    // nomeTurma vem de ProfessorTurma.getNomeTurma(). dataAula no formato ano-mês-dia, ex.: 2026-11-06.
    public void buscarChamada(String nomeTurma, String dataAula, RepositoryCallback<List<ChamadaAluno>> callback) {
        if (!dataValida(dataAula)) {
            callback.onErro("Data inválida. Use o formato ano-mês-dia, ex.: 2026-11-06.");
            return;
        }
        Call<List<FuncionarioResponse.Matricula>> chamada = service.buscarChamada(
                "eq." + nomeTurma, "eq." + ProfessorMapper.STATUS_ATIVO, "eq." + dataAula, SELECT_CHAMADA);
        executar(chamada, callback, matriculas -> callback.onSucesso(ProfessorMapper.paraChamada(matriculas)));
    }

    // Salva a chamada montada a partir de buscarChamada. Todos os alunos precisam estar marcados.
    // Quem já tinha presença salva nessa data é atualizado; quem não tinha ganha uma linha nova.
    public void salvarChamada(String dataAula, List<ChamadaAluno> alunos, RepositoryCallback<Void> callback) {
        if (!dataValida(dataAula)) {
            callback.onErro("Data inválida. Use o formato ano-mês-dia, ex.: 2026-11-06.");
            return;
        }
        if (alunos == null || alunos.isEmpty()) {
            callback.onErro("A turma não tem alunos para a chamada.");
            return;
        }

        List<Map<String, Object>> novas = new ArrayList<>();
        List<Map<String, Object>> existentes = new ArrayList<>();
        for (ChamadaAluno aluno : alunos) {
            if (aluno.getPresente() == null) {
                callback.onErro("Marque presença ou falta de todos os alunos.");
                return;
            }
            Map<String, Object> linha = new HashMap<>();
            linha.put("id_matricula", aluno.getIdMatricula());
            linha.put("data_aula", dataAula);
            linha.put("presenca", aluno.getPresente());
            linha.put("observacao_frequencia", aluno.getObservacao());
            if (aluno.getIdFrequencia() == null) {
                novas.add(linha);
            } else {
                linha.put("id_frequencia", aluno.getIdFrequencia());
                existentes.add(linha);
            }
        }

        // Primeiro as linhas que já existem, depois as novas. Uma lista vazia pula a etapa.
        gravarFrequencias(COLUNAS_FREQUENCIA_EXISTENTE, existentes, callback,
                () -> gravarFrequencias(COLUNAS_FREQUENCIA_NOVA, novas, callback,
                        () -> callback.onSucesso(null)));
    }

    // Botão "Alterar senha": o próprio PATCH confere a senha atual
    public void alterarSenha(String cpf, String senhaAtual, String novaSenha, RepositoryCallback<Void> callback) {
        if (estaVazio(novaSenha)) {
            callback.onErro("A nova senha não pode ficar em branco.");
            return;
        }
        Map<String, String> campos = Collections.singletonMap("senha_funcionario", novaSenha);
        // O select pede só o CPF para a senha não voltar na resposta
        Call<List<FuncionarioResponse>> chamada =
                service.alterarSenhaFuncionario("eq." + cpf, "eq." + senhaAtual, "fk_cpf_func", campos);
        executar(chamada, callback, alteradas -> {
            if (alteradas.isEmpty()) {
                callback.onErro("Senha atual incorreta ou alteração não permitida.");
            } else {
                callback.onSucesso(null);
            }
        });
    }

    // Envia a foto para o Storage, salva o endereço em tb_pessoa e devolve esse endereço.
    // tipoImagem é "image/jpeg" ou "image/png". A tela é quem transforma a imagem em bytes.
    public void atualizarFoto(String cpf, byte[] foto, String tipoImagem, RepositoryCallback<String> callback) {
        String extensao = "image/png".equals(tipoImagem) ? ".png" : ".jpg";
        String arquivo = cpf + extensao;
        RequestBody corpo = RequestBody.create(MediaType.parse(tipoImagem), foto);

        executar(service.enviarFotoPerfil(arquivo, "true", corpo), callback, enviada -> {
            // O "?v=" muda o endereço a cada troca, senão o app pode continuar mostrando a foto antiga
            String url = SupabaseClient.urlPublicaStorage(BUCKET_FOTOS, arquivo)
                    + "?v=" + System.currentTimeMillis();
            Map<String, String> campos = Collections.singletonMap("imagem_perfil", url);
            executar(service.atualizarImagemPerfil("eq." + cpf, "pk_cpf", campos), callback, alteradas -> {
                if (alteradas.isEmpty()) {
                    callback.onErro("A foto foi enviada, mas o perfil não foi alterado.");
                } else {
                    callback.onSucesso(url);
                }
            });
        });
    }

    // Altera o e-mail, ou cadastra se o professor ainda não tiver um
    public void atualizarEmail(String cpf, String novoEmail, RepositoryCallback<Void> callback) {
        if (estaVazio(novoEmail)) {
            callback.onErro("O e-mail não pode ficar em branco.");
            return;
        }
        Map<String, String> campos = Collections.singletonMap("pk_email", novoEmail);
        Map<String, String> novaLinha = new HashMap<>(campos);
        novaLinha.put("fk_cpf", cpf);
        executar(service.atualizarEmail("eq." + cpf, campos), callback, alteradas -> {
            if (alteradas.isEmpty()) {
                executar(service.inserirEmail(novaLinha), callback, criada -> callback.onSucesso(null));
            } else {
                callback.onSucesso(null);
            }
        });
    }

    // dddAtual e numeroAtual vêm de buscarPerfil. Se forem null, cadastra um telefone novo.
    // tipo é um dos valores de ProfessorPerfil.TIPOS_TELEFONE
    public void atualizarTelefone(String cpf, String dddAtual, String numeroAtual,
                                  String novoDdd, String novoNumero, String tipo,
                                  RepositoryCallback<Void> callback) {
        if (estaVazio(novoDdd) || estaVazio(novoNumero) || estaVazio(tipo)) {
            callback.onErro("Preencha o DDD, o número e o tipo do telefone.");
            return;
        }
        Map<String, String> campos = new HashMap<>();
        campos.put("pk_ddd", novoDdd);
        campos.put("pk_numero", novoNumero);
        campos.put("tipo_telefone", tipo);

        if (dddAtual == null || numeroAtual == null) {
            campos.put("fk_cpf", cpf);
            executar(service.inserirTelefone(campos), callback, criado -> callback.onSucesso(null));
            return;
        }
        Call<List<Object>> chamada = service.atualizarTelefone(
                "eq." + cpf, "eq." + dddAtual, "eq." + numeroAtual, campos);
        executar(chamada, callback, alteradas -> {
            if (alteradas.isEmpty()) {
                callback.onErro("O telefone não foi alterado.");
            } else {
                callback.onSucesso(null);
            }
        });
    }

    // Grava as linhas e só segue para "depois" se o banco devolver todas elas.
    // Menos linhas na resposta quer dizer que o banco bloqueou parte da gravação.
    private void gravarFrequencias(String colunas, List<Map<String, Object>> linhas,
                                   RepositoryCallback<Void> callback, Runnable depois) {
        if (linhas.isEmpty()) {
            depois.run();
            return;
        }
        executar(service.salvarFrequencias(colunas, linhas), callback, gravadas -> {
            if (gravadas.size() < linhas.size()) {
                callback.onErro("A chamada não foi salva por completo. Tente novamente.");
            } else {
                depois.run();
            }
        });
    }

    private void buscarFuncionario(String cpf, String select, RepositoryCallback<?> callback,
                                   AoReceber<FuncionarioResponse> aoEncontrar) {
        executar(service.buscarFuncionario("eq." + cpf, select), callback, funcionarios -> {
            if (funcionarios.isEmpty()) {
                callback.onErro("Professor não encontrado.");
            } else {
                aoEncontrar.tratar(funcionarios.get(0));
            }
        });
    }

    private boolean dataValida(String data) {
        return data != null && data.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private boolean estaVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // Faz a chamada em segundo plano e trata os erros iguais para todos os métodos
    private <T> void executar(Call<T> chamada, RepositoryCallback<?> callback, AoReceber<T> aoReceber) {
        chamada.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    aoReceber.tratar(response.body());
                } else {
                    callback.onErro("O servidor recusou a operação (HTTP " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onErro("Falha na comunicação com o servidor: " + t.getMessage());
            }
        });
    }

    private interface AoReceber<T> {
        void tratar(T corpo);
    }
}

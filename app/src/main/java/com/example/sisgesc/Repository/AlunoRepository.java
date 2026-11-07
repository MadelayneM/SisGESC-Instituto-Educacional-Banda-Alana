package com.example.sisgesc.Repository;

import com.example.sisgesc.DTOs.AlunoDadosEdicao;
import com.example.sisgesc.DTOs.AlunoDashboard;
import com.example.sisgesc.DTOs.AlunoFrequencia;
import com.example.sisgesc.DTOs.AlunoPerfil;
import com.example.sisgesc.DTOs.AlunoPerfilResponse;
import com.example.sisgesc.DTOs.AlunoTurma;
import com.example.sisgesc.DTOs.Aviso;
import com.example.sisgesc.DTOs.MatriculaResponse;
import com.example.sisgesc.Supabase.SupabaseClient;
import com.example.sisgesc.Supabase.SupabaseService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Todos os dados de que as telas do perfil Aluno precisam.
// O cpf deve ter 11 dígitos, sem pontos nem traço.
public class AlunoRepository {

    private static final String BUCKET_FOTOS = "fotos_perfil";

    // Do mais recente para o mais antigo, para pegar a matrícula atual
    private static final String ORDEM_MATRICULA = "ano.desc,semestre.desc,data_matricula.desc";

    private static final String SELECT_PERFIL =
            "fk_cpf,"
            + "tb_pessoa(primeiro_nome,sobrenome,imagem_perfil,tb_email(pk_email),tb_telefone(pk_ddd,pk_numero,tipo_telefone)),"
            + "tb_aluno_responsavel(grau_parentesco,tb_responsavel(tb_pessoa(primeiro_nome,sobrenome,"
            + "tb_telefone(pk_ddd,pk_numero,tipo_telefone))))";

    private static final String SELECT_EDICAO =
            "fk_cpf,tb_pessoa(tb_email(pk_email),tb_telefone(pk_ddd,pk_numero,tipo_telefone),"
            + "tb_endereco(pk_cep,numero_endereco,complemento,tb_cep(logradouro,bairro,cidade,uf)))";

    private static final String SELECT_FREQUENCIA =
            "id_matricula,tb_frequencia(data_aula,presenca,observacao_frequencia)";

    private static final String SELECT_TURMA =
            "id_matricula,ano,semestre,tb_curso(nome_curso),"
            + "tb_turma(pk_nome_turma,qtd_alunos,data_da_aula,horario_inicio,horario_fim,"
            + "tb_turma_funcionario(tb_tipo_monitor(tipo_monitor),tb_funcionario(tb_pessoa(primeiro_nome,sobrenome))),"
            + "tb_matricula(fk_cpf,ano,semestre,status_matricula,tb_aluno(tb_pessoa(primeiro_nome,sobrenome))))";

    private static final String SELECT_DASHBOARD =
            SELECT_TURMA + ",tb_aluno(tb_pessoa(primeiro_nome,sobrenome,imagem_perfil)),"
            + "tb_frequencia(data_aula,presenca,observacao_frequencia)";

    private static final String SELECT_AVISOS = "pk_id_aviso,titulo_aviso,descricao_aviso,data_envio";

    private final SupabaseService service;

    public AlunoRepository() {
        this.service = SupabaseClient.getClient().create(SupabaseService.class);
    }

    // Tela de Perfil
    public void buscarPerfil(String cpf, RepositoryCallback<AlunoPerfil> callback) {
        executar(service.buscarPerfilAluno("eq." + cpf, SELECT_PERFIL), callback, alunos -> {
            if (alunos.isEmpty()) {
                callback.onErro("Aluno não encontrado.");
            } else {
                callback.onSucesso(AlunoMapper.paraPerfil(alunos.get(0)));
            }
        });
    }

    // Tela Minha Frequência
    public void buscarFrequencia(String cpf, RepositoryCallback<AlunoFrequencia> callback) {
        buscarMatriculaAtiva(cpf, SELECT_FREQUENCIA, callback,
                matricula -> callback.onSucesso(AlunoMapper.paraFrequencia(matricula)));
    }

    // Tela Minha Turma
    public void buscarTurma(String cpf, RepositoryCallback<AlunoTurma> callback) {
        buscarMatriculaAtiva(cpf, SELECT_TURMA, callback,
                matricula -> callback.onSucesso(AlunoMapper.paraTurma(cpf, matricula)));
    }

    // Tela inicial, exceto os avisos
    public void buscarDashboard(String cpf, RepositoryCallback<AlunoDashboard> callback) {
        buscarMatriculaAtiva(cpf, SELECT_DASHBOARD, callback,
                matricula -> callback.onSucesso(AlunoMapper.paraDashboard(cpf, matricula)));
    }

    // Avisos recentes da tela inicial, do mais novo para o mais antigo
    public void buscarAvisos(int limite, RepositoryCallback<List<Aviso>> callback) {
        executar(service.buscarAvisos("data_envio.desc", limite, SELECT_AVISOS), callback, callback::onSucesso);
    }

    // Botão "Alterar senha"
    public void alterarSenha(String cpf, String senhaAtual, String novaSenha, RepositoryCallback<Void> callback) {
        if (novaSenha == null || novaSenha.trim().isEmpty()) {
            callback.onErro("A nova senha não pode ficar em branco.");
            return;
        }
        Map<String, String> campos = Collections.singletonMap("senha_login", novaSenha);
        // O select pede só o id para a senha não voltar na resposta
        Call<List<MatriculaResponse>> chamada =
                service.alterarSenha("eq." + cpf, "eq." + senhaAtual, "id_matricula", campos);
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

    // Tela "Editar dados": traz os valores atuais para preencher os campos
    public void buscarDadosEdicao(String cpf, RepositoryCallback<AlunoDadosEdicao> callback) {
        executar(service.buscarPerfilAluno("eq." + cpf, SELECT_EDICAO), callback, alunos -> {
            if (alunos.isEmpty()) {
                callback.onErro("Aluno não encontrado.");
            } else {
                callback.onSucesso(AlunoMapper.paraDadosEdicao(alunos.get(0)));
            }
        });
    }

    // Altera o e-mail, ou cadastra se o aluno ainda não tiver um
    public void atualizarEmail(String cpf, String novoEmail, RepositoryCallback<Void> callback) {
        if (estaVazio(novoEmail)) {
            callback.onErro("O e-mail não pode ficar em branco.");
            return;
        }
        Map<String, String> campos = Collections.singletonMap("pk_email", novoEmail);
        Map<String, String> novaLinha = new HashMap<>(campos);
        novaLinha.put("fk_cpf", cpf);
        atualizarOuInserir(service.atualizarEmail("eq." + cpf, campos), service.inserirEmail(novaLinha), callback);
    }

    // dddAtual e numeroAtual vêm de buscarDadosEdicao. Se forem null, cadastra um telefone novo.
    // tipo é um dos valores de AlunoDadosEdicao.TIPOS_TELEFONE
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

    // Salva o CEP e depois liga esse CEP ao aluno. complemento pode ser null.
    public void atualizarEndereco(String cpf, String cep, String logradouro, String bairro, String cidade,
                                  String uf, String numero, String complemento,
                                  RepositoryCallback<Void> callback) {
        if (estaVazio(cep) || estaVazio(logradouro) || estaVazio(bairro) || estaVazio(cidade)
                || estaVazio(uf) || estaVazio(numero)) {
            callback.onErro("Preencha todos os campos do endereço.");
            return;
        }
        Map<String, String> dadosCep = new HashMap<>();
        dadosCep.put("pk_cep", cep);
        dadosCep.put("logradouro", logradouro);
        dadosCep.put("bairro", bairro);
        dadosCep.put("cidade", cidade);
        dadosCep.put("uf", uf);

        Map<String, String> campos = new HashMap<>();
        campos.put("pk_cep", cep);
        campos.put("numero_endereco", numero);
        // Texto vazio, porque um valor null não seria enviado e o complemento antigo ficaria salvo
        campos.put("complemento", complemento == null ? "" : complemento);
        Map<String, String> novaLinha = new HashMap<>(campos);
        novaLinha.put("pk_cpf", cpf);

        executar(service.salvarCep(dadosCep), callback, salvo -> atualizarOuInserir(
                service.atualizarEndereco("eq." + cpf, campos), service.inserirEndereco(novaLinha), callback));
    }

    // Tenta alterar a linha do aluno. Se ela ainda não existir, cria.
    private void atualizarOuInserir(Call<List<Object>> atualizar, Call<ResponseBody> inserir,
                                    RepositoryCallback<Void> callback) {
        executar(atualizar, callback, alteradas -> {
            if (alteradas.isEmpty()) {
                executar(inserir, callback, criada -> callback.onSucesso(null));
            } else {
                callback.onSucesso(null);
            }
        });
    }

    private boolean estaVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private void buscarMatriculaAtiva(String cpf, String select, RepositoryCallback<?> callback,
                                      AoReceber<MatriculaResponse> aoEncontrar) {
        Call<List<MatriculaResponse>> chamada = service.buscarMatriculas(
                "eq." + cpf, "eq." + AlunoMapper.STATUS_ATIVO, ORDEM_MATRICULA, 1, select);
        executar(chamada, callback, matriculas -> {
            if (matriculas.isEmpty()) {
                callback.onErro("Nenhuma matrícula ativa encontrada para este aluno.");
            } else {
                aoEncontrar.tratar(matriculas.get(0));
            }
        });
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

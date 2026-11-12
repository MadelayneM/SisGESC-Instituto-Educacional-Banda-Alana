package com.example.sisgesc.Supabase;

import com.example.sisgesc.DTOs.AlunoPerfilResponse;
import com.example.sisgesc.DTOs.Aviso;
import com.example.sisgesc.DTOs.FuncionarioLoginResponse;
import com.example.sisgesc.DTOs.FuncionarioResponse;
import com.example.sisgesc.DTOs.MatriculaResponse;
import com.example.sisgesc.DTOs.Pessoa;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseService {

    @GET("rest/v1/tb_pessoa")
    Call<List<Pessoa>> buscarPessoaPorCpf(@Query("pk_cpf") String filtroCpf);

    // ===== Login =====
    // O CPF e a senha vão como filtro: a resposta só traz linhas se os dois baterem,
    // e a senha nunca volta do servidor.

    @GET("rest/v1/tb_matricula")
    Call<List<MatriculaResponse>> entrarComoAluno(
            @Query("fk_cpf") String filtroCpf,
            @Query("senha_login") String filtroSenha,
            @Query("status_matricula") String filtroStatus,
            @Query("select") String select,
            @Query("limit") int limite);

    @GET("rest/v1/tb_funcionario")
    Call<List<FuncionarioLoginResponse>> entrarComoFuncionario(
            @Query("fk_cpf_func") String filtroCpf,
            @Query("senha_funcionario") String filtroSenha,
            @Query("select") String select,
            @Query("limit") int limite);

    // ===== Módulo Aluno =====

    @GET("rest/v1/tb_aluno")
    Call<List<AlunoPerfilResponse>> buscarPerfilAluno(
            @Query("fk_cpf") String filtroCpf,
            @Query("select") String select);

    @GET("rest/v1/tb_matricula")
    Call<List<MatriculaResponse>> buscarMatriculas(
            @Query("fk_cpf") String filtroCpf,
            @Query("status_matricula") String filtroStatus,
            @Query("order") String ordem,
            @Query("limit") int limite,
            @Query("select") String select);

    @GET("rest/v1/tb_aviso")
    Call<List<Aviso>> buscarAvisos(
            @Query("order") String ordem,
            @Query("limit") int limite,
            @Query("select") String select);

    // O filtro de senha faz o próprio PATCH validar a senha atual:
    // se ela estiver errada, nenhuma linha muda e a resposta vem como lista vazia.
    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/tb_matricula")
    Call<List<MatriculaResponse>> alterarSenha(
            @Query("fk_cpf") String filtroCpf,
            @Query("senha_login") String filtroSenhaAtual,
            @Query("select") String select,
            @Body Map<String, String> campos);

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/tb_pessoa")
    Call<List<Pessoa>> atualizarImagemPerfil(
            @Query("pk_cpf") String filtroCpf,
            @Query("select") String select,
            @Body Map<String, String> campos);

    // ===== Editar dados do aluno =====
    // Os PATCH devolvem as linhas alteradas: lista vazia quer dizer que nada mudou.

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/tb_email")
    Call<List<Object>> atualizarEmail(
            @Query("fk_cpf") String filtroCpf,
            @Body Map<String, String> campos);

    @POST("rest/v1/tb_email")
    Call<ResponseBody> inserirEmail(@Body Map<String, String> campos);

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/tb_telefone")
    Call<List<Object>> atualizarTelefone(
            @Query("fk_cpf") String filtroCpf,
            @Query("pk_ddd") String filtroDdd,
            @Query("pk_numero") String filtroNumero,
            @Body Map<String, String> campos);

    @POST("rest/v1/tb_telefone")
    Call<ResponseBody> inserirTelefone(@Body Map<String, String> campos);

    // merge-duplicates: se o CEP já existir, atualiza a linha em vez de dar erro
    @Headers("Prefer: resolution=merge-duplicates")
    @POST("rest/v1/tb_cep")
    Call<ResponseBody> salvarCep(@Body Map<String, String> campos);

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/tb_endereco")
    Call<List<Object>> atualizarEndereco(
            @Query("pk_cpf") String filtroCpf,
            @Body Map<String, String> campos);

    @POST("rest/v1/tb_endereco")
    Call<ResponseBody> inserirEndereco(@Body Map<String, String> campos);

    // x-upsert "true" substitui a foto anterior do aluno, se ela já existir
    @POST("storage/v1/object/fotos_perfil/{arquivo}")
    Call<ResponseBody> enviarFotoPerfil(
            @Path("arquivo") String arquivo,
            @Header("x-upsert") String substituir,
            @Body RequestBody foto);

    // ===== Módulo Professor =====

    // Um funcionário com as tabelas ligadas a ele. O select escolhe o que vem junto.
    @GET("rest/v1/tb_funcionario")
    Call<List<FuncionarioResponse>> buscarFuncionario(
            @Query("fk_cpf_func") String filtroCpf,
            @Query("select") String select);

    // Alunos de uma turma, cada um com a frequência de uma única data
    // (o filtro "tb_frequencia.data_aula" vale só para a lista de frequências que vem junto).
    @GET("rest/v1/tb_matricula")
    Call<List<FuncionarioResponse.Matricula>> buscarChamada(
            @Query("fk_nome_turma") String filtroTurma,
            @Query("status_matricula") String filtroStatus,
            @Query("tb_frequencia.data_aula") String filtroData,
            @Query("select") String select);

    // Igual ao alterarSenha do aluno: o filtro de senha faz o PATCH validar a senha atual
    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/tb_funcionario")
    Call<List<FuncionarioResponse>> alterarSenhaFuncionario(
            @Query("fk_cpf_func") String filtroCpf,
            @Query("senha_funcionario") String filtroSenhaAtual,
            @Query("select") String select,
            @Body Map<String, String> campos);

    // Grava várias linhas de frequência de uma vez. merge-duplicates: uma linha com
    // id_frequencia que já existe é atualizada; uma linha sem id_frequencia é criada.
    // colunas lista os campos gravados: um campo que ficou fora do JSON (valor null) é gravado como null.
    @Headers("Prefer: return=representation,resolution=merge-duplicates")
    @POST("rest/v1/tb_frequencia")
    Call<List<Object>> salvarFrequencias(
            @Query("columns") String colunas,
            @Body List<Map<String, Object>> linhas);

    @Headers("Prefer: return=representation")
    @POST("rest/v1/tb_aviso")
    Call<List<Aviso>> enviarAviso(@Body Map<String, String> campos);
}

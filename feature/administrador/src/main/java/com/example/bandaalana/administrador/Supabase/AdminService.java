package com.example.bandaalana.administrador.Supabase;

import com.example.bandaalana.administrador.DTOs.AdminResponse;
import com.example.bandaalana.administrador.DTOs.MatriculaResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

// Requisições do módulo Administrador.
// Fica separado do SupabaseService para as branches de Aluno, Professor e Administrador
// não mexerem no mesmo arquivo (evita conflito no merge com a main).
// Os filtros vão num Map no padrão do PostgREST, ex.: {"pk_cpf": "eq.12345678901", "select": "..."}
//
// As gravações (inserir, atualizar, excluir) devolvem as linhas afetadas.
// Lista vazia = nada mudou: o filtro não achou nada ou o RLS bloqueou sem dar erro.
public interface AdminService {

    // ===== Contagem (cards do Dashboard) =====
    // O total vem no cabeçalho Content-Range, ex.: "0-12/13" (13 linhas). A resposta não tem corpo.
    @Headers("Prefer: count=exact")
    @HEAD("rest/v1/{tabela}")
    Call<Void> contar(@Path("tabela") String tabela, @QueryMap Map<String, String> filtros);

    // ===== Consultas =====

    @GET("rest/v1/tb_aluno")
    Call<List<AdminResponse.Aluno>> buscarAlunos(@QueryMap Map<String, String> filtros);

    @GET("rest/v1/tb_matricula")
    Call<List<MatriculaResponse>> buscarMatriculas(@QueryMap Map<String, String> filtros);

    @GET("rest/v1/tb_funcionario")
    Call<List<AdminResponse.Funcionario>> buscarFuncionarios(@QueryMap Map<String, String> filtros);

    @GET("rest/v1/tb_turma")
    Call<List<AdminResponse.Turma>> buscarTurmas(@QueryMap Map<String, String> filtros);

    @GET("rest/v1/tb_curso")
    Call<List<AdminResponse.Curso>> buscarCursos(@QueryMap Map<String, String> filtros);

    @GET("rest/v1/tb_cargo")
    Call<List<AdminResponse.Cargo>> buscarCargos(@QueryMap Map<String, String> filtros);

    @GET("rest/v1/tb_tipo_monitor")
    Call<List<AdminResponse.TipoMonitor>> buscarTiposMonitor(@QueryMap Map<String, String> filtros);

    // ===== Gravações =====

    // Cadastra uma linha (Map) ou várias (List<Map>) em qualquer tabela.
    // filtros pode levar só o "select", para escolher o que volta na resposta.
    @Headers("Prefer: return=representation")
    @POST("rest/v1/{tabela}")
    Call<List<Object>> inserir(@Path("tabela") String tabela,
                               @QueryMap Map<String, String> filtros,
                               @Body Object linhas);

    // Igual a inserir, mas se a linha já existir (mesma chave primária) ela é mantida sem erro
    // e não volta na resposta. Usado para o responsável, que pode já existir por causa de um irmão.
    @Headers("Prefer: resolution=ignore-duplicates,return=representation")
    @POST("rest/v1/{tabela}")
    Call<List<Object>> inserirSeNaoExistir(@Path("tabela") String tabela,
                                           @QueryMap Map<String, String> filtros,
                                           @Body Object linhas);

    // Altera as linhas que passam nos filtros
    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/{tabela}")
    Call<List<Object>> atualizar(@Path("tabela") String tabela,
                                 @QueryMap Map<String, String> filtros,
                                 @Body Map<String, Object> campos);

    // Exclui as linhas que passam nos filtros.
    // CUIDADO: sem filtro, apagaria a tabela inteira. O AdminRepository sempre manda um filtro.
    @Headers("Prefer: return=representation")
    @DELETE("rest/v1/{tabela}")
    Call<List<Object>> excluir(@Path("tabela") String tabela,
                               @QueryMap Map<String, String> filtros);
}

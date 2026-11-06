package com.example.sisgesc.Supabase;

import com.example.sisgesc.DTOs.AlunoPerfilResponse;
import com.example.sisgesc.DTOs.Aviso;
import com.example.sisgesc.DTOs.FuncionarioLoginResponse;
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

    // x-upsert "true" substitui a foto anterior do aluno, se ela já existir
    @POST("storage/v1/object/fotos_perfil/{arquivo}")
    Call<ResponseBody> enviarFotoPerfil(
            @Path("arquivo") String arquivo,
            @Header("x-upsert") String substituir,
            @Body RequestBody foto);
}

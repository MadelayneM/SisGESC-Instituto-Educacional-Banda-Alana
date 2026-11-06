package com.example.sisgesc.Supabase;

import com.example.sisgesc.DTOs.Aluno;
import com.example.sisgesc.DTOs.AlunoPerfilResponse;
import com.example.sisgesc.DTOs.Aviso;
import com.example.sisgesc.DTOs.Funcionario;
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

    // Busca o aluno e já traz os dados da pessoa associada (usando o relacionamento do banco)
    @GET("rest/v1/tb_aluno")
    Call<List<Aluno>> buscarAlunoPorCpf(@Query("fk_cpf") String filtroCpf);

    // Exemplo para buscar um funcionário na tabela 'tb_funcionario'
    @GET("rest/v1/tb_funcionario")
    Call<List<Funcionario>> buscarFuncionarioPorCpf(@Query("fk_cpf_func") String filtroCpf);

    // ... dentro da interface SupabaseService:
    @GET("rest/v1/tb_pessoa")
    Call<List<Pessoa>> buscarPessoaPorCpf(@Query("pk_cpf") String filtroCpf);

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

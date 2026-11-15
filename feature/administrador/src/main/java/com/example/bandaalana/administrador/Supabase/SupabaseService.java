package com.example.bandaalana.administrador.Supabase;

import com.example.bandaalana.administrador.DTOs.AlunoPerfilResponse;
import com.example.bandaalana.administrador.DTOs.Aviso;
import com.example.bandaalana.administrador.DTOs.FuncionarioLoginResponse;
import com.example.bandaalana.administrador.DTOs.MatriculaResponse;
import com.example.bandaalana.administrador.DTOs.NovoAviso;
import com.example.bandaalana.administrador.DTOs.Pessoa;
import com.example.navigation.model.LoginResponse; // 1. NOVO IMPORT

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    // ===== 2. NOVO LOGIN CENTRALIZADO (tb_login) =====
    // Mantém sua segurança: CPF, senha e ativo=eq.true vão como filtro na URL.
    @GET("rest/v1/tb_login")
    Call<List<LoginResponse>> realizarLogin(
            @Query("fk_cpf") String filtroCpf,         // Ex: "eq.12345678901"
            @Query("senha_hash") String filtroSenha,   // Ex: "eq.123456"
            @Query("ativo") String filtroAtivo,        // Ex: "eq.true"
            @Query("select") String select,            // Ex: "fk_cpf,trocar_senha,ativo"
            @Query("limit") int limite);

    // ===== 3. VALIDAR PERFIL DE FUNCIONÁRIO / ADMIN =====
    // Depois que realizarLogin() retornar sucesso, chama este método sem senha
    // apenas para confirmar que esse CPF é funcionário e buscar o cargo dele.
    @GET("rest/v1/tb_funcionario")
    Call<List<FuncionarioLoginResponse>> buscarFuncionarioPorCpf(
            @Query("fk_cpf_func") String filtroCpf,
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

    // ===== 4. ALTERAR SENHA NA tb_login =====
    // Filtra por fk_cpf e senha_hash atual.
    // Usa Map<String, Object> para permitir mandar "senha_hash" (String) e "trocar_senha" (Boolean).
    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/tb_login")
    Call<List<LoginResponse>> alterarSenha(
            @Query("fk_cpf") String filtroCpf,
            @Query("senha_hash") String filtroSenhaAtual,
            @Query("select") String select,
            @Body Map<String, Object> campos);

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

    // 1. Listar todos os avisos do mais recente para o mais antigo
    @GET("rest/v1/tb_aviso?select=pk_id_aviso,titulo_aviso,descricao_aviso,data_envio&order=data_envio.desc")
    Call<List<Aviso>> listarAvisos();

    // 2. Criar um novo aviso
    @Headers("Prefer: return=minimal")
    @POST("rest/v1/tb_aviso")
    Call<Void> criarAviso(@Body NovoAviso aviso);

    // 3. Excluir um aviso pelo ID
    @DELETE("rest/v1/tb_aviso")
    Call<Void> excluirAviso(@Query("pk_id_aviso") String filtroId);

    // 4. Editar um aviso existente pelo ID
    @Headers("Prefer: return=minimal")
    @PATCH("rest/v1/tb_aviso")
    Call<Void> editarAviso(
            @Query("pk_id_aviso") String filtroId,
            @Body java.util.Map<String, String> campos);

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/tb_login")
    Call<List<LoginResponse>> alterarSenhaLogin(
            @Query("fk_cpf") String filtroCpf,
            @Query("senha_hash") String filtroSenhaAtual,
            @Query("select") String select,
            @Body Map<String, Object> campos);



    // 1. Verifica se o CPF existe e está ativo na tb_login
    @GET("rest/v1/tb_login")
    Call<List<LoginResponse>> verificarCpfLogin(
            @Query("fk_cpf") String cpfFiltro,
            @Query("ativo") String ativoFiltro,
            @Query("select") String select
    );

    // 2. Atualiza a senha_hash na tb_login
    @PATCH("rest/v1/tb_login")
    Call<Void> atualizarSenha(
            @Query("fk_cpf") String cpfFiltro,       // Ex: "eq.12345678910"
            @Body Map<String, Object> body           // Ex: {"senha_hash": "novaSenha", "trocar_senha": false}
    );
}
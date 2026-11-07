package com.example.sisgesc.Repository;

import com.example.sisgesc.DTOs.FuncionarioLoginResponse;
import com.example.sisgesc.DTOs.MatriculaResponse;
import com.example.sisgesc.DTOs.UsuarioLogado;
import com.example.sisgesc.Supabase.SupabaseClient;
import com.example.sisgesc.Supabase.SupabaseService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Login único do app: descobre quem está entrando e com qual perfil.
public class AuthRepository {

    private static final String STATUS_ATIVO = "eq.Ativo";
    private static final String SELECT_ALUNO = "id_matricula";
    private static final String SELECT_FUNCIONARIO = "fk_cpf_func,tb_funcionario_cargo(data_saida,tb_cargo(cargo))";

    private final SupabaseService service;

    public AuthRepository() {
        this.service = SupabaseClient.getClient().create(SupabaseService.class);
    }

    // Procura primeiro entre os alunos e depois entre os funcionários.
    public void entrar(String cpf, String senha, RepositoryCallback<UsuarioLogado> callback) {
        service.entrarComoAluno("eq." + cpf, "eq." + senha, STATUS_ATIVO, SELECT_ALUNO, 1)
                .enqueue(new Callback<List<MatriculaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MatriculaResponse>> call, Response<List<MatriculaResponse>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            avisarErroDoServidor(response.code(), callback);
                        } else if (response.body().isEmpty()) {
                            entrarComoFuncionario(cpf, senha, callback);
                        } else {
                            callback.onSucesso(new UsuarioLogado(cpf, UsuarioLogado.Perfil.ALUNO));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MatriculaResponse>> call, Throwable t) {
                        avisarFalhaDeConexao(t, callback);
                    }
                });
    }

    private void entrarComoFuncionario(String cpf, String senha, RepositoryCallback<UsuarioLogado> callback) {
        service.entrarComoFuncionario("eq." + cpf, "eq." + senha, SELECT_FUNCIONARIO, 1)
                .enqueue(new Callback<List<FuncionarioLoginResponse>>() {
                    @Override
                    public void onResponse(Call<List<FuncionarioLoginResponse>> call, Response<List<FuncionarioLoginResponse>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            avisarErroDoServidor(response.code(), callback);
                        } else if (response.body().isEmpty()) {
                            callback.onErro("CPF ou senha incorretos.");
                        } else {
                            UsuarioLogado.Perfil perfil = response.body().get(0).isAdministrador()
                                    ? UsuarioLogado.Perfil.ADMINISTRADOR
                                    : UsuarioLogado.Perfil.PROFESSOR;
                            callback.onSucesso(new UsuarioLogado(cpf, perfil));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FuncionarioLoginResponse>> call, Throwable t) {
                        avisarFalhaDeConexao(t, callback);
                    }
                });
    }

    private void avisarErroDoServidor(int codigo, RepositoryCallback<UsuarioLogado> callback) {
        callback.onErro("O servidor recusou a operação (HTTP " + codigo + ").");
    }

    private void avisarFalhaDeConexao(Throwable erro, RepositoryCallback<UsuarioLogado> callback) {
        callback.onErro("Falha na comunicação com o servidor: " + erro.getMessage());
    }
}

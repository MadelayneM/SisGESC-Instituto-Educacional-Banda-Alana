package com.example.bandaalana.feature.aluno.Repository;

import com.example.bandaalana.feature.aluno.DTOs.FuncionarioLoginResponse;
import com.example.bandaalana.feature.aluno.DTOs.MatriculaResponse;
import com.example.bandaalana.feature.aluno.DTOs.UsuarioLogado;
import com.example.bandaalana.feature.aluno.Supabase.SupabaseClient;
import com.example.bandaalana.feature.aluno.Supabase.SupabaseService;
import com.example.navigation.model.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Login único do app: valida na tb_login e depois descobre o perfil do usuário.
public class AuthRepository {

    private static final String ATIVO_TRUE = "eq.true";
    private static final String STATUS_ATIVO = "eq.Ativo";
    private static final String SELECT_LOGIN = "fk_cpf,trocar_senha,ativo";
    private static final String SELECT_ALUNO = "id_matricula";
    private static final String SELECT_FUNCIONARIO = "fk_cpf_func,tb_funcionario_cargo(data_saida,tb_cargo(cargo))";

    private final SupabaseService service;

    public AuthRepository() {
        this.service = SupabaseClient.getClient().create(SupabaseService.class);
    }

    // 1. Valida CPF, senha e status ativo na tabela centralizada tb_login
    public void entrar(String cpf, String senha, RepositoryCallback<UsuarioLogado> callback) {
        service.realizarLogin("eq." + cpf, "eq." + senha, ATIVO_TRUE, SELECT_LOGIN, 1)
                .enqueue(new Callback<List<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<List<LoginResponse>> call, Response<List<LoginResponse>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            avisarErroDoServidor(response.code(), callback);
                        } else if (response.body().isEmpty()) {
                            callback.onErro("CPF ou senha incorretos, ou usuário inativo.");
                        } else {
                            LoginResponse login = response.body().get(0);
                            descobrirPerfilFuncionario(cpf, login.trocar_senha, callback);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<LoginResponse>> call, Throwable t) {
                        avisarFalhaDeConexao(t, callback);
                    }
                });
    }

    // 2. Verifica primeiro se o CPF autenticado pertence a um Funcionário (Admin ou Professor)
    private void descobrirPerfilFuncionario(String cpf, boolean trocarSenha, RepositoryCallback<UsuarioLogado> callback) {
        service.buscarFuncionarioPorCpf("eq." + cpf, SELECT_FUNCIONARIO, 1)
                .enqueue(new Callback<List<FuncionarioLoginResponse>>() {
                    @Override
                    public void onResponse(Call<List<FuncionarioLoginResponse>> call, Response<List<FuncionarioLoginResponse>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            avisarErroDoServidor(response.code(), callback);
                        } else if (response.body().isEmpty()) {
                            descobrirPerfilAluno(cpf, trocarSenha, callback);
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

    // 3. Verifica se o CPF autenticado possui matrícula ativa como Aluno
    private void descobrirPerfilAluno(String cpf, boolean trocarSenha, RepositoryCallback<UsuarioLogado> callback) {
        service.buscarMatriculas("eq." + cpf, STATUS_ATIVO, null, 1, SELECT_ALUNO)
                .enqueue(new Callback<List<MatriculaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MatriculaResponse>> call, Response<List<MatriculaResponse>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            avisarErroDoServidor(response.code(), callback);
                        } else if (response.body().isEmpty()) {
                            callback.onErro("Usuário sem vínculo ativo (matrícula ou cargo).");
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

    private void avisarErroDoServidor(int codigo, RepositoryCallback<UsuarioLogado> callback) {
        callback.onErro("O servidor recusou a operação (HTTP " + codigo + ").");
    }

    private void avisarFalhaDeConexao(Throwable erro, RepositoryCallback<UsuarioLogado> callback) {
        callback.onErro("Falha na comunicação com o servidor: " + erro.getMessage());
    }
}
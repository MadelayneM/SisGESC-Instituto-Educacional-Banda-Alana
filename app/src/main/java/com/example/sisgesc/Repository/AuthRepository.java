package com.example.sisgesc.Repository;

import com.example.sisgesc.DTOs.Aluno;
import com.example.sisgesc.DTOs.Funcionario;
import com.example.sisgesc.Supabase.SupabaseClient;
import com.example.sisgesc.Supabase.SupabaseService;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final SupabaseService apiService;

    public AuthRepository() {
        // Inicializa o serviço do Retrofit configurado anteriormente
        this.apiService = SupabaseClient.getClient().create(SupabaseService.class);
    }

    // Interface de callback para retornar o resultado de forma assíncrona para a Activity
    public interface LoginCallback {
        void onAlunoEncontrado(Aluno aluno);
        void onFuncionarioEncontrado(Funcionario funcionario, boolean isAdministrador);
        void onError(String mensagemErro);
    }

    /** Tenta buscar o usuário pelo CPF nas tabelas correspondentes.
     * (No futuro, você pode adaptar para buscar por email, dependendo de como estruturou o login).*/
    public void verificarUsuarioPorCpf(String cpf, LoginCallback callback) {

        // 1. Primeiro tenta buscar na tabela de Alunos
        apiService.buscarAlunoPorCpf("eq." + cpf).enqueue(new Callback<List<Aluno>>() {
            @Override
            public void onResponse(Call<List<Aluno>> call, Response<List<Aluno>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Encontrou na tabela de alunos!
                    Aluno aluno = response.body().get(0);
                    callback.onAlunoEncontrado(aluno);
                } else {
                    // Se não é aluno, tenta buscar na tabela de funcionários (Professores / Admins)
                    verificarFuncionario(cpf, callback);
                }
            }

            @Override
            public void onFailure(Call<List<Aluno>> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    private void verificarFuncionario(String cpf, LoginCallback callback) {
        apiService.buscarFuncionarioPorCpf("eq." + cpf).enqueue(new Callback<List<Funcionario>>() {
            @Override
            public void onResponse(Call<List<Funcionario>> call, Response<List<Funcionario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Funcionario funcionario = response.body().get(0);

                    // Aqui você faria a regra para diferenciar Admin de Professor
                    // (ex: consultando a tabela de cargos ligada ao funcionário)
                    boolean isAdmin = false; // Defina a lógica do cargo aqui

                    callback.onFuncionarioEncontrado(funcionario, isAdmin);
                } else {
                    callback.onError("Usuário não encontrado ou credenciais inválidas.");
                }
            }

            @Override
            public void onFailure(Call<List<Funcionario>> call, Throwable t) {
                callback.onError("Erro de conexão ao buscar funcionário: " + t.getMessage());
            }
        });
    }
}
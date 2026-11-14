package com.example.bandaalana.feature.aluno.DTOs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Espelha o JSON do funcionário com os cargos dele. Usado só no login.
public class FuncionarioLoginResponse {

    @SerializedName("tb_funcionario_cargo") public List<VinculoCargo> cargos;

    // Coordenador entra como Administrador. Um cargo com data de saída já foi encerrado.
    public boolean isAdministrador() {
        if (cargos == null) return false;
        for (VinculoCargo vinculo : cargos) {
            if (vinculo.dataSaida == null && vinculo.cargo != null && "Coordenador".equals(vinculo.cargo.nome)) {
                return true;
            }
        }
        return false;
    }

    public static class VinculoCargo {
        @SerializedName("data_saida") public String dataSaida;
        @SerializedName("tb_cargo") public Cargo cargo;
    }

    public static class Cargo {
        @SerializedName("cargo") public String nome;
    }
}

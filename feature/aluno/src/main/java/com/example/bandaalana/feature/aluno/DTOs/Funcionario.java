package com.example.bandaalana.feature.aluno.DTOs;

public class Funcionario extends Pessoa {
    private String fk_cpf_func;

    public Funcionario() {
        super();
    }

    public String getFk_cpf_func() { return fk_cpf_func; }
    public void setFk_cpf_func(String fk_cpf_func) {
        this.fk_cpf_func = fk_cpf_func;
        super.setPk_cpf(fk_cpf_func);
    }
}

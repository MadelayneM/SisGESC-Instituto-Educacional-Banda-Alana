package com.example.bandaalana.feature.aluno.DTOs;

public class Pessoa {
    private String pk_cpf;
    private String primeiro_nome;
    private String sobrenome;
    private String data_nasc;
    private String imagem_perfil;

    // Construtor vazio (necessário para bibliotecas de mapeamento JSON como Gson)
    public Pessoa() {}



    public Pessoa(String pk_cpf, String primeiro_nome, String sobrenome, String data_nasc, String imagem_perfil) {
        this.pk_cpf = pk_cpf;
        this.primeiro_nome = primeiro_nome;
        this.sobrenome = sobrenome;
        this.data_nasc = data_nasc;
        this.imagem_perfil = imagem_perfil;
    }

    // Getters e Setters
    public String getPk_cpf() { return pk_cpf; }
    public void setPk_cpf(String pk_cpf) { this.pk_cpf = pk_cpf; }

    public String getPrimeiro_nome() { return primeiro_nome; }
    public void setPrimeiro_nome(String primeiro_nome) { this.primeiro_nome = primeiro_nome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getData_nasc() { return data_nasc; }
    public void setData_nasc(String data_nasc) { this.data_nasc = data_nasc; }

    public String getImagem_perfil() { return imagem_perfil; }
    public void setImagem_perfil(String imagem_perfil) { this.imagem_perfil = imagem_perfil; }
}

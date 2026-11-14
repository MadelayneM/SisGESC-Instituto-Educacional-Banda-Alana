package com.example.bandaalana.feature.aluno.DTOs;

// Dados atuais do aluno para preencher a tela de "Editar dados".
// O que ainda não tiver cadastro vem null.
public class AlunoDadosEdicao {

    // Valores usados hoje na coluna tipo_telefone do banco
    public static final String[] TIPOS_TELEFONE = {"Celular", "Comercial", "Residencial"};

    private final String email;
    private final String ddd;
    private final String numeroTelefone;
    private final String tipoTelefone;
    private final String cep;
    private final String logradouro;
    private final String bairro;
    private final String cidade;
    private final String uf;
    private final String numeroEndereco;
    private final String complemento;

    public AlunoDadosEdicao(String email, String ddd, String numeroTelefone, String tipoTelefone,
                            String cep, String logradouro, String bairro, String cidade, String uf,
                            String numeroEndereco, String complemento) {
        this.email = email;
        this.ddd = ddd;
        this.numeroTelefone = numeroTelefone;
        this.tipoTelefone = tipoTelefone;
        this.cep = cep;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.numeroEndereco = numeroEndereco;
        this.complemento = complemento;
    }

    public String getEmail() { return email; }
    public String getDdd() { return ddd; }
    public String getNumeroTelefone() { return numeroTelefone; }
    public String getTipoTelefone() { return tipoTelefone; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public String getNumeroEndereco() { return numeroEndereco; }
    public String getComplemento() { return complemento; }
}

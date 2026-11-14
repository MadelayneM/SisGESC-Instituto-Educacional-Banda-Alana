package com.example.bandaalana.professor.DTOs;

import java.util.List;

// Objeto simples com os dados da tela Perfil do professor.
// Também traz o telefone separado em DDD e número, para a tela "Editar dados".
public class ProfessorPerfil {

    // Valores aceitos pelo enum tipo_telefone do banco
    public static final String[] TIPOS_TELEFONE = {"Celular", "Residencial", "Comercial"};

    private final String primeiroNome;
    private final String nomeCompleto;
    private final String fotoUrl;
    private final String cargo;
    private final List<String> funcoes;
    private final String email;
    private final String ddd;
    private final String numeroTelefone;
    private final String tipoTelefone;

    public ProfessorPerfil(String primeiroNome, String nomeCompleto, String fotoUrl, String cargo,
                           List<String> funcoes, String email, String ddd, String numeroTelefone,
                           String tipoTelefone) {
        this.primeiroNome = primeiroNome;
        this.nomeCompleto = nomeCompleto;
        this.fotoUrl = fotoUrl;
        this.cargo = cargo;
        this.funcoes = funcoes;
        this.email = email;
        this.ddd = ddd;
        this.numeroTelefone = numeroTelefone;
        this.tipoTelefone = tipoTelefone;
    }

    public String getPrimeiroNome() { return primeiroNome; }
    public String getNomeCompleto() { return nomeCompleto; }
    // Endereço da foto, ou null se não tiver foto
    public String getFotoUrl() { return fotoUrl; }
    // Cargo atual em tb_cargo, ex.: Monitor
    public String getCargo() { return cargo; }
    // Funções nas turmas, sem repetir, ex.: [Monitor Auxiliar, Monitor Multiplicador]
    public List<String> getFuncoes() { return funcoes; }
    // Os campos abaixo vêm null quando não estão cadastrados
    public String getEmail() { return email; }
    public String getDdd() { return ddd; }
    public String getNumeroTelefone() { return numeroTelefone; }
    // Um dos valores de TIPOS_TELEFONE
    public String getTipoTelefone() { return tipoTelefone; }

    // Texto pronto para a tela, ex.: "(11) 992220002"
    public String getTelefoneFormatado() {
        if (ddd == null || numeroTelefone == null) return null;
        return "(" + ddd + ") " + numeroTelefone;
    }
}

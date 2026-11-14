package com.example.bandaalana.administrador.DTOs;

// Dados da tela Perfil do Administrador (e os valores atuais para "Editar dados").
public class AdminPerfil {

    // Valores aceitos pelo enum tipo_telefone do banco
    public static final String[] TIPOS_TELEFONE = {"Celular", "Residencial", "Comercial"};

    private final String primeiroNome;
    private final String nomeCompleto;
    private final String fotoUrl;
    private final String cargo;
    private final String email;
    private final String ddd;
    private final String numeroTelefone;
    private final String tipoTelefone;

    public AdminPerfil(String primeiroNome, String nomeCompleto, String fotoUrl, String cargo,
                       String email, String ddd, String numeroTelefone, String tipoTelefone) {
        this.primeiroNome = primeiroNome;
        this.nomeCompleto = nomeCompleto;
        this.fotoUrl = fotoUrl;
        this.cargo = cargo;
        this.email = email;
        this.ddd = ddd;
        this.numeroTelefone = numeroTelefone;
        this.tipoTelefone = tipoTelefone;
    }

    public String getPrimeiroNome() { return primeiroNome; }
    public String getNomeCompleto() { return nomeCompleto; }
    // Endereço da foto, ou null se não tiver foto
    public String getFotoUrl() { return fotoUrl; }
    // Cargo atual, ex.: Coordenador
    public String getCargo() { return cargo; }
    // Os campos abaixo vêm null quando não estão cadastrados
    public String getEmail() { return email; }
    public String getDdd() { return ddd; }
    public String getNumeroTelefone() { return numeroTelefone; }
    // Um dos valores de TIPOS_TELEFONE
    public String getTipoTelefone() { return tipoTelefone; }

    // Texto pronto para a tela, ex.: "(11) 991110001"
    public String getTelefoneFormatado() {
        if (ddd == null || numeroTelefone == null) return null;
        return "(" + ddd + ") " + numeroTelefone;
    }
}

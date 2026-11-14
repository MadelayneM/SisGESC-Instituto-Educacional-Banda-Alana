package com.example.bandaalana.feature.aluno.DTOs;

// Objeto simples com os dados da tela de Perfil do aluno.
// Campos sem cadastro no banco (foto, e-mail, telefones, responsável) podem vir null.
public class AlunoPerfil {
    private final String primeiroNome;
    private final String nomeCompleto;
    private final String fotoUrl;
    private final String email;
    private final String telefone;
    private final String nomeResponsavel;
    private final String telefoneResponsavel;

    public AlunoPerfil(String primeiroNome, String nomeCompleto, String fotoUrl, String email, String telefone,
                       String nomeResponsavel, String telefoneResponsavel) {
        this.primeiroNome = primeiroNome;
        this.nomeCompleto = nomeCompleto;
        this.fotoUrl = fotoUrl;
        this.email = email;
        this.telefone = telefone;
        this.nomeResponsavel = nomeResponsavel;
        this.telefoneResponsavel = telefoneResponsavel;
    }

    public String getPrimeiroNome() { return primeiroNome; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getFotoUrl() { return fotoUrl; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getNomeResponsavel() { return nomeResponsavel; }
    public String getTelefoneResponsavel() { return telefoneResponsavel; }
}

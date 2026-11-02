package com.example.layout_appdev.admin.model;

public class Aluno {

    public enum Status { ATIVO, PENDENTE, INATIVO }

    private final String nome;
    private final String turma;
    private final Status status;
    private String nascimento = "";
    private String cpf = "";
    private String telefone = "";
    private String email = "";
    private String responsavel = "";
    private String telefoneResponsavel = "";
    private String instrumento = "";
    private String fotoUri = null;

    public Aluno(String nome, String turma, Status status) {
        this.nome = nome;
        this.turma = turma;
        this.status = status;
    }

    public String getNome() { return nome; }
    public String getTurma() { return turma; }
    public Status getStatus() { return status; }

    public String getNascimento() { return nascimento; }
    public void setNascimento(String v) { nascimento = v; }
    public String getCpf() { return cpf; }
    public void setCpf(String v) { cpf = v; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String v) { telefone = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String v) { responsavel = v; }
    public String getTelefoneResponsavel() { return telefoneResponsavel; }
    public void setTelefoneResponsavel(String v) { telefoneResponsavel = v; }
    public String getInstrumento() { return instrumento; }
    public void setInstrumento(String v) { instrumento = v; }
    public String getFotoUri() { return fotoUri; }
    public void setFotoUri(String v) { fotoUri = v; }
}

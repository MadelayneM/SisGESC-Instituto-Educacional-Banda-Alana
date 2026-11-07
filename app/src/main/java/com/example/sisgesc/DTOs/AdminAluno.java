package com.example.sisgesc.DTOs;

// Um aluno na tela Alunos e na lista "Alunos recentes" do Dashboard.
// Os dados de turma e status são da matrícula mais recente (a ativa, se houver).
public class AdminAluno {

    private final String cpf;
    private final String nome;
    private final String fotoUrl;
    private final Integer idMatricula;
    private final String nomeTurma;
    private final String nomeCurso;
    private final String status;

    public AdminAluno(String cpf, String nome, String fotoUrl, Integer idMatricula,
                      String nomeTurma, String nomeCurso, String status) {
        this.cpf = cpf;
        this.nome = nome;
        this.fotoUrl = fotoUrl;
        this.idMatricula = idMatricula;
        this.nomeTurma = nomeTurma;
        this.nomeCurso = nomeCurso;
        this.status = status;
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    // Endereço da foto, ou null se não tiver foto
    public String getFotoUrl() { return fotoUrl; }
    // Os campos abaixo vêm null se o aluno ainda não tiver matrícula
    public Integer getIdMatricula() { return idMatricula; }
    public String getNomeTurma() { return nomeTurma; }
    public String getNomeCurso() { return nomeCurso; }
    // Um dos valores de NovoAluno.STATUS_MATRICULA, ex.: Ativo
    public String getStatus() { return status; }
}

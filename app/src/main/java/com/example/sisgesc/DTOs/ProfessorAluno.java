package com.example.sisgesc.DTOs;

// Objeto simples com um aluno da lista da tela Alunos.
public class ProfessorAluno {
    private final String cpf;
    private final Integer idMatricula;
    private final String nome;
    private final String fotoUrl;
    private final String nomeTurma;
    private final String nomeCurso;

    public ProfessorAluno(String cpf, Integer idMatricula, String nome, String fotoUrl,
                          String nomeTurma, String nomeCurso) {
        this.cpf = cpf;
        this.idMatricula = idMatricula;
        this.nome = nome;
        this.fotoUrl = fotoUrl;
        this.nomeTurma = nomeTurma;
        this.nomeCurso = nomeCurso;
    }

    public String getCpf() { return cpf; }
    public Integer getIdMatricula() { return idMatricula; }
    // Nome completo, ex.: Mariana Costa Oliveira
    public String getNome() { return nome; }
    // Endereço da foto, ou null se o aluno não tiver foto
    public String getFotoUrl() { return fotoUrl; }
    // Ex.: Violão A
    public String getNomeTurma() { return nomeTurma; }
    // Ex.: Violão
    public String getNomeCurso() { return nomeCurso; }
}

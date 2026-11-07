package com.example.sisgesc.DTOs;

// Objeto simples com os dados de uma turma do professor (telas Início e Minhas Turmas).
public class ProfessorTurma {
    private final String nomeTurma;
    private final String nomeCurso;
    private final String funcao;
    private final String dataAula;
    private final String diaSemana;
    private final String horarioInicio;
    private final String horarioFim;
    private final Integer vagas;
    private final int totalAlunos;

    public ProfessorTurma(String nomeTurma, String nomeCurso, String funcao, String dataAula, String diaSemana,
                          String horarioInicio, String horarioFim, Integer vagas, int totalAlunos) {
        this.nomeTurma = nomeTurma;
        this.nomeCurso = nomeCurso;
        this.funcao = funcao;
        this.dataAula = dataAula;
        this.diaSemana = diaSemana;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.vagas = vagas;
        this.totalAlunos = totalAlunos;
    }

    // Ex.: Violão A. É este nome que vai em buscarChamada e salvarChamada.
    public String getNomeTurma() { return nomeTurma; }
    // Ex.: Violão
    public String getNomeCurso() { return nomeCurso; }
    // Função do professor nesta turma, ex.: Monitor Multiplicador
    public String getFuncao() { return funcao; }
    // Formato do banco: ano-mês-dia, ex.: 2026-11-06
    public String getDataAula() { return dataAula; }
    // Ex.: Sexta-feira
    public String getDiaSemana() { return diaSemana; }
    // Horários no formato 14:00
    public String getHorarioInicio() { return horarioInicio; }
    public String getHorarioFim() { return horarioFim; }
    // Quantidade de alunos prevista para a turma (coluna qtd_alunos)
    public Integer getVagas() { return vagas; }
    // Quantos alunos estão matriculados agora (matrículas com status Ativo)
    public int getTotalAlunos() { return totalAlunos; }
    // O banco não tem status de turma: ela conta como ativa se tiver algum aluno matriculado
    public boolean isAtiva() { return totalAlunos > 0; }
}

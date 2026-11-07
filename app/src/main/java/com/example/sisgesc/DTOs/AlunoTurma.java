package com.example.sisgesc.DTOs;

import java.util.List;

// Objeto simples com os dados da tela Minha Turma.
public class AlunoTurma {
    private final String nomeTurma;
    private final String nomeCurso;
    private final String professor;
    private final String monitor;
    private final String dataAula;
    private final String diaSemana;
    private final String horarioInicio;
    private final String horarioFim;
    private final Integer qtdAlunos;
    private final List<String> colegas;

    public AlunoTurma(String nomeTurma, String nomeCurso, String professor, String monitor,
                      String dataAula, String diaSemana, String horarioInicio, String horarioFim,
                      Integer qtdAlunos, List<String> colegas) {
        this.nomeTurma = nomeTurma;
        this.nomeCurso = nomeCurso;
        this.professor = professor;
        this.monitor = monitor;
        this.dataAula = dataAula;
        this.diaSemana = diaSemana;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.qtdAlunos = qtdAlunos;
        this.colegas = colegas;
    }

    public String getNomeTurma() { return nomeTurma; }
    public String getNomeCurso() { return nomeCurso; }
    // Professor e monitor vêm null se a turma não tiver alguém nessa função
    public String getProfessor() { return professor; }
    public String getMonitor() { return monitor; }
    // Formato do banco: ano-mês-dia, ex.: 2026-11-06
    public String getDataAula() { return dataAula; }
    // Ex.: Sexta-feira
    public String getDiaSemana() { return diaSemana; }
    // Horários no formato 14:00
    public String getHorarioInicio() { return horarioInicio; }
    public String getHorarioFim() { return horarioFim; }
    public Integer getQtdAlunos() { return qtdAlunos; }
    // Nomes em ordem alfabética, sem o próprio aluno
    public List<String> getColegas() { return colegas; }
}

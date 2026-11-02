package com.example.layout_appdev.admin.model;

public class Turma {
    private final String nome;
    private final String professor;
    private final String diaSemana;
    private final String horario;
    private int qtdAlunos;

    public Turma(String nome, String professor, String diaSemana, String horario, int qtdAlunos) {
        this.nome = nome;
        this.professor = professor;
        this.diaSemana = diaSemana;
        this.horario = horario;
        this.qtdAlunos = qtdAlunos;
    }

    public String getNome() { return nome; }
    public String getProfessor() { return professor; }
    public String getDiaSemana() { return diaSemana; }
    public String getHorario() { return horario; }
    public int getQtdAlunos() { return qtdAlunos; }
    public void setQtdAlunos(int qtdAlunos) { this.qtdAlunos = qtdAlunos; }
}

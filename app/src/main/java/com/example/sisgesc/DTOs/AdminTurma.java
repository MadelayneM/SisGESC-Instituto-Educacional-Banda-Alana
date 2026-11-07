package com.example.sisgesc.DTOs;

import java.util.List;

// Uma turma na tela Turmas (activity_item_admin_turma) e em "Consultar frequência".
public class AdminTurma {

    private final String nomeTurma;
    private final String nomeCurso;
    private final List<String> professores;
    private final String dataAula;
    private final String diaSemana;
    private final String horarioInicio;
    private final String horarioFim;
    private final Integer vagas;
    private final int alunosAtivos;
    private final int presencas;
    private final int faltas;

    public AdminTurma(String nomeTurma, String nomeCurso, List<String> professores, String dataAula,
                      String diaSemana, String horarioInicio, String horarioFim, Integer vagas,
                      int alunosAtivos, int presencas, int faltas) {
        this.nomeTurma = nomeTurma;
        this.nomeCurso = nomeCurso;
        this.professores = professores;
        this.dataAula = dataAula;
        this.diaSemana = diaSemana;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.vagas = vagas;
        this.alunosAtivos = alunosAtivos;
        this.presencas = presencas;
        this.faltas = faltas;
    }

    public String getNomeTurma() { return nomeTurma; }
    public String getNomeCurso() { return nomeCurso; }
    // Nomes da equipe da turma
    public List<String> getProfessores() { return professores; }
    // Data no formato do banco, ex.: 2026-11-10
    public String getDataAula() { return dataAula; }
    // Ex.: Terça-feira
    public String getDiaSemana() { return diaSemana; }
    // Ex.: 08:00
    public String getHorarioInicio() { return horarioInicio; }
    public String getHorarioFim() { return horarioFim; }
    // Limite de alunos (qtd_alunos)
    public Integer getVagas() { return vagas; }
    // Matrículas com status Ativo
    public int getAlunosAtivos() { return alunosAtivos; }
    public int getPresencas() { return presencas; }
    public int getFaltas() { return faltas; }

    // Frequência da turma de 0 a 100, ou null se ainda não houve chamada
    public Integer getFrequencia() {
        int total = presencas + faltas;
        return total == 0 ? null : Math.round(presencas * 100f / total);
    }

    // Para tvProfessor, ex.: "Rafael Gomes" ou "Sem professor"
    public String getProfessoresTexto() {
        return professores == null || professores.isEmpty() ? "Sem professor" : juntar(professores);
    }

    // Igual a String.join(", ", lista), que só existe a partir do Android 8 (o app aceita o 7)
    static String juntar(List<String> lista) {
        StringBuilder texto = new StringBuilder();
        for (String item : lista) {
            if (texto.length() > 0) texto.append(", ");
            texto.append(item);
        }
        return texto.toString();
    }
}

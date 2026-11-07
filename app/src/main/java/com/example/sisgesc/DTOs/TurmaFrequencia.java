package com.example.sisgesc.DTOs;

import java.util.List;

// Objeto simples com o histórico de frequência de uma turma (tela Histórico de Frequência).
public class TurmaFrequencia {
    private final String nomeTurma;
    private final String nomeCurso;
    private final List<Aula> aulas;

    public TurmaFrequencia(String nomeTurma, String nomeCurso, List<Aula> aulas) {
        this.nomeTurma = nomeTurma;
        this.nomeCurso = nomeCurso;
        this.aulas = aulas;
    }

    public String getNomeTurma() { return nomeTurma; }
    public String getNomeCurso() { return nomeCurso; }
    // Aulas com chamada feita, da mais antiga para a mais recente. Lista vazia se não houver.
    public List<Aula> getAulas() { return aulas; }

    // Resumo de uma aula: quantos alunos vieram e quantos faltaram
    public static class Aula {
        private final String dataAula;
        private final int presentes;
        private final int ausentes;

        public Aula(String dataAula, int presentes, int ausentes) {
            this.dataAula = dataAula;
            this.presentes = presentes;
            this.ausentes = ausentes;
        }

        // Formato do banco: ano-mês-dia. Use este valor em buscarChamada para abrir a chamada da aula.
        public String getDataAula() { return dataAula; }
        // Ex.: "25/08 — Terça"
        public String getDataFormatada() { return FormatoData.diaComSemana(dataAula); }
        public int getPresentes() { return presentes; }
        public int getAusentes() { return ausentes; }
        public int getTotal() { return presentes + ausentes; }
    }
}

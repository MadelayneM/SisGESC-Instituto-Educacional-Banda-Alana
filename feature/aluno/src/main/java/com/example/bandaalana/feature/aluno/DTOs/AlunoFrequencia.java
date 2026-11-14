package com.example.bandaalana.feature.aluno.DTOs;

import java.util.List;

// Objeto simples com os dados da tela Minha Frequência.
public class AlunoFrequencia {
    private final int percentual;
    private final int totalPresencas;
    private final int totalAusencias;
    private final int totalAulas;
    private final List<RegistroAula> historico;

    public AlunoFrequencia(int percentual, int totalPresencas, int totalAusencias, int totalAulas,
                           List<RegistroAula> historico) {
        this.percentual = percentual;
        this.totalPresencas = totalPresencas;
        this.totalAusencias = totalAusencias;
        this.totalAulas = totalAulas;
        this.historico = historico;
    }

    // Porcentagem de 0 a 100, sem o símbolo %
    public int getPercentual() { return percentual; }
    public int getTotalPresencas() { return totalPresencas; }
    public int getTotalAusencias() { return totalAusencias; }
    public int getTotalAulas() { return totalAulas; }
    // Da aula mais antiga para a mais recente
    public List<RegistroAula> getHistorico() { return historico; }

    public static class RegistroAula {
        private final String dataAula;
        private final boolean presente;
        private final String observacao;

        public RegistroAula(String dataAula, boolean presente, String observacao) {
            this.dataAula = dataAula;
            this.presente = presente;
            this.observacao = observacao;
        }

        // Formato do banco: ano-mês-dia, ex.: 2026-08-25
        public String getDataAula() { return dataAula; }
        // Texto para a tela, ex.: 25/08 — Segunda
        public String getDataFormatada() { return FormatoData.diaComSemana(dataAula); }
        public boolean isPresente() { return presente; }
        public String getObservacao() { return observacao; }
    }
}

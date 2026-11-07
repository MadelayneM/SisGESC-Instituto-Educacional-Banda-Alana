package com.example.sisgesc.DTOs;

// Números da "Visão geral" do Dashboard do Administrador.
public class AdminResumo {

    private final String primeiroNome;
    private final int totalAlunos;
    private final int totalTurmas;
    private final int totalProfessores;
    private final Integer frequenciaGeral;

    public AdminResumo(String primeiroNome, int totalAlunos, int totalTurmas,
                       int totalProfessores, Integer frequenciaGeral) {
        this.primeiroNome = primeiroNome;
        this.totalAlunos = totalAlunos;
        this.totalTurmas = totalTurmas;
        this.totalProfessores = totalProfessores;
        this.frequenciaGeral = frequenciaGeral;
    }

    // Para o "Bem-vindo, ..."
    public String getPrimeiroNome() { return primeiroNome; }
    // Alunos cadastrados em tb_aluno
    public int getTotalAlunos() { return totalAlunos; }
    // Turmas cadastradas em tb_turma
    public int getTotalTurmas() { return totalTurmas; }
    // Funcionários com cargo ativo que não é Coordenador
    public int getTotalProfessores() { return totalProfessores; }
    // Presenças ÷ total de chamadas, de 0 a 100. Null se ainda não houver nenhuma chamada.
    public Integer getFrequenciaGeral() { return frequenciaGeral; }

    // Texto pronto para o card, ex.: "89%" ou "--" sem chamadas
    public String getFrequenciaTexto() {
        return frequenciaGeral == null ? "--" : frequenciaGeral + "%";
    }
}

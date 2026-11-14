package com.example.bandaalana.feature.aluno.DTOs;

// Objeto simples com os dados da tela inicial do aluno.
// Os avisos vêm à parte, de AlunoRepository.buscarAvisos.
public class AlunoDashboard {
    private final String primeiroNome;
    private final String nomeCompleto;
    private final String fotoUrl;
    private final AlunoTurma turma;
    private final AlunoFrequencia frequencia;

    public AlunoDashboard(String primeiroNome, String nomeCompleto, String fotoUrl,
                          AlunoTurma turma, AlunoFrequencia frequencia) {
        this.primeiroNome = primeiroNome;
        this.nomeCompleto = nomeCompleto;
        this.fotoUrl = fotoUrl;
        this.turma = turma;
        this.frequencia = frequencia;
    }

    public String getPrimeiroNome() { return primeiroNome; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getFotoUrl() { return fotoUrl; }
    public AlunoTurma getTurma() { return turma; }
    public AlunoFrequencia getFrequencia() { return frequencia; }
}

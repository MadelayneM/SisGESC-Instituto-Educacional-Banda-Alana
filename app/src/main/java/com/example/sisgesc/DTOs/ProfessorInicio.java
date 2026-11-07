package com.example.sisgesc.DTOs;

import java.util.List;

// Objeto simples com os dados da tela Início do professor.
public class ProfessorInicio {
    private final String primeiroNome;
    private final List<ProfessorTurma> turmas;

    public ProfessorInicio(String primeiroNome, List<ProfessorTurma> turmas) {
        this.primeiroNome = primeiroNome;
        this.turmas = turmas;
    }

    // Para o "Bem-vindo de volta, Rafael!"
    public String getPrimeiroNome() { return primeiroNome; }
    // Em ordem alfabética pelo nome da turma
    public List<ProfessorTurma> getTurmas() { return turmas; }
}

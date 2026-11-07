package com.example.sisgesc.DTOs;

// Um aluno na tela de chamada ("Fazer chamada").
// A tela muda a presença com setPresente e depois manda a lista inteira para salvarChamada.
public class ChamadaAluno {
    private final Integer idMatricula;
    private final Integer idFrequencia;
    private final String nome;
    private final String fotoUrl;
    private Boolean presente;
    private String observacao;

    public ChamadaAluno(Integer idMatricula, Integer idFrequencia, String nome, String fotoUrl,
                        Boolean presente, String observacao) {
        this.idMatricula = idMatricula;
        this.idFrequencia = idFrequencia;
        this.nome = nome;
        this.fotoUrl = fotoUrl;
        this.presente = presente;
        this.observacao = observacao;
    }

    public Integer getIdMatricula() { return idMatricula; }
    // null quando a chamada deste aluno ainda não foi salva nesta data
    public Integer getIdFrequencia() { return idFrequencia; }
    public String getNome() { return nome; }
    public String getFotoUrl() { return fotoUrl; }

    // null quando o professor ainda não marcou o aluno
    public Boolean getPresente() { return presente; }
    public void setPresente(Boolean presente) { this.presente = presente; }

    // Ex.: "Falta justificada: consulta médica". Pode ser null.
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}

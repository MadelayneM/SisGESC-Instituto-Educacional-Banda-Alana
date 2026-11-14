package com.example.bandaalana.administrador.DTOs;

// Um item para os Spinners dos cadastros (curso, turma, professor, cargo, tipo de monitor).
// toString() devolve o texto, então a lista pode ir direto num ArrayAdapter.
public class AdminOpcao {

    private final String valor;
    private final String texto;

    public AdminOpcao(String valor, String texto) {
        this.valor = valor;
        this.texto = texto;
    }

    // O que vai para o cadastro: código do curso/cargo/tipo, nome da turma ou CPF do professor
    public String getValor() { return valor; }
    // O que aparece no Spinner
    public String getTexto() { return texto; }

    @Override
    public String toString() { return texto; }
}

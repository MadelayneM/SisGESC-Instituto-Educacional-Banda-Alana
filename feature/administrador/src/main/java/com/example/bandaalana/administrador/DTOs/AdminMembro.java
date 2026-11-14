package com.example.bandaalana.administrador.DTOs;

import java.util.List;

// Um funcionário na tela Equipe (activity_item_admin_membro).
public class AdminMembro {

    private final String cpf;
    private final String nome;
    private final String fotoUrl;
    private final String cargo;
    private final List<String> turmas;

    public AdminMembro(String cpf, String nome, String fotoUrl, String cargo, List<String> turmas) {
        this.cpf = cpf;
        this.nome = nome;
        this.fotoUrl = fotoUrl;
        this.cargo = cargo;
        this.turmas = turmas;
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getFotoUrl() { return fotoUrl; }
    // Cargo atual em tb_cargo, ex.: Coordenador ou Monitor
    public String getCargo() { return cargo; }
    // Nomes das turmas em que trabalha
    public List<String> getTurmas() { return turmas; }

    // Para o círculo com as iniciais (tvIniciais), ex.: "João Silva" -> "JS"
    public String getIniciais() {
        String[] partes = nome == null ? new String[0] : nome.trim().split("\\s+");
        if (partes.length == 0 || partes[0].isEmpty()) return "";
        String iniciais = partes[0].substring(0, 1);
        if (partes.length > 1) iniciais += partes[partes.length - 1].substring(0, 1);
        return iniciais.toUpperCase();
    }

    // Para tvDetalhe, ex.: "Violão A, Coral A" ou "Sem turmas"
    public String getTurmasTexto() {
        return turmas == null || turmas.isEmpty() ? "Sem turmas" : AdminTurma.juntar(turmas);
    }
}

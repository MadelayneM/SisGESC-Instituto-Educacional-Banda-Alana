package com.example.layout_appdev.admin.model;

/** Membro da equipe: professor(a), monitor(a) ou coordenador(a). */
public class Membro {
    public static final String[] CARGOS = {
            "Professor", "Professora", "Monitor", "Monitora", "Coordenador", "Coordenadora"
    };

    private final String nome;
    private final String turmas;
    private final String cargo;
    private String telefone = "";
    private String email = "";

    public Membro(String nome, String turmas, String cargo) {
        this.nome = nome;
        this.turmas = turmas;
        this.cargo = cargo;
    }

    public String getNome() { return nome; }
    public String getTurmas() { return turmas; }
    public String getCargo() { return cargo; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String v) { telefone = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }

    public boolean isProfessor() { return cargo.startsWith("Professor"); }
}

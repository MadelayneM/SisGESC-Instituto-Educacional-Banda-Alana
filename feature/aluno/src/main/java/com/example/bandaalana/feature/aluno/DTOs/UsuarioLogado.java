package com.example.bandaalana.feature.aluno.DTOs;

// Quem acabou de entrar no app e com qual perfil.
public class UsuarioLogado {

    public enum Perfil { ALUNO, PROFESSOR, ADMINISTRADOR }

    private final String cpf;
    private final Perfil perfil;

    public UsuarioLogado(String cpf, Perfil perfil) {
        this.cpf = cpf;
        this.perfil = perfil;
    }

    public String getCpf() { return cpf; }
    public Perfil getPerfil() { return perfil; }
}

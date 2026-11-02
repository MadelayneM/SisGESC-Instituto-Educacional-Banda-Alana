package com.example.layout_appdev.admin.util;

import com.example.layout_appdev.admin.model.Aluno;
import com.example.layout_appdev.admin.model.Aluno.Status;
import com.example.layout_appdev.admin.model.Membro;
import com.example.layout_appdev.admin.model.PerfilAdmin;
import com.example.layout_appdev.admin.model.Turma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dados de exemplo (iguais aos do protótipo do Figma) para o front-end funcionar
 * antes da integração. Quando o back-end estiver pronto, substituir por chamadas reais.
 * Tudo fica só na memória: ao fechar o app, os dados voltam ao início.
 */
public final class MockData {

    private MockData() {}

    private static final List<Aluno> ALUNOS = new ArrayList<>(Arrays.asList(
            new Aluno("Maria Silva", "Musicalização 01", Status.ATIVO),
            new Aluno("João Santos", "Violão 02", Status.ATIVO),
            new Aluno("Pedro Oliveira", "Musicalização 02", Status.PENDENTE),
            new Aluno("Ana Costa", "Percussão 01", Status.ATIVO),
            new Aluno("Larissa Gomes", "Musicalização 01", Status.ATIVO),
            new Aluno("Rafael Lima", "Violão 02", Status.INATIVO),
            new Aluno("Beatriz Souza", "Musicalização 02", Status.ATIVO),
            new Aluno("Carlos Neto", "Percussão 01", Status.PENDENTE)
    ));

    private static final List<Turma> TURMAS = new ArrayList<>(Arrays.asList(
            new Turma("Musicalização 01", "João Silva", "Quarta-feira", "14:00", 12),
            new Turma("Musicalização 02", "Ana Souza", "Terça-feira", "10:00", 11),
            new Turma("Violão 02", "Ana Souza", "Sexta-feira", "15:00", 10),
            new Turma("Percussão 01", "João Silva", "Terça-feira", "09:00", 8)
    ));

    private static final List<Membro> EQUIPE = new ArrayList<>(Arrays.asList(
            new Membro("João Silva", "Musicalização 01, Percussão 01", "Professor"),
            new Membro("Ana Souza", "Musicalização 02, Violão 02", "Professora"),
            new Membro("Carlos Melo", "Musicalização 01", "Monitor"),
            new Membro("Fernanda Costa", "Violão 02", "Monitora"),
            new Membro("Ricardo Alves", "Todas as turmas", "Coordenador")
    ));

    private static final PerfilAdmin PERFIL = new PerfilAdmin();

    public static List<Aluno> alunos() { return ALUNOS; }
    public static List<Turma> turmas() { return TURMAS; }
    public static List<Membro> equipe() { return EQUIPE; }
    public static PerfilAdmin perfil() { return PERFIL; }
}

package com.example.bandaalana.professor.DTOs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Espelha o JSON aninhado que o Supabase devolve para um funcionário (tb_funcionario),
// com as turmas em que ele trabalha. Cada consulta pede só os campos de que precisa;
// os outros ficam null. As telas não usam esta classe: elas recebem os objetos simples Professor*.
public class FuncionarioResponse {

    @SerializedName("fk_cpf_func") public String cpf;
    @SerializedName("tb_pessoa") public AlunoPerfilResponse.Pessoa pessoa;
    @SerializedName("tb_funcionario_cargo") public List<FuncionarioLoginResponse.VinculoCargo> cargos;
    @SerializedName("tb_turma_funcionario") public List<TurmaDoFuncionario> turmas;

    // Uma linha de tb_turma_funcionario: a turma e a função do funcionário nela
    public static class TurmaDoFuncionario {
        @SerializedName("tb_tipo_monitor") public MatriculaResponse.TipoMonitor tipoMonitor;
        @SerializedName("tb_turma") public Turma turma;
    }

    public static class Turma {
        @SerializedName("pk_nome_turma") public String nome;
        @SerializedName("qtd_alunos") public Integer qtdAlunos;
        @SerializedName("data_da_aula") public String dataAula;
        @SerializedName("horario_inicio") public String horarioInicio;
        @SerializedName("horario_fim") public String horarioFim;
        @SerializedName("tb_curso") public MatriculaResponse.Curso curso;
        @SerializedName("tb_matricula") public List<Matricula> matriculas;
    }

    // Também é a resposta da consulta da chamada, feita direto em tb_matricula
    public static class Matricula {
        @SerializedName("id_matricula") public Integer idMatricula;
        @SerializedName("fk_cpf") public String cpf;
        @SerializedName("status_matricula") public String status;
        @SerializedName("tb_aluno") public Aluno aluno;
        @SerializedName("tb_frequencia") public List<Frequencia> frequencias;
    }

    public static class Aluno {
        @SerializedName("tb_pessoa") public AlunoPerfilResponse.Pessoa pessoa;
    }

    public static class Frequencia {
        @SerializedName("id_frequencia") public Integer idFrequencia;
        @SerializedName("data_aula") public String dataAula;
        @SerializedName("presenca") public boolean presente;
        @SerializedName("observacao_frequencia") public String observacao;
    }
}

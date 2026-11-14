package com.example.bandaalana.feature.aluno.DTOs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Espelha o JSON de tb_matricula com as tabelas ligadas a ela.
// Cada consulta pede só os campos de que precisa; os outros ficam null.
// A senha_login nunca é pedida, para não trafegar sem necessidade.
public class MatriculaResponse {

    @SerializedName("id_matricula") public Integer idMatricula;
    @SerializedName("fk_cpf") public String cpf;
    @SerializedName("ano") public Integer ano;
    @SerializedName("semestre") public Integer semestre;
    @SerializedName("status_matricula") public String status;

    @SerializedName("tb_aluno") public AlunoPerfilResponse aluno;
    @SerializedName("tb_curso") public Curso curso;
    @SerializedName("tb_turma") public Turma turma;
    @SerializedName("tb_frequencia") public List<Frequencia> frequencias;

    public static class Curso {
        @SerializedName("nome_curso") public String nome;
    }

    public static class Turma {
        @SerializedName("pk_nome_turma") public String nome;
        @SerializedName("qtd_alunos") public Integer qtdAlunos;
        @SerializedName("data_da_aula") public String dataAula;
        @SerializedName("horario_inicio") public String horarioInicio;
        @SerializedName("horario_fim") public String horarioFim;
        @SerializedName("tb_turma_funcionario") public List<TurmaFuncionario> equipe;
        @SerializedName("tb_matricula") public List<MatriculaResponse> matriculas;
    }

    public static class TurmaFuncionario {
        @SerializedName("tb_tipo_monitor") public TipoMonitor tipoMonitor;
        @SerializedName("tb_funcionario") public FuncionarioResumo funcionario;
    }

    public static class TipoMonitor {
        @SerializedName("tipo_monitor") public String tipo;
    }

    public static class FuncionarioResumo {
        @SerializedName("tb_pessoa") public AlunoPerfilResponse.Pessoa pessoa;
    }

    public static class Frequencia {
        @SerializedName("data_aula") public String dataAula;
        @SerializedName("presenca") public boolean presente;
        @SerializedName("observacao_frequencia") public String observacao;
    }
}

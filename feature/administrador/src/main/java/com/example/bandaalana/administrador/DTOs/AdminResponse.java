package com.example.bandaalana.administrador.DTOs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Espelha o JSON que o Supabase devolve nas consultas do módulo Administrador.
// As telas não usam estas classes: elas recebem os objetos simples Admin* e Cadastro*.
// Cada consulta pede só os campos de que precisa; os outros ficam null.
// Algumas classes estendem as dos outros módulos só para acrescentar campos,
// assim os arquivos compartilhados com a main não precisam mudar.
public class AdminResponse {

    // Pessoa com a data de nascimento (usada para preencher os formulários de edição)
    public static class Pessoa extends AlunoPerfilResponse.Pessoa {
        @SerializedName("data_nasc") public String dataNascimento;
    }

    // Linha de tb_aluno com a pessoa, os responsáveis e as matrículas
    public static class Aluno {
        @SerializedName("fk_cpf") public String cpf;
        @SerializedName("nome_escola") public String nomeEscola;
        @SerializedName("periodo_escola") public String periodoEscola;
        @SerializedName("tb_pessoa") public Pessoa pessoa;
        @SerializedName("tb_aluno_responsavel") public List<VinculoResponsavel> responsaveis;
        @SerializedName("tb_matricula") public List<Matricula> matriculas;
    }

    public static class VinculoResponsavel {
        @SerializedName("fk_cpf_responsavel") public String cpfResponsavel;
        @SerializedName("grau_parentesco") public String grauParentesco;
        @SerializedName("autorizado_busca") public Boolean autorizadoBusca;
        @SerializedName("tb_responsavel") public Responsavel responsavel;
    }

    public static class Responsavel {
        @SerializedName("tb_pessoa") public Pessoa pessoa;
    }

    // Matrícula com o período (que o MatriculaResponse dos outros módulos não traz)
    public static class Matricula extends MatriculaResponse {
        @SerializedName("periodo") public String periodo;
    }

    // Linha de tb_funcionario com a pessoa, os cargos e as turmas
    public static class Funcionario {
        @SerializedName("fk_cpf_func") public String cpf;
        @SerializedName("tb_pessoa") public Pessoa pessoa;
        @SerializedName("tb_funcionario_cargo") public List<VinculoCargo> cargos;
        @SerializedName("tb_turma_funcionario") public List<VinculoTurma> turmas;
    }

    public static class VinculoCargo extends FuncionarioLoginResponse.VinculoCargo {
        @SerializedName("fk_cod_cargo") public Integer codigoCargo;
    }

    public static class VinculoTurma {
        @SerializedName("fk_nome_turma") public String nomeTurma;
        @SerializedName("fk_cod_tipo") public Integer codigoTipo;
        @SerializedName("tb_tipo_monitor") public MatriculaResponse.TipoMonitor tipoMonitor;
    }

    // Linha de tb_turma com o curso, a equipe e as matrículas (com as frequências)
    public static class Turma {
        @SerializedName("pk_nome_turma") public String nome;
        @SerializedName("pk_horario") public String horario;
        @SerializedName("fk_cod_curso") public Integer codCurso;
        @SerializedName("qtd_alunos") public Integer qtdAlunos;
        @SerializedName("data_da_aula") public String dataAula;
        @SerializedName("horario_inicio") public String horarioInicio;
        @SerializedName("horario_fim") public String horarioFim;
        @SerializedName("tb_curso") public Curso curso;
        @SerializedName("tb_turma_funcionario") public List<EquipeTurma> equipe;
        @SerializedName("tb_matricula") public List<MatriculaResponse> matriculas;
    }

    public static class EquipeTurma extends MatriculaResponse.TurmaFuncionario {
        @SerializedName("fk_cpf_funcionario") public String cpfFuncionario;
        @SerializedName("fk_cod_tipo") public Integer codigoTipo;
    }

    public static class Curso {
        @SerializedName("pk_cod_curso") public Integer codigo;
        @SerializedName("nome_curso") public String nome;
    }

    public static class Cargo {
        @SerializedName("pk_cod_cargo") public Integer codigo;
        @SerializedName("cargo") public String nome;
    }

    public static class TipoMonitor {
        @SerializedName("pk_cod_tipo") public Integer codigo;
        @SerializedName("tipo_monitor") public String nome;
    }
}

package com.example.sisgesc.DTOs;

// Formulário da turma, usado para criar e para editar.
//   Criar: new CadastroTurma(), preencher e chamar AdminRepository.criarTurma.
//   Editar: AdminRepository.buscarCadastroTurma(nome), trocar o que mudou e chamar editarTurma.
//
// O cadastro grava tb_turma e, se houver professor, uma linha em tb_turma_funcionario.
// Formatos: data como ano-mês-dia (ex.: 2026-11-10) e horários como hora:minuto (ex.: 14:00).
public class CadastroTurma {

    private String nomeTurma;               // não muda na edição (é a chave da turma)
    private String codigoCurso;             // AdminOpcao.getValor() de buscarOpcoesCursos
    private int vagas;                      // vai para qtd_alunos
    private String dataAula;                // o dia da semana sai desta data
    private String horarioInicio;
    private String horarioFim;
    private String cpfProfessor;            // opcional: AdminOpcao.getValor() de buscarOpcoesProfessores
    private String codigoTipoMonitor;       // obrigatório se tiver professor: buscarOpcoesTiposMonitor

    // Professor como estava no banco, guardado por buscarCadastroTurma
    private String cpfProfessorOriginal;
    private boolean carregadoDoBanco;

    // Chamado pelo Repository depois de preencher o objeto com os dados do banco
    public void guardarValoresOriginais() {
        this.cpfProfessorOriginal = cpfProfessor;
        this.carregadoDoBanco = true;
    }

    public boolean isCarregadoDoBanco() { return carregadoDoBanco; }
    public String getCpfProfessorOriginal() { return cpfProfessorOriginal; }

    public String getNomeTurma() { return nomeTurma; }
    public void setNomeTurma(String nomeTurma) { this.nomeTurma = nomeTurma; }

    public String getCodigoCurso() { return codigoCurso; }
    public void setCodigoCurso(String codigoCurso) { this.codigoCurso = codigoCurso; }

    public int getVagas() { return vagas; }
    public void setVagas(int vagas) { this.vagas = vagas; }

    public String getDataAula() { return dataAula; }
    public void setDataAula(String dataAula) { this.dataAula = dataAula; }

    public String getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }

    public String getHorarioFim() { return horarioFim; }
    public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }

    public String getCpfProfessor() { return cpfProfessor; }
    public String getCodigoTipoMonitor() { return codigoTipoMonitor; }
    // cpfProfessor null ou vazio = turma sem professor (na edição, tira o professor que estava)
    public void setProfessor(String cpfProfessor, String codigoTipoMonitor) {
        this.cpfProfessor = cpfProfessor;
        this.codigoTipoMonitor = codigoTipoMonitor;
    }
}

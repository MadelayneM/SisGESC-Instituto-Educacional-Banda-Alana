package com.example.bandaalana.administrador.DTOs;

// Formulário do aluno, usado para cadastrar e para editar.
//   Cadastrar: a tela cria um new CadastroAluno(), preenche e chama AdminRepository.cadastrarAluno.
//   Editar: a tela pede AdminRepository.buscarCadastroAluno(cpf), mostra os valores,
//           troca o que mudou e chama AdminRepository.editarAluno com o mesmo objeto.
//
// O cadastro grava: tb_pessoa, tb_aluno, tb_email, tb_telefone, o responsável
// (tb_pessoa, tb_responsavel, tb_telefone, tb_aluno_responsavel) e tb_matricula.
// Formatos: CPF com 11 dígitos, sem pontos nem traço. Datas como ano-mês-dia, ex.: 2014-03-12.
// Campos marcados como opcionais podem ficar null ou vazios.
public class CadastroAluno {

    // Valores aceitos pelos enums do banco (testado: "Pendente" não existe em enum_status_matricula).
    // Os graus de parentesco são os que já aparecem nos dados; o enum pode ter outros.
    public static final String[] STATUS_MATRICULA = {"Ativo", "Inativo"};
    public static final String[] PERIODOS = {"Manhã", "Tarde"};
    public static final String[] PERIODOS_ESCOLA = {"Matutino", "Vespertino"};
    public static final String[] GRAUS_PARENTESCO = {"Mãe", "Pai", "Avó", "Tia"};

    // Aluno (tb_pessoa e tb_aluno). O CPF não muda na edição (o banco não deixa).
    private String cpf;
    private String primeiroNome;
    private String sobrenome;
    private String dataNascimento;
    private String nomeEscola;          // obrigatório no banco
    private String periodoEscola;       // um de PERIODOS_ESCOLA

    // Contato do aluno (opcionais; deixar vazio na edição apaga o que existia)
    private String email;
    private String ddd;
    private String numeroTelefone;
    private String tipoTelefone = "Celular";

    // Responsável
    private String cpfResponsavel;
    private String primeiroNomeResponsavel;
    private String sobrenomeResponsavel;
    private String dataNascimentoResponsavel;   // obrigatória (o banco exige data_nasc em toda pessoa)
    private String dddResponsavel;              // opcional
    private String numeroTelefoneResponsavel;   // opcional
    private String grauParentesco;              // um de GRAUS_PARENTESCO
    private boolean autorizadoBusca = true;

    // Matrícula (tb_matricula). Ano, semestre e data da matrícula são preenchidos pelo Repository.
    private String nomeTurma;           // AdminOpcao.getValor() de buscarOpcoesTurmas
    private String periodo;             // um de PERIODOS
    private String status = "Ativo";    // um de STATUS_MATRICULA
    private String senhaLogin;          // obrigatória no cadastro; na edição, vazia mantém a atual

    // Valores como estavam no banco, guardados por buscarCadastroAluno.
    // O Repository usa para saber o que mudou. A tela não precisa mexer nestes.
    private Integer idMatricula;
    private String emailOriginal;
    private String dddOriginal;
    private String numeroTelefoneOriginal;
    private String cpfResponsavelOriginal;
    private String dddResponsavelOriginal;
    private String numeroTelefoneResponsavelOriginal;
    private boolean carregadoDoBanco;

    // Chamado pelo Repository depois de preencher o objeto com os dados do banco
    public void guardarValoresOriginais(Integer idMatricula) {
        this.idMatricula = idMatricula;
        this.emailOriginal = email;
        this.dddOriginal = ddd;
        this.numeroTelefoneOriginal = numeroTelefone;
        this.cpfResponsavelOriginal = cpfResponsavel;
        this.dddResponsavelOriginal = dddResponsavel;
        this.numeroTelefoneResponsavelOriginal = numeroTelefoneResponsavel;
        this.carregadoDoBanco = true;
    }

    // true se veio de buscarCadastroAluno (formulário de edição)
    public boolean isCarregadoDoBanco() { return carregadoDoBanco; }
    public Integer getIdMatricula() { return idMatricula; }
    public String getEmailOriginal() { return emailOriginal; }
    public String getDddOriginal() { return dddOriginal; }
    public String getNumeroTelefoneOriginal() { return numeroTelefoneOriginal; }
    public String getCpfResponsavelOriginal() { return cpfResponsavelOriginal; }
    public String getDddResponsavelOriginal() { return dddResponsavelOriginal; }
    public String getNumeroTelefoneResponsavelOriginal() { return numeroTelefoneResponsavelOriginal; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPrimeiroNome() { return primeiroNome; }
    public void setPrimeiroNome(String primeiroNome) { this.primeiroNome = primeiroNome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getNomeEscola() { return nomeEscola; }
    public void setNomeEscola(String nomeEscola) { this.nomeEscola = nomeEscola; }

    public String getPeriodoEscola() { return periodoEscola; }
    public void setPeriodoEscola(String periodoEscola) { this.periodoEscola = periodoEscola; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDdd() { return ddd; }
    public String getNumeroTelefone() { return numeroTelefone; }
    public String getTipoTelefone() { return tipoTelefone; }
    // tipo é um de AdminPerfil.TIPOS_TELEFONE
    public void setTelefone(String ddd, String numero, String tipo) {
        this.ddd = ddd;
        this.numeroTelefone = numero;
        this.tipoTelefone = tipo;
    }

    public String getCpfResponsavel() { return cpfResponsavel; }
    public void setCpfResponsavel(String cpfResponsavel) { this.cpfResponsavel = cpfResponsavel; }

    public String getPrimeiroNomeResponsavel() { return primeiroNomeResponsavel; }
    public void setPrimeiroNomeResponsavel(String nome) { this.primeiroNomeResponsavel = nome; }

    public String getSobrenomeResponsavel() { return sobrenomeResponsavel; }
    public void setSobrenomeResponsavel(String sobrenome) { this.sobrenomeResponsavel = sobrenome; }

    public String getDataNascimentoResponsavel() { return dataNascimentoResponsavel; }
    public void setDataNascimentoResponsavel(String data) { this.dataNascimentoResponsavel = data; }

    public String getDddResponsavel() { return dddResponsavel; }
    public String getNumeroTelefoneResponsavel() { return numeroTelefoneResponsavel; }
    // O telefone do responsável é gravado como Celular
    public void setTelefoneResponsavel(String ddd, String numero) {
        this.dddResponsavel = ddd;
        this.numeroTelefoneResponsavel = numero;
    }

    public String getGrauParentesco() { return grauParentesco; }
    public void setGrauParentesco(String grauParentesco) { this.grauParentesco = grauParentesco; }

    public boolean isAutorizadoBusca() { return autorizadoBusca; }
    public void setAutorizadoBusca(boolean autorizadoBusca) { this.autorizadoBusca = autorizadoBusca; }

    public String getNomeTurma() { return nomeTurma; }
    public void setNomeTurma(String nomeTurma) { this.nomeTurma = nomeTurma; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSenhaLogin() { return senhaLogin; }
    public void setSenhaLogin(String senhaLogin) { this.senhaLogin = senhaLogin; }
}

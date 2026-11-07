package com.example.sisgesc.DTOs;

import java.util.ArrayList;
import java.util.List;

// Formulário do funcionário (professor/monitor ou coordenador), usado para cadastrar e para editar.
//   Cadastrar: new CadastroMembro(), preencher e chamar AdminRepository.cadastrarMembro.
//   Editar: AdminRepository.buscarCadastroMembro(cpf), trocar o que mudou e chamar editarMembro.
//
// O cadastro grava: tb_pessoa, tb_funcionario, tb_funcionario_cargo, tb_email,
// tb_telefone e uma linha de tb_turma_funcionario para cada turma.
// Formatos: CPF com 11 dígitos, sem pontos nem traço. Data como ano-mês-dia.
public class CadastroMembro {

    private String cpf;                     // não muda na edição (o banco não deixa)
    private String primeiroNome;
    private String sobrenome;
    private String dataNascimento;
    private String senha;                   // obrigatória no cadastro; na edição, vazia mantém a atual
    private String codigoCargo;             // AdminOpcao.getValor() de buscarOpcoesCargos
    private String email;                   // opcional
    private String ddd;                     // opcional
    private String numeroTelefone;          // opcional
    private String tipoTelefone = "Celular";
    private final List<Turma> turmas = new ArrayList<>();

    // Valores como estavam no banco, guardados por buscarCadastroMembro
    private String codigoCargoOriginal;
    private String emailOriginal;
    private String dddOriginal;
    private String numeroTelefoneOriginal;
    private boolean carregadoDoBanco;

    // Uma turma em que o funcionário trabalha e a função dele nela
    public static class Turma {
        private final String nomeTurma;
        private final String codigoTipoMonitor;

        public Turma(String nomeTurma, String codigoTipoMonitor) {
            this.nomeTurma = nomeTurma;
            this.codigoTipoMonitor = codigoTipoMonitor;
        }

        public String getNomeTurma() { return nomeTurma; }
        public String getCodigoTipoMonitor() { return codigoTipoMonitor; }
    }

    // Chamado pelo Repository depois de preencher o objeto com os dados do banco
    public void guardarValoresOriginais() {
        this.codigoCargoOriginal = codigoCargo;
        this.emailOriginal = email;
        this.dddOriginal = ddd;
        this.numeroTelefoneOriginal = numeroTelefone;
        this.carregadoDoBanco = true;
    }

    public boolean isCarregadoDoBanco() { return carregadoDoBanco; }
    public String getCodigoCargoOriginal() { return codigoCargoOriginal; }
    public String getEmailOriginal() { return emailOriginal; }
    public String getDddOriginal() { return dddOriginal; }
    public String getNumeroTelefoneOriginal() { return numeroTelefoneOriginal; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPrimeiroNome() { return primeiroNome; }
    public void setPrimeiroNome(String primeiroNome) { this.primeiroNome = primeiroNome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCodigoCargo() { return codigoCargo; }
    public void setCodigoCargo(String codigoCargo) { this.codigoCargo = codigoCargo; }

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

    // Na edição, a lista substitui as turmas antigas (vazia = tira o funcionário de todas)
    public List<Turma> getTurmas() { return turmas; }
    // nomeTurma vem de buscarOpcoesTurmas e codigoTipoMonitor de buscarOpcoesTiposMonitor
    public void adicionarTurma(String nomeTurma, String codigoTipoMonitor) {
        turmas.add(new Turma(nomeTurma, codigoTipoMonitor));
    }
    public void limparTurmas() { turmas.clear(); }
}

package com.example.bandaalana.professor.DTOs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Espelha o JSON aninhado que o Supabase devolve para os dados de um aluno.
// As telas não usam esta classe diretamente: elas recebem o objeto simples AlunoPerfil.
public class AlunoPerfilResponse {

    @SerializedName("tb_pessoa")
    public Pessoa pessoa;

    @SerializedName("tb_aluno_responsavel")
    public List<AlunoResponsavel> responsaveis;

    // Representa os dados da tabela tb_pessoa vinculados ao aluno
    public static class Pessoa {
        @SerializedName("primeiro_nome") public String primeiroNome;
        @SerializedName("sobrenome") public String sobrenome;
        @SerializedName("imagem_perfil") public String imagemPerfil;
        @SerializedName("tb_telefone") public List<Telefone> telefones;
        @SerializedName("tb_email") public Email email;
        @SerializedName("tb_endereco") public List<Endereco> enderecos;

        // Retorna o nome completo concatenando primeiro nome e sobrenome
        public String nomeCompleto() {
            String nome = primeiroNome == null ? "" : primeiroNome;
            String resto = sobrenome == null ? "" : sobrenome;
            return (nome + " " + resto).trim();
        }

        // Retorna o primeiro telefone formatado ou null se não houver
        public String primeiroTelefone() {
            if (telefones == null || telefones.isEmpty()) return null;
            Telefone t = telefones.get(0);
            return "(" + t.ddd.trim() + ") " + t.numero.trim();
        }
    }

    // Espelha o e-mail da tabela tb_email
    public static class Email {
        @SerializedName("pk_email") public String endereco;
    }

    // Espelha o telefone da tabela tb_telefone
    public static class Telefone {
        @SerializedName("pk_ddd") public String ddd;
        @SerializedName("pk_numero") public String numero;
        @SerializedName("tipo_telefone") public String tipo;
    }

    // Espelha o endereço da tabela tb_endereco
    public static class Endereco {
        @SerializedName("pk_cep") public String cep;
        @SerializedName("numero_endereco") public String numero;
        @SerializedName("complemento") public String complemento;
        @SerializedName("tb_cep") public Cep dadosCep;
    }

    // Espelha os dados de CEP da tabela tb_cep
    public static class Cep {
        @SerializedName("logradouro") public String logradouro;
        @SerializedName("bairro") public String bairro;
        @SerializedName("cidade") public String cidade;
        @SerializedName("uf") public String uf;
    }

    // Espelha o vínculo entre aluno e responsável (tb_aluno_responsavel)
    public static class AlunoResponsavel {
        @SerializedName("grau_parentesco") public String grauParentesco;
        @SerializedName("tb_responsavel") public Responsavel responsavel;
    }

    // Espelha os dados do responsável (tb_responsavel)
    public static class Responsavel {
        @SerializedName("tb_pessoa") public Pessoa pessoa;
    }
}

package com.example.sisgesc.Repository;

import com.example.sisgesc.DTOs.AlunoDadosEdicao;
import com.example.sisgesc.DTOs.AlunoDashboard;
import com.example.sisgesc.DTOs.AlunoFrequencia;
import com.example.sisgesc.DTOs.AlunoPerfil;
import com.example.sisgesc.DTOs.AlunoPerfilResponse;
import com.example.sisgesc.DTOs.AlunoTurma;
import com.example.sisgesc.DTOs.MatriculaResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// Converte o JSON aninhado do Supabase nos objetos simples que as telas usam.
final class AlunoMapper {

    static final String STATUS_ATIVO = "Ativo";
    private static final String TIPO_MONITOR_AUXILIAR = "Monitor Auxiliar";

    private AlunoMapper() {}

    static AlunoPerfil paraPerfil(AlunoPerfilResponse resposta) {
        AlunoPerfilResponse.Pessoa pessoa = resposta.pessoa;
        AlunoPerfilResponse.Pessoa responsavel = primeiroResponsavel(resposta);
        return new AlunoPerfil(
                pessoa == null ? null : pessoa.primeiroNome,
                pessoa == null ? null : pessoa.nomeCompleto(),
                pessoa == null ? null : pessoa.imagemPerfil,
                pessoa == null || pessoa.email == null ? null : pessoa.email.endereco,
                pessoa == null ? null : pessoa.primeiroTelefone(),
                responsavel == null ? null : responsavel.nomeCompleto(),
                responsavel == null ? null : responsavel.primeiroTelefone());
    }

    static AlunoDadosEdicao paraDadosEdicao(AlunoPerfilResponse resposta) {
        AlunoPerfilResponse.Pessoa pessoa = resposta.pessoa;
        AlunoPerfilResponse.Telefone telefone = pessoa == null || pessoa.telefones == null || pessoa.telefones.isEmpty()
                ? null : pessoa.telefones.get(0);
        AlunoPerfilResponse.Endereco endereco = pessoa == null || pessoa.enderecos == null || pessoa.enderecos.isEmpty()
                ? null : pessoa.enderecos.get(0);
        AlunoPerfilResponse.Cep cep = endereco == null ? null : endereco.dadosCep;
        return new AlunoDadosEdicao(
                pessoa == null || pessoa.email == null ? null : pessoa.email.endereco,
                telefone == null ? null : telefone.ddd.trim(),
                telefone == null ? null : telefone.numero.trim(),
                telefone == null ? null : telefone.tipo,
                endereco == null ? null : endereco.cep,
                cep == null ? null : cep.logradouro,
                cep == null ? null : cep.bairro,
                cep == null ? null : cep.cidade,
                cep == null ? null : cep.uf,
                endereco == null ? null : endereco.numero,
                endereco == null ? null : endereco.complemento);
    }

    static AlunoFrequencia paraFrequencia(MatriculaResponse matricula) {
        List<MatriculaResponse.Frequencia> aulas = matricula.frequencias == null
                ? new ArrayList<>() : new ArrayList<>(matricula.frequencias);

        // A data vem como ano-mês-dia, então a ordem alfabética também é a ordem cronológica
        Collections.sort(aulas, (a, b) -> a.dataAula.compareTo(b.dataAula));

        int presencas = 0;
        List<AlunoFrequencia.RegistroAula> historico = new ArrayList<>();
        for (MatriculaResponse.Frequencia aula : aulas) {
            if (aula.presente) presencas++;
            historico.add(new AlunoFrequencia.RegistroAula(aula.dataAula, aula.presente, aula.observacao));
        }

        int total = aulas.size();
        int percentual = total == 0 ? 0 : Math.round(presencas * 100f / total);
        return new AlunoFrequencia(percentual, presencas, total - presencas, total, historico);
    }

    static AlunoTurma paraTurma(String cpfAluno, MatriculaResponse matricula) {
        MatriculaResponse.Turma turma = matricula.turma;
        String nomeCurso = matricula.curso == null ? null : matricula.curso.nome;
        if (turma == null) {
            return new AlunoTurma(null, nomeCurso, null, null, null, null, null, null, null, new ArrayList<>());
        }
        return new AlunoTurma(
                turma.nome,
                nomeCurso,
                buscarNomeNaEquipe(turma, false),
                buscarNomeNaEquipe(turma, true),
                turma.dataAula,
                diaDaSemana(turma.dataAula),
                formatarHora(turma.horarioInicio),
                formatarHora(turma.horarioFim),
                turma.qtdAlunos,
                listarColegas(cpfAluno, matricula));
    }

    static AlunoDashboard paraDashboard(String cpfAluno, MatriculaResponse matricula) {
        AlunoPerfilResponse.Pessoa pessoa = matricula.aluno == null ? null : matricula.aluno.pessoa;
        return new AlunoDashboard(
                pessoa == null ? null : pessoa.primeiroNome,
                pessoa == null ? null : pessoa.nomeCompleto(),
                pessoa == null ? null : pessoa.imagemPerfil,
                paraTurma(cpfAluno, matricula),
                paraFrequencia(matricula));
    }

    private static AlunoPerfilResponse.Pessoa primeiroResponsavel(AlunoPerfilResponse resposta) {
        if (resposta.responsaveis == null) return null;
        for (AlunoPerfilResponse.AlunoResponsavel vinculo : resposta.responsaveis) {
            if (vinculo.responsavel != null && vinculo.responsavel.pessoa != null) {
                return vinculo.responsavel.pessoa;
            }
        }
        return null;
    }

    // O banco só tem dois tipos: "Monitor Auxiliar" auxilia o professor, e "Monitor Multiplicador"
    // dá o conteúdo à turma. Por isso a tela mostra o Multiplicador como professor.
    private static String buscarNomeNaEquipe(MatriculaResponse.Turma turma, boolean monitor) {
        if (turma.equipe == null) return null;
        for (MatriculaResponse.TurmaFuncionario membro : turma.equipe) {
            boolean ehMonitor = membro.tipoMonitor != null && TIPO_MONITOR_AUXILIAR.equals(membro.tipoMonitor.tipo);
            if (ehMonitor == monitor && membro.funcionario != null && membro.funcionario.pessoa != null) {
                return membro.funcionario.pessoa.nomeCompleto();
            }
        }
        return null;
    }

    // Colegas são os alunos com matrícula ativa na mesma turma, no mesmo ano e semestre
    private static List<String> listarColegas(String cpfAluno, MatriculaResponse matricula) {
        List<String> colegas = new ArrayList<>();
        if (matricula.turma.matriculas == null) return colegas;
        for (MatriculaResponse outra : matricula.turma.matriculas) {
            boolean mesmoPeriodo = Objects.equals(outra.ano, matricula.ano)
                    && Objects.equals(outra.semestre, matricula.semestre);
            if (mesmoPeriodo && STATUS_ATIVO.equals(outra.status) && !cpfAluno.equals(outra.cpf)
                    && outra.aluno != null && outra.aluno.pessoa != null) {
                colegas.add(outra.aluno.pessoa.nomeCompleto());
            }
        }
        Collections.sort(colegas, String.CASE_INSENSITIVE_ORDER);
        return colegas;
    }

    // Transforma 2026-11-06 em Sexta-feira
    private static String diaDaSemana(String data) {
        if (data == null) return null;
        try {
            Locale portugues = new Locale("pt", "BR");
            Date dia = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(data);
            String nome = new SimpleDateFormat("EEEE", portugues).format(dia);
            return nome.substring(0, 1).toUpperCase(portugues) + nome.substring(1);
        } catch (ParseException e) {
            return null;
        }
    }

    // O banco devolve 14:00:00 e a tela mostra 14:00
    private static String formatarHora(String hora) {
        return hora != null && hora.length() >= 5 ? hora.substring(0, 5) : hora;
    }
}

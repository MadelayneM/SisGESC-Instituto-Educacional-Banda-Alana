package com.example.sisgesc.Repository;

import com.example.sisgesc.DTOs.AlunoPerfilResponse;
import com.example.sisgesc.DTOs.ChamadaAluno;
import com.example.sisgesc.DTOs.FuncionarioLoginResponse;
import com.example.sisgesc.DTOs.FuncionarioResponse;
import com.example.sisgesc.DTOs.ProfessorAluno;
import com.example.sisgesc.DTOs.ProfessorInicio;
import com.example.sisgesc.DTOs.ProfessorPerfil;
import com.example.sisgesc.DTOs.ProfessorTurma;
import com.example.sisgesc.DTOs.TurmaFrequencia;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// Converte o JSON aninhado do Supabase nos objetos simples que as telas do Professor usam.
final class ProfessorMapper {

    static final String STATUS_ATIVO = "Ativo";
    private static final Locale PORTUGUES = new Locale("pt", "BR");

    private ProfessorMapper() {
    }

    static ProfessorInicio paraInicio(FuncionarioResponse resposta) {
        String primeiroNome = resposta.pessoa == null ? null : resposta.pessoa.primeiroNome;
        return new ProfessorInicio(primeiroNome, paraTurmas(resposta));
    }

    static List<ProfessorTurma> paraTurmas(FuncionarioResponse resposta) {
        List<ProfessorTurma> turmas = new ArrayList<>();
        for (FuncionarioResponse.TurmaDoFuncionario vinculo : listaOuVazia(resposta.turmas)) {
            FuncionarioResponse.Turma turma = vinculo.turma;
            if (turma == null) continue;

            int ativos = 0;
            for (FuncionarioResponse.Matricula matricula : listaOuVazia(turma.matriculas)) {
                if (STATUS_ATIVO.equals(matricula.status)) ativos++;
            }
            turmas.add(new ProfessorTurma(
                    turma.nome,
                    turma.curso == null ? null : turma.curso.nome,
                    vinculo.tipoMonitor == null ? null : vinculo.tipoMonitor.tipo,
                    turma.dataAula,
                    diaSemana(turma.dataAula),
                    horaMinuto(turma.horarioInicio),
                    horaMinuto(turma.horarioFim),
                    turma.qtdAlunos,
                    ativos));
        }
        Collections.sort(turmas, (a, b) -> texto(a.getNomeTurma()).compareTo(texto(b.getNomeTurma())));
        return turmas;
    }

    // Alunos com matrícula ativa nas turmas do professor, em ordem alfabética.
    // Se nomeTurma não for null, só entram os alunos dessa turma.
    // Se textoBusca não for vazio, só entram os nomes que contêm esse texto
    // (sem diferenciar maiúsculas, minúsculas e acentos).
    static List<ProfessorAluno> paraAlunos(FuncionarioResponse resposta, String nomeTurma, String textoBusca) {
        String busca = semAcento(textoBusca);
        List<ProfessorAluno> alunos = new ArrayList<>();
        for (FuncionarioResponse.TurmaDoFuncionario vinculo : listaOuVazia(resposta.turmas)) {
            FuncionarioResponse.Turma turma = vinculo.turma;
            if (turma == null) continue;
            if (nomeTurma != null && !nomeTurma.equals(turma.nome)) continue;

            for (FuncionarioResponse.Matricula matricula : listaOuVazia(turma.matriculas)) {
                if (!STATUS_ATIVO.equals(matricula.status)) continue;
                AlunoPerfilResponse.Pessoa pessoa = matricula.aluno == null ? null : matricula.aluno.pessoa;
                String nome = pessoa == null ? "" : pessoa.nomeCompleto();
                if (!busca.isEmpty() && !semAcento(nome).contains(busca)) continue;

                alunos.add(new ProfessorAluno(
                        matricula.cpf,
                        matricula.idMatricula,
                        nome,
                        pessoa == null ? null : pessoa.imagemPerfil,
                        turma.nome,
                        turma.curso == null ? null : turma.curso.nome));
            }
        }
        Collections.sort(alunos, (a, b) -> semAcento(a.getNome()).compareTo(semAcento(b.getNome())));
        return alunos;
    }

    // Para cada turma, junta as frequências de todos os alunos por data da aula
    static List<TurmaFrequencia> paraFrequencia(FuncionarioResponse resposta) {
        List<TurmaFrequencia> historico = new ArrayList<>();
        for (FuncionarioResponse.TurmaDoFuncionario vinculo : listaOuVazia(resposta.turmas)) {
            FuncionarioResponse.Turma turma = vinculo.turma;
            if (turma == null) continue;

            // TreeMap deixa as datas em ordem. Como vêm como ano-mês-dia,
            // a ordem alfabética também é a ordem cronológica. O valor guarda {presentes, ausentes}.
            Map<String, int[]> porData = new TreeMap<>();
            for (FuncionarioResponse.Matricula matricula : listaOuVazia(turma.matriculas)) {
                for (FuncionarioResponse.Frequencia frequencia : listaOuVazia(matricula.frequencias)) {
                    if (frequencia.dataAula == null) continue;
                    int[] contagem = porData.get(frequencia.dataAula);
                    if (contagem == null) {
                        contagem = new int[2];
                        porData.put(frequencia.dataAula, contagem);
                    }
                    contagem[frequencia.presente ? 0 : 1]++;
                }
            }

            List<TurmaFrequencia.Aula> aulas = new ArrayList<>();
            for (Map.Entry<String, int[]> dia : porData.entrySet()) {
                aulas.add(new TurmaFrequencia.Aula(dia.getKey(), dia.getValue()[0], dia.getValue()[1]));
            }
            historico.add(new TurmaFrequencia(turma.nome, turma.curso == null ? null : turma.curso.nome, aulas));
        }
        Collections.sort(historico, (a, b) -> texto(a.getNomeTurma()).compareTo(texto(b.getNomeTurma())));
        return historico;
    }

    static List<ChamadaAluno> paraChamada(List<FuncionarioResponse.Matricula> matriculas) {
        List<ChamadaAluno> chamada = new ArrayList<>();
        for (FuncionarioResponse.Matricula matricula : matriculas) {
            AlunoPerfilResponse.Pessoa pessoa = matricula.aluno == null ? null : matricula.aluno.pessoa;
            // A consulta já filtrou pela data, então há no máximo uma frequência por aluno
            List<FuncionarioResponse.Frequencia> frequencias = listaOuVazia(matricula.frequencias);
            FuncionarioResponse.Frequencia salva = frequencias.isEmpty() ? null : frequencias.get(0);

            chamada.add(new ChamadaAluno(
                    matricula.idMatricula,
                    salva == null ? null : salva.idFrequencia,
                    pessoa == null ? "" : pessoa.nomeCompleto(),
                    pessoa == null ? null : pessoa.imagemPerfil,
                    salva == null ? null : salva.presente,
                    salva == null ? null : salva.observacao));
        }
        Collections.sort(chamada, (a, b) -> semAcento(a.getNome()).compareTo(semAcento(b.getNome())));
        return chamada;
    }

    static ProfessorPerfil paraPerfil(FuncionarioResponse resposta) {
        AlunoPerfilResponse.Pessoa pessoa = resposta.pessoa;
        AlunoPerfilResponse.Telefone telefone = pessoa == null || pessoa.telefones == null
                || pessoa.telefones.isEmpty() ? null : pessoa.telefones.get(0);

        // Set sem repetição que mantém a ordem em que as funções aparecem
        Set<String> funcoes = new LinkedHashSet<>();
        for (FuncionarioResponse.TurmaDoFuncionario vinculo : listaOuVazia(resposta.turmas)) {
            if (vinculo.tipoMonitor != null && vinculo.tipoMonitor.tipo != null) {
                funcoes.add(vinculo.tipoMonitor.tipo);
            }
        }

        return new ProfessorPerfil(
                pessoa == null ? null : pessoa.primeiroNome,
                pessoa == null ? null : pessoa.nomeCompleto(),
                pessoa == null ? null : pessoa.imagemPerfil,
                cargoAtual(resposta),
                new ArrayList<>(funcoes),
                pessoa == null || pessoa.email == null ? null : pessoa.email.endereco,
                telefone == null ? null : telefone.ddd.trim(),
                telefone == null ? null : telefone.numero.trim(),
                telefone == null ? null : telefone.tipo);
    }

    // O cargo sem data de saída é o atual
    private static String cargoAtual(FuncionarioResponse resposta) {
        for (FuncionarioLoginResponse.VinculoCargo vinculo : listaOuVazia(resposta.cargos)) {
            if (vinculo.dataSaida == null && vinculo.cargo != null) {
                return vinculo.cargo.nome;
            }
        }
        return null;
    }

    // 2026-11-06 vira "Sexta-feira"
    private static String diaSemana(String dataBanco) {
        if (dataBanco == null || dataBanco.length() < 10) return null;
        try {
            Date dia = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dataBanco.substring(0, 10));
            String semana = new SimpleDateFormat("EEEE", PORTUGUES).format(dia);
            return semana.substring(0, 1).toUpperCase(PORTUGUES) + semana.substring(1);
        } catch (ParseException e) {
            return null;
        }
    }

    // 14:00:00 vira 14:00
    private static String horaMinuto(String horario) {
        return horario == null || horario.length() < 5 ? horario : horario.substring(0, 5);
    }

    // "Ação" vira "acao", para buscar e ordenar sem ligar para acentos e maiúsculas
    private static String semAcento(String texto) {
        if (texto == null) return "";
        String separado = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);
        return separado.replaceAll("\\p{M}", "").toLowerCase(PORTUGUES);
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private static <T> List<T> listaOuVazia(List<T> lista) {
        return lista == null ? Collections.emptyList() : lista;
    }
}

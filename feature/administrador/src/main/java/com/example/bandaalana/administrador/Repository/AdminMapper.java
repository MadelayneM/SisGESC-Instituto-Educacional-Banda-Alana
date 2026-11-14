package com.example.bandaalana.administrador.Repository;

import com.example.bandaalana.administrador.DTOs.AdminAluno;
import com.example.bandaalana.administrador.DTOs.AdminMembro;
import com.example.bandaalana.administrador.DTOs.AdminOpcao;
import com.example.bandaalana.administrador.DTOs.AdminPerfil;
import com.example.bandaalana.administrador.DTOs.AdminResponse;
import com.example.bandaalana.administrador.DTOs.AdminTurma;
import com.example.bandaalana.administrador.DTOs.AlunoPerfilResponse;
import com.example.bandaalana.administrador.DTOs.CadastroAluno;
import com.example.bandaalana.administrador.DTOs.CadastroMembro;
import com.example.bandaalana.administrador.DTOs.CadastroTurma;
import com.example.bandaalana.administrador.DTOs.FuncionarioLoginResponse;
import com.example.bandaalana.administrador.DTOs.MatriculaResponse;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Converte o JSON aninhado do Supabase nos objetos simples que as telas do Administrador usam.
final class AdminMapper {

    static final String STATUS_ATIVO = "Ativo";
    static final String CARGO_ADMINISTRADOR = "Coordenador";
    private static final Locale PORTUGUES = new Locale("pt", "BR");

    private AdminMapper() {
    }

    // ===== Alunos =====

    // Alunos em ordem alfabética. Se textoBusca não for vazio, só entram os nomes que contêm
    // esse texto (sem diferenciar maiúsculas, minúsculas e acentos).
    static List<AdminAluno> paraAlunos(List<AdminResponse.Aluno> resposta, String textoBusca) {
        String busca = semAcento(textoBusca);
        List<AdminAluno> alunos = new ArrayList<>();
        for (AdminResponse.Aluno aluno : resposta) {
            String nome = nomeCompleto(aluno.pessoa);
            if (!busca.isEmpty() && !semAcento(nome).contains(busca)) continue;
            alunos.add(paraAluno(aluno.cpf, aluno.pessoa, matriculaPrincipal(aluno.matriculas)));
        }
        Collections.sort(alunos, (a, b) -> semAcento(a.getNome()).compareTo(semAcento(b.getNome())));
        return alunos;
    }

    // Matrículas já vêm da mais nova para a mais antiga
    static List<AdminAluno> paraAlunosRecentes(List<MatriculaResponse> matriculas) {
        List<AdminAluno> alunos = new ArrayList<>();
        for (MatriculaResponse matricula : matriculas) {
            AlunoPerfilResponse.Pessoa pessoa = matricula.aluno == null ? null : matricula.aluno.pessoa;
            alunos.add(paraAluno(matricula.cpf, pessoa, matricula));
        }
        return alunos;
    }

    private static AdminAluno paraAluno(String cpf, AlunoPerfilResponse.Pessoa pessoa, MatriculaResponse matricula) {
        return new AdminAluno(
                cpf,
                nomeCompleto(pessoa),
                pessoa == null ? null : pessoa.imagemPerfil,
                matricula == null ? null : matricula.idMatricula,
                matricula == null || matricula.turma == null ? null : matricula.turma.nome,
                matricula == null || matricula.curso == null ? null : matricula.curso.nome,
                matricula == null ? null : matricula.status);
    }

    // Preenche o formulário de edição com o que está no banco.
    // Responsável: o primeiro vinculado. Matrícula: a ativa mais recente (ou a mais recente).
    static CadastroAluno paraCadastroAluno(AdminResponse.Aluno aluno) {
        CadastroAluno cadastro = new CadastroAluno();
        AdminResponse.Pessoa pessoa = aluno.pessoa;
        cadastro.setCpf(aluno.cpf);
        cadastro.setNomeEscola(aluno.nomeEscola);
        cadastro.setPeriodoEscola(aluno.periodoEscola);
        if (pessoa != null) {
            cadastro.setPrimeiroNome(pessoa.primeiroNome);
            cadastro.setSobrenome(pessoa.sobrenome);
            cadastro.setDataNascimento(pessoa.dataNascimento);
            cadastro.setEmail(pessoa.email == null ? null : pessoa.email.endereco);
            AlunoPerfilResponse.Telefone telefone = primeiroTelefone(pessoa);
            if (telefone != null) cadastro.setTelefone(telefone.ddd.trim(), telefone.numero.trim(), telefone.tipo);
        }

        List<AdminResponse.VinculoResponsavel> responsaveis = listaOuVazia(aluno.responsaveis);
        if (!responsaveis.isEmpty()) {
            AdminResponse.VinculoResponsavel vinculo = responsaveis.get(0);
            cadastro.setCpfResponsavel(vinculo.cpfResponsavel);
            cadastro.setGrauParentesco(vinculo.grauParentesco);
            cadastro.setAutorizadoBusca(vinculo.autorizadoBusca == null || vinculo.autorizadoBusca);
            AdminResponse.Pessoa resp = vinculo.responsavel == null ? null : vinculo.responsavel.pessoa;
            if (resp != null) {
                cadastro.setPrimeiroNomeResponsavel(resp.primeiroNome);
                cadastro.setSobrenomeResponsavel(resp.sobrenome);
                cadastro.setDataNascimentoResponsavel(resp.dataNascimento);
                AlunoPerfilResponse.Telefone telefone = primeiroTelefone(resp);
                if (telefone != null) cadastro.setTelefoneResponsavel(telefone.ddd.trim(), telefone.numero.trim());
            }
        }

        AdminResponse.Matricula matricula = (AdminResponse.Matricula) matriculaPrincipal(aluno.matriculas);
        if (matricula != null) {
            cadastro.setNomeTurma(matricula.turma == null ? null : matricula.turma.nome);
            cadastro.setPeriodo(matricula.periodo);
            cadastro.setStatus(matricula.status);
        }
        cadastro.guardarValoresOriginais(matricula == null ? null : matricula.idMatricula);
        return cadastro;
    }

    // A primeira matrícula ativa; se não houver, a mais recente
    private static MatriculaResponse matriculaPrincipal(List<? extends MatriculaResponse> matriculas) {
        for (MatriculaResponse matricula : listaOuVazia(matriculas)) {
            if (STATUS_ATIVO.equals(matricula.status)) return matricula;
        }
        return matriculas == null || matriculas.isEmpty() ? null : matriculas.get(0);
    }

    // ===== Equipe =====

    // Só quem tem cargo ativo (sem data de saída), em ordem alfabética
    static List<AdminMembro> paraEquipe(List<AdminResponse.Funcionario> funcionarios) {
        List<AdminMembro> equipe = new ArrayList<>();
        for (AdminResponse.Funcionario funcionario : funcionarios) {
            String cargo = cargoAtual(funcionario.cargos);
            if (cargo == null) continue;

            List<String> turmas = new ArrayList<>();
            for (AdminResponse.VinculoTurma vinculo : listaOuVazia(funcionario.turmas)) {
                if (vinculo.nomeTurma != null && !turmas.contains(vinculo.nomeTurma)) turmas.add(vinculo.nomeTurma);
            }
            Collections.sort(turmas);
            equipe.add(new AdminMembro(
                    funcionario.cpf,
                    nomeCompleto(funcionario.pessoa),
                    funcionario.pessoa == null ? null : funcionario.pessoa.imagemPerfil,
                    cargo,
                    turmas));
        }
        Collections.sort(equipe, (a, b) -> semAcento(a.getNome()).compareTo(semAcento(b.getNome())));
        return equipe;
    }

    // Professores = funcionários com cargo ativo que não é o de administrador
    static List<AdminResponse.Funcionario> soProfessores(List<AdminResponse.Funcionario> funcionarios) {
        List<AdminResponse.Funcionario> professores = new ArrayList<>();
        for (AdminResponse.Funcionario funcionario : funcionarios) {
            String cargo = cargoAtual(funcionario.cargos);
            if (cargo != null && !CARGO_ADMINISTRADOR.equals(cargo)) professores.add(funcionario);
        }
        return professores;
    }

    static CadastroMembro paraCadastroMembro(AdminResponse.Funcionario funcionario) {
        CadastroMembro cadastro = new CadastroMembro();
        AdminResponse.Pessoa pessoa = funcionario.pessoa;
        cadastro.setCpf(funcionario.cpf);
        if (pessoa != null) {
            cadastro.setPrimeiroNome(pessoa.primeiroNome);
            cadastro.setSobrenome(pessoa.sobrenome);
            cadastro.setDataNascimento(pessoa.dataNascimento);
            cadastro.setEmail(pessoa.email == null ? null : pessoa.email.endereco);
            AlunoPerfilResponse.Telefone telefone = primeiroTelefone(pessoa);
            if (telefone != null) cadastro.setTelefone(telefone.ddd.trim(), telefone.numero.trim(), telefone.tipo);
        }
        AdminResponse.VinculoCargo cargo = vinculoCargoAtual(funcionario.cargos);
        cadastro.setCodigoCargo(cargo == null || cargo.codigoCargo == null ? null : String.valueOf(cargo.codigoCargo));
        for (AdminResponse.VinculoTurma turma : listaOuVazia(funcionario.turmas)) {
            cadastro.adicionarTurma(turma.nomeTurma, turma.codigoTipo == null ? null : String.valueOf(turma.codigoTipo));
        }
        cadastro.guardarValoresOriginais();
        return cadastro;
    }

    // ===== Turmas =====

    static List<AdminTurma> paraTurmas(List<AdminResponse.Turma> resposta) {
        List<AdminTurma> turmas = new ArrayList<>();
        for (AdminResponse.Turma turma : resposta) {
            List<String> professores = new ArrayList<>();
            for (AdminResponse.EquipeTurma vinculo : listaOuVazia(turma.equipe)) {
                if (vinculo.funcionario == null) continue;
                String nome = nomeCompleto(vinculo.funcionario.pessoa);
                if (!nome.isEmpty()) professores.add(nome);
            }

            int ativos = 0;
            int presencas = 0;
            int faltas = 0;
            for (MatriculaResponse matricula : listaOuVazia(turma.matriculas)) {
                if (STATUS_ATIVO.equals(matricula.status)) ativos++;
                for (MatriculaResponse.Frequencia frequencia : listaOuVazia(matricula.frequencias)) {
                    if (frequencia.presente) presencas++;
                    else faltas++;
                }
            }

            turmas.add(new AdminTurma(
                    turma.nome,
                    turma.curso == null ? null : turma.curso.nome,
                    professores,
                    turma.dataAula,
                    diaSemana(turma.dataAula),
                    horaMinuto(turma.horarioInicio),
                    horaMinuto(turma.horarioFim),
                    turma.qtdAlunos,
                    ativos,
                    presencas,
                    faltas));
        }
        Collections.sort(turmas, (a, b) -> semAcento(a.getNomeTurma()).compareTo(semAcento(b.getNomeTurma())));
        return turmas;
    }

    // Professor do formulário = o primeiro da equipe da turma
    static CadastroTurma paraCadastroTurma(AdminResponse.Turma turma) {
        CadastroTurma cadastro = new CadastroTurma();
        cadastro.setNomeTurma(turma.nome);
        cadastro.setCodigoCurso(turma.codCurso == null ? null : String.valueOf(turma.codCurso));
        cadastro.setVagas(turma.qtdAlunos == null ? 0 : turma.qtdAlunos);
        cadastro.setDataAula(turma.dataAula);
        cadastro.setHorarioInicio(horaMinuto(turma.horarioInicio));
        cadastro.setHorarioFim(horaMinuto(turma.horarioFim));
        List<AdminResponse.EquipeTurma> equipe = listaOuVazia(turma.equipe);
        if (!equipe.isEmpty()) {
            AdminResponse.EquipeTurma professor = equipe.get(0);
            cadastro.setProfessor(professor.cpfFuncionario,
                    professor.codigoTipo == null ? null : String.valueOf(professor.codigoTipo));
        }
        cadastro.guardarValoresOriginais();
        return cadastro;
    }

    // ===== Perfil =====

    static AdminPerfil paraPerfil(AdminResponse.Funcionario resposta) {
        AdminResponse.Pessoa pessoa = resposta.pessoa;
        AlunoPerfilResponse.Telefone telefone = primeiroTelefone(pessoa);
        return new AdminPerfil(
                pessoa == null ? null : pessoa.primeiroNome,
                nomeCompleto(pessoa),
                pessoa == null ? null : pessoa.imagemPerfil,
                cargoAtual(resposta.cargos),
                pessoa == null || pessoa.email == null ? null : pessoa.email.endereco,
                telefone == null || telefone.ddd == null ? null : telefone.ddd.trim(),
                telefone == null || telefone.numero == null ? null : telefone.numero.trim(),
                telefone == null ? null : telefone.tipo);
    }

    // ===== Opções dos Spinners =====

    static List<AdminOpcao> paraOpcoesCursos(List<AdminResponse.Curso> cursos) {
        List<AdminOpcao> opcoes = new ArrayList<>();
        for (AdminResponse.Curso curso : cursos) opcoes.add(new AdminOpcao(String.valueOf(curso.codigo), curso.nome));
        return ordenar(opcoes);
    }

    static List<AdminOpcao> paraOpcoesCargos(List<AdminResponse.Cargo> cargos) {
        List<AdminOpcao> opcoes = new ArrayList<>();
        for (AdminResponse.Cargo cargo : cargos) opcoes.add(new AdminOpcao(String.valueOf(cargo.codigo), cargo.nome));
        return ordenar(opcoes);
    }

    static List<AdminOpcao> paraOpcoesTiposMonitor(List<AdminResponse.TipoMonitor> tipos) {
        List<AdminOpcao> opcoes = new ArrayList<>();
        for (AdminResponse.TipoMonitor tipo : tipos) opcoes.add(new AdminOpcao(String.valueOf(tipo.codigo), tipo.nome));
        return ordenar(opcoes);
    }

    // Valor e texto são o nome da turma, ex.: "Violão A"
    static List<AdminOpcao> paraOpcoesTurmas(List<AdminResponse.Turma> turmas) {
        List<AdminOpcao> opcoes = new ArrayList<>();
        for (AdminResponse.Turma turma : turmas) opcoes.add(new AdminOpcao(turma.nome, turma.nome));
        return ordenar(opcoes);
    }

    // Valor é o CPF e texto é o nome completo
    static List<AdminOpcao> paraOpcoesProfessores(List<AdminResponse.Funcionario> funcionarios) {
        List<AdminOpcao> opcoes = new ArrayList<>();
        for (AdminResponse.Funcionario professor : soProfessores(funcionarios)) {
            opcoes.add(new AdminOpcao(professor.cpf, nomeCompleto(professor.pessoa)));
        }
        return ordenar(opcoes);
    }

    // ===== Auxiliares =====

    // Nome do cargo sem data de saída; null se a pessoa não trabalha mais no instituto
    private static String cargoAtual(List<? extends FuncionarioLoginResponse.VinculoCargo> cargos) {
        FuncionarioLoginResponse.VinculoCargo vinculo = vinculoCargoAtual(cargos);
        return vinculo == null ? null : vinculo.cargo.nome;
    }

    // Vínculo de cargo sem data de saída. Se tiver mais de um ativo, Coordenador tem prioridade.
    private static <V extends FuncionarioLoginResponse.VinculoCargo> V vinculoCargoAtual(List<V> cargos) {
        V atual = null;
        for (V vinculo : listaOuVazia(cargos)) {
            if (vinculo.dataSaida != null || vinculo.cargo == null || vinculo.cargo.nome == null) continue;
            if (CARGO_ADMINISTRADOR.equals(vinculo.cargo.nome)) return vinculo;
            atual = vinculo;
        }
        return atual;
    }

    private static AlunoPerfilResponse.Telefone primeiroTelefone(AlunoPerfilResponse.Pessoa pessoa) {
        if (pessoa == null || pessoa.telefones == null || pessoa.telefones.isEmpty()) return null;
        AlunoPerfilResponse.Telefone telefone = pessoa.telefones.get(0);
        return telefone.ddd == null || telefone.numero == null ? null : telefone;
    }

    private static String nomeCompleto(AlunoPerfilResponse.Pessoa pessoa) {
        return pessoa == null ? "" : pessoa.nomeCompleto();
    }

    private static List<AdminOpcao> ordenar(List<AdminOpcao> opcoes) {
        Collections.sort(opcoes, (a, b) -> semAcento(a.getTexto()).compareTo(semAcento(b.getTexto())));
        return opcoes;
    }

    // "2026-11-10" -> "Terça-feira"
    private static String diaSemana(String dataBanco) {
        if (dataBanco == null) return null;
        try {
            Date data = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dataBanco);
            String dia = new SimpleDateFormat("EEEE", PORTUGUES).format(data);
            return dia.substring(0, 1).toUpperCase(PORTUGUES) + dia.substring(1);
        } catch (ParseException e) {
            return null;
        }
    }

    // "08:00:00" -> "08:00"
    private static String horaMinuto(String horario) {
        return horario != null && horario.length() >= 5 ? horario.substring(0, 5) : horario;
    }

    private static String semAcento(String texto) {
        if (texto == null) return "";
        String semMarcas = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semMarcas.toLowerCase(Locale.ROOT).trim();
    }

    private static <T> List<T> listaOuVazia(List<T> lista) {
        return lista == null ? Collections.<T>emptyList() : lista;
    }
}

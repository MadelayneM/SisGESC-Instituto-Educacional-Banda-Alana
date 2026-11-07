package com.example.sisgesc.Repository;

import com.example.sisgesc.DTOs.AdminAluno;
import com.example.sisgesc.DTOs.AdminMembro;
import com.example.sisgesc.DTOs.AdminOpcao;
import com.example.sisgesc.DTOs.AdminPerfil;
import com.example.sisgesc.DTOs.AdminResumo;
import com.example.sisgesc.DTOs.AdminTurma;
import com.example.sisgesc.DTOs.CadastroTurma;
import com.example.sisgesc.DTOs.CadastroAluno;
import com.example.sisgesc.DTOs.CadastroMembro;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

// Testes do AdminRepository contra o Supabase de verdade (dados fictícios).
// Rodam no computador, sem celular: a camada de dados não usa classes do Android.
// Nenhum destes testes grava no banco: as leituras só leem, e os cadastros param
// na validação ou na checagem de CPF repetido, antes da primeira gravação.
//
// Para rodar: gradlew :app:testDebugUnitTest --tests "*AdminRepositoryTest"
public class AdminRepositoryTest {

    private static final String CPF_ADMIN = "30000000001";          // Ana, Coordenadora
    private static final String CPF_FUNCIONARIO_SAIU = "30000000004"; // cargo com data de saída
    private static final String CPF_ALUNO_EXISTENTE = "10000000001";

    private final AdminRepository repositorio = new AdminRepository();

    // ===== Leituras =====

    @Test
    public void resumoDoDashboard() throws Exception {
        AdminResumo resumo = esperar((repo, cb) -> repo.buscarResumo(CPF_ADMIN, cb));
        System.out.println("Resumo: nome=" + resumo.getPrimeiroNome() + " alunos=" + resumo.getTotalAlunos()
                + " turmas=" + resumo.getTotalTurmas() + " professores=" + resumo.getTotalProfessores()
                + " frequencia=" + resumo.getFrequenciaTexto());
        assertEquals("Ana", resumo.getPrimeiroNome());
        assertTrue(resumo.getTotalAlunos() > 0);
        assertTrue(resumo.getTotalTurmas() > 0);
        assertTrue(resumo.getTotalProfessores() > 0);
        assertNotNull(resumo.getFrequenciaGeral());
        assertTrue(resumo.getFrequenciaGeral() >= 0 && resumo.getFrequenciaGeral() <= 100);
    }

    @Test
    public void alunosRecentes() throws Exception {
        List<AdminAluno> alunos = esperar((repo, cb) -> repo.buscarAlunosRecentes(5, cb));
        for (AdminAluno a : alunos) {
            System.out.println("Recente: " + a.getIdMatricula() + " " + a.getNome() + " | " + a.getNomeTurma() + " | " + a.getStatus());
        }
        assertEquals(5, alunos.size());
        assertTrue(alunos.get(0).getIdMatricula() > alunos.get(4).getIdMatricula());
        for (AdminAluno a : alunos) {
            assertFalse(a.getNome().isEmpty());
            assertNotNull(a.getNomeTurma());
        }
    }

    @Test
    public void listaDeAlunosEBusca() throws Exception {
        List<AdminAluno> todos = esperar((repo, cb) -> repo.buscarAlunos(null, cb));
        System.out.println("Alunos: " + todos.size());
        for (AdminAluno a : todos) System.out.println("  " + a.getNome() + " | " + a.getNomeTurma() + " | " + a.getNomeCurso() + " | " + a.getStatus());
        assertFalse(todos.isEmpty());

        // Busca sem diferenciar maiúsculas e acentos
        List<AdminAluno> busca = esperar((repo, cb) -> repo.buscarAlunos("MARIÁNA", cb));
        assertEquals(1, busca.size());
        assertTrue(busca.get(0).getNome().startsWith("Mariana"));
    }

    @Test
    public void turmas() throws Exception {
        List<AdminTurma> turmas = esperar((repo, cb) -> repo.buscarTurmas(cb));
        for (AdminTurma t : turmas) {
            System.out.println("Turma: " + t.getNomeTurma() + " | " + t.getNomeCurso() + " | " + t.getProfessoresTexto()
                    + " | " + t.getDiaSemana() + " " + t.getHorarioInicio() + "-" + t.getHorarioFim()
                    + " | " + t.getAlunosAtivos() + "/" + t.getVagas() + " | freq=" + t.getFrequencia());
        }
        assertFalse(turmas.isEmpty());
        for (AdminTurma t : turmas) {
            assertNotNull(t.getNomeCurso());
            assertNotNull(t.getDiaSemana());
            assertEquals(5, t.getHorarioInicio().length());
        }
    }

    @Test
    public void equipeSemQuemSaiu() throws Exception {
        List<AdminMembro> equipe = esperar((repo, cb) -> repo.buscarEquipe(cb));
        for (AdminMembro m : equipe) {
            System.out.println("Equipe: " + m.getIniciais() + " " + m.getNome() + " | " + m.getCargo() + " | " + m.getTurmasTexto());
            assertFalse(CPF_FUNCIONARIO_SAIU.equals(m.getCpf()));
            assertNotNull(m.getCargo());
        }
        assertFalse(equipe.isEmpty());
    }

    @Test
    public void opcoesDosSpinners() throws Exception {
        List<AdminOpcao> cursos = esperar((repo, cb) -> repo.buscarOpcoesCursos(cb));
        List<AdminOpcao> turmas = esperar((repo, cb) -> repo.buscarOpcoesTurmas(cb));
        List<AdminOpcao> professores = esperar((repo, cb) -> repo.buscarOpcoesProfessores(cb));
        List<AdminOpcao> cargos = esperar((repo, cb) -> repo.buscarOpcoesCargos(cb));
        List<AdminOpcao> tipos = esperar((repo, cb) -> repo.buscarOpcoesTiposMonitor(cb));
        System.out.println("Cursos: " + cursos);
        System.out.println("Turmas: " + turmas);
        System.out.println("Professores: " + professores);
        System.out.println("Cargos: " + cargos);
        System.out.println("Tipos de monitor: " + tipos);
        assertFalse(cursos.isEmpty());
        assertFalse(turmas.isEmpty());
        assertFalse(professores.isEmpty());
        assertFalse(cargos.isEmpty());
        assertFalse(tipos.isEmpty());
        for (AdminOpcao p : professores) assertTrue(p.getValor().matches("\\d{11}"));
    }

    @Test
    public void perfilDoAdministrador() throws Exception {
        AdminPerfil perfil = esperar((repo, cb) -> repo.buscarPerfil(CPF_ADMIN, cb));
        System.out.println("Perfil: " + perfil.getNomeCompleto() + " | " + perfil.getCargo() + " | "
                + perfil.getEmail() + " | " + perfil.getTelefoneFormatado());
        assertEquals("Coordenador", perfil.getCargo());
        assertNotNull(perfil.getEmail());
    }

    // ===== Erros esperados (nada é gravado) =====

    @Test
    public void senhaAtualErradaNaoAltera() throws Exception {
        String erro = esperarErro((repo, cb) -> repo.alterarSenha(CPF_ADMIN, "senha-errada-de-teste", "nova", cb));
        System.out.println("Alterar senha com a atual errada: " + erro);
        assertTrue(erro.startsWith("Senha atual incorreta"));
    }

    @Test
    public void cadastroDeAlunoComCpfInvalido() throws Exception {
        CadastroAluno aluno = alunoValido();
        aluno.setCpf("123.456.789-00");
        String erro = esperarErro((repo, cb) -> repo.cadastrarAluno(aluno, cb));
        assertTrue(erro.contains("11 dígitos"));
    }

    @Test
    public void cadastroDeAlunoComCpfRepetido() throws Exception {
        CadastroAluno aluno = alunoValido();
        aluno.setCpf(CPF_ALUNO_EXISTENTE);
        String erro = esperarErro((repo, cb) -> repo.cadastrarAluno(aluno, cb));
        System.out.println("Cadastrar aluno com CPF repetido: " + erro);
        assertTrue(erro.startsWith("Já existe uma pessoa"));
    }

    @Test
    public void cadastroDeMembroSemCargo() throws Exception {
        CadastroMembro membro = new CadastroMembro();
        membro.setCpf("39999999999");
        membro.setPrimeiroNome("Teste");
        membro.setSobrenome("Validação");
        membro.setDataNascimento("1990-05-20");
        membro.setSenha("abc");
        String erro = esperarErro((repo, cb) -> repo.cadastrarMembro(membro, cb));
        assertEquals("Escolha o cargo.", erro);
    }

    @Test
    public void turmaComHorarioInvertido() throws Exception {
        CadastroTurma turma = new CadastroTurma();
        turma.setNomeTurma("Turma de Teste");
        turma.setCodigoCurso("1");
        turma.setVagas(10);
        turma.setDataAula("2026-11-20");
        turma.setHorarioInicio("15:00");
        turma.setHorarioFim("14:00");
        String erro = esperarErro((repo, cb) -> repo.criarTurma(turma, cb));
        assertEquals("O horário de fim precisa ser depois do início.", erro);
    }

    // ===== Auxiliares =====

    private CadastroAluno alunoValido() {
        CadastroAluno aluno = new CadastroAluno();
        aluno.setCpf("19999999999");
        aluno.setPrimeiroNome("Teste");
        aluno.setSobrenome("Validação");
        aluno.setDataNascimento("2014-01-01");
        aluno.setNomeEscola("EMEF Teste");
        aluno.setPeriodoEscola("Matutino");
        aluno.setCpfResponsavel("29999999999");
        aluno.setPrimeiroNomeResponsavel("Responsável");
        aluno.setSobrenomeResponsavel("Teste");
        aluno.setDataNascimentoResponsavel("1985-08-30");
        aluno.setGrauParentesco("Mãe");
        aluno.setNomeTurma("Violão A");
        aluno.setPeriodo("Manhã");
        aluno.setSenhaLogin("abc");
        return aluno;
    }

    // Chama o Repository e espera a resposta (no computador o callback vem em outra thread)
    private <T> T esperar(BiConsumer<AdminRepository, RepositoryCallback<T>> chamada) throws Exception {
        Object[] resultado = new Object[2];
        CountDownLatch fim = new CountDownLatch(1);
        chamada.accept(repositorio, new RepositoryCallback<T>() {
            @Override public void onSucesso(T r) { resultado[0] = r; fim.countDown(); }
            @Override public void onErro(String m) { resultado[1] = m; fim.countDown(); }
        });
        assertTrue("O servidor não respondeu a tempo", fim.await(30, TimeUnit.SECONDS));
        assertNull("Erro inesperado: " + resultado[1], resultado[1]);
        @SuppressWarnings("unchecked") T r = (T) resultado[0];
        return r;
    }

    // Todos os métodos testados aqui (cadastros e alterarSenha) devolvem RepositoryCallback<Void>
    private String esperarErro(BiConsumer<AdminRepository, RepositoryCallback<Void>> chamada) throws Exception {
        String[] erro = new String[1];
        boolean[] sucesso = new boolean[1];
        CountDownLatch fim = new CountDownLatch(1);
        chamada.accept(repositorio, new RepositoryCallback<Void>() {
            @Override public void onSucesso(Void r) { sucesso[0] = true; fim.countDown(); }
            @Override public void onErro(String m) { erro[0] = m; fim.countDown(); }
        });
        assertTrue("O servidor não respondeu a tempo", fim.await(30, TimeUnit.SECONDS));
        assertFalse("Era para dar erro, mas deu certo", sucesso[0]);
        return erro[0];
    }
}

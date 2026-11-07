package com.example.sisgesc.Repository;

import com.example.sisgesc.DTOs.AdminMembro;
import com.example.sisgesc.DTOs.CadastroAluno;
import com.example.sisgesc.DTOs.CadastroMembro;
import com.example.sisgesc.DTOs.CadastroTurma;
import com.example.sisgesc.DTOs.UsuarioLogado;
import com.example.sisgesc.Supabase.AdminService;
import com.example.sisgesc.Supabase.SupabaseClient;
import com.example.sisgesc.Supabase.SupabaseService;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

// Teste do ciclo completo do Administrador no banco de verdade:
// cria uma turma, um professor e um aluno fictícios, edita os três, testa o login deles
// e depois exclui tudo. Os dados de teste usam CPFs que começam com 199, 299 e 399.
// Mesmo se um passo falhar, o bloco finally apaga o que foi criado.
//
// DESLIGADO POR PADRÃO: este teste grava e apaga dados no banco que o grupo usa.
// Para rodar, apague a linha @Ignore abaixo e use:
//   gradlew :app:testDebugUnitTest --tests "*AdminCadastroTest"
// Depois, coloque o @Ignore de volta.
@Ignore("Grava no banco compartilhado. Rodar só de propósito (veja o comentário acima).")
public class AdminCadastroTest {

    private static final String TURMA = "Teste Admin";
    private static final String CPF_ALUNO = "19900000001";
    private static final String CPF_ALUNO_DESFEITO = "19900000003";
    private static final String CPF_RESPONSAVEL = "29900000001";
    private static final String CPF_MEMBRO = "39900000001";
    private static final String PROFESSOR_1 = "30000000002";   // Rafael
    private static final String PROFESSOR_2 = "30000000003";   // Camila

    private final AdminRepository repo = new AdminRepository();
    private final AuthRepository auth = new AuthRepository();

    @Test
    public void cicloCompletoDeCadastroEdicaoEExclusao() throws Exception {
        limparDadosDeTeste();
        try {
            turma();
            membro();
            aluno();
            cadastroComErroEDesfeito();
            regrasDeExclusao();
            exclusoes();
        } finally {
            limparDadosDeTeste();
        }
    }

    // ===== Turma =====

    private void turma() throws Exception {
        CadastroTurma nova = new CadastroTurma();
        nova.setNomeTurma(TURMA);
        nova.setCodigoCurso("1");
        nova.setVagas(10);
        nova.setDataAula("2026-12-01");
        nova.setHorarioInicio("09:00");
        nova.setHorarioFim("10:00");
        nova.setProfessor(PROFESSOR_1, "1");
        passo("Criar turma", cb -> repo.criarTurma(nova, cb));

        CadastroTurma salva = obter("Abrir turma para editar", cb -> repo.buscarCadastroTurma(TURMA, cb));
        assertEquals("1", salva.getCodigoCurso());
        assertEquals(10, salva.getVagas());
        assertEquals("09:00", salva.getHorarioInicio());
        assertEquals(PROFESSOR_1, salva.getCpfProfessor());

        salva.setVagas(12);
        salva.setDataAula("2026-12-02");
        salva.setHorarioInicio("10:00");
        salva.setHorarioFim("11:30");
        salva.setProfessor(PROFESSOR_2, "2");
        passo("Editar turma (vagas, data, horário e professor)", cb -> repo.editarTurma(salva, cb));

        CadastroTurma editada = obter("Conferir turma editada", cb -> repo.buscarCadastroTurma(TURMA, cb));
        assertEquals(12, editada.getVagas());
        assertEquals("2026-12-02", editada.getDataAula());
        assertEquals("10:00", editada.getHorarioInicio());
        assertEquals("11:30", editada.getHorarioFim());
        assertEquals(PROFESSOR_2, editada.getCpfProfessor());
        assertEquals("2", editada.getCodigoTipoMonitor());
    }

    // ===== Professor =====

    private void membro() throws Exception {
        CadastroMembro novo = new CadastroMembro();
        novo.setCpf(CPF_MEMBRO);
        novo.setPrimeiroNome("Professor");
        novo.setSobrenome("de Teste");
        novo.setDataNascimento("1990-05-20");
        novo.setSenha("teste123");
        novo.setCodigoCargo("2");
        novo.setEmail("professor.teste.admin@exemplo.org");
        novo.setTelefone("11", "900000391", "Celular");
        novo.adicionarTurma(TURMA, "1");
        passo("Cadastrar professor", cb -> repo.cadastrarMembro(novo, cb));

        UsuarioLogado login = obter("Login do professor novo", cb -> auth.entrar(CPF_MEMBRO, "teste123", cb));
        assertEquals(UsuarioLogado.Perfil.PROFESSOR, login.getPerfil());

        CadastroMembro salvo = obter("Abrir professor para editar", cb -> repo.buscarCadastroMembro(CPF_MEMBRO, cb));
        assertEquals("de Teste", salvo.getSobrenome());
        assertEquals("1990-05-20", salvo.getDataNascimento());
        assertEquals("professor.teste.admin@exemplo.org", salvo.getEmail());
        assertEquals("900000391", salvo.getNumeroTelefone());
        assertEquals("2", salvo.getCodigoCargo());
        assertEquals(1, salvo.getTurmas().size());

        salvo.setSobrenome("Editado");
        salvo.setEmail("professor.editado.admin@exemplo.org");
        salvo.setTelefone("21", "900000392", "Comercial");
        salvo.setSenha("nova456");
        salvo.limparTurmas();
        passo("Editar professor (nome, e-mail, telefone, senha e turmas)", cb -> repo.editarMembro(salvo, cb));

        CadastroMembro editado = obter("Conferir professor editado", cb -> repo.buscarCadastroMembro(CPF_MEMBRO, cb));
        assertEquals("Editado", editado.getSobrenome());
        assertEquals("professor.editado.admin@exemplo.org", editado.getEmail());
        assertEquals("21", editado.getDdd());
        assertEquals("Comercial", editado.getTipoTelefone());
        assertTrue(editado.getTurmas().isEmpty());
        this.<UsuarioLogado>obter("Login com a senha nova", cb -> auth.entrar(CPF_MEMBRO, "nova456", cb));

        // Trocar o cargo encerra o atual e cria outro (aqui: vira Coordenador e depois volta)
        editado.setCodigoCargo("1");
        passo("Trocar cargo para Coordenador", cb -> repo.editarMembro(editado, cb));
        UsuarioLogado admin = obter("Login como administrador", cb -> auth.entrar(CPF_MEMBRO, "nova456", cb));
        assertEquals(UsuarioLogado.Perfil.ADMINISTRADOR, admin.getPerfil());
    }

    // ===== Aluno =====

    private void aluno() throws Exception {
        CadastroAluno novo = new CadastroAluno();
        novo.setCpf(CPF_ALUNO);
        novo.setPrimeiroNome("Aluno");
        novo.setSobrenome("de Teste");
        novo.setDataNascimento("2015-04-10");
        novo.setNomeEscola("EMEF Teste");
        novo.setPeriodoEscola("Matutino");
        novo.setEmail("aluno.teste.admin@exemplo.org");
        novo.setTelefone("11", "900000191", "Celular");
        novo.setCpfResponsavel(CPF_RESPONSAVEL);
        novo.setPrimeiroNomeResponsavel("Responsável");
        novo.setSobrenomeResponsavel("de Teste");
        novo.setDataNascimentoResponsavel("1985-08-30");
        novo.setTelefoneResponsavel("11", "900000291");
        novo.setGrauParentesco("Mãe");
        novo.setNomeTurma(TURMA);
        novo.setPeriodo("Manhã");
        novo.setStatus("Ativo");
        novo.setSenhaLogin("aluno123");
        passo("Cadastrar aluno (com responsável e matrícula)", cb -> repo.cadastrarAluno(novo, cb));

        UsuarioLogado login = obter("Login do aluno novo", cb -> auth.entrar(CPF_ALUNO, "aluno123", cb));
        assertEquals(UsuarioLogado.Perfil.ALUNO, login.getPerfil());

        CadastroAluno salvo = obter("Abrir aluno para editar", cb -> repo.buscarCadastroAluno(CPF_ALUNO, cb));
        assertEquals("2015-04-10", salvo.getDataNascimento());
        assertEquals("EMEF Teste", salvo.getNomeEscola());
        assertEquals("aluno.teste.admin@exemplo.org", salvo.getEmail());
        assertEquals(CPF_RESPONSAVEL, salvo.getCpfResponsavel());
        assertEquals("900000291", salvo.getNumeroTelefoneResponsavel());
        assertEquals(TURMA, salvo.getNomeTurma());
        assertEquals("Manhã", salvo.getPeriodo());

        salvo.setSobrenome("Editado");
        salvo.setNomeEscola("EMEF Teste Editada");
        salvo.setPeriodoEscola("Vespertino");
        salvo.setEmail("");                     // apagar o e-mail
        salvo.setTelefone("11", "900000192", "Residencial");
        salvo.setTelefoneResponsavel("11", "900000292");
        salvo.setGrauParentesco("Pai");
        salvo.setAutorizadoBusca(false);
        salvo.setPeriodo("Tarde");
        salvo.setSenhaLogin("aluno456");
        passo("Editar aluno (nome, escola, contatos, responsável, matrícula e senha)", cb -> repo.editarAluno(salvo, cb));

        CadastroAluno editado = obter("Conferir aluno editado", cb -> repo.buscarCadastroAluno(CPF_ALUNO, cb));
        assertEquals("Editado", editado.getSobrenome());
        assertEquals("EMEF Teste Editada", editado.getNomeEscola());
        assertEquals("Vespertino", editado.getPeriodoEscola());
        assertNull(editado.getEmail());
        assertEquals("900000192", editado.getNumeroTelefone());
        assertEquals("Residencial", editado.getTipoTelefone());
        assertEquals("900000292", editado.getNumeroTelefoneResponsavel());
        assertEquals("Pai", editado.getGrauParentesco());
        assertFalse(editado.isAutorizadoBusca());
        assertEquals("Tarde", editado.getPeriodo());
        this.<UsuarioLogado>obter("Login do aluno com a senha nova", cb -> auth.entrar(CPF_ALUNO, "aluno456", cb));

        int id = editado.getIdMatricula();
        passo("Status da matrícula para Inativo", cb -> repo.alterarStatusMatricula(id, "Inativo", cb));
        passo("Status da matrícula para Ativo", cb -> repo.alterarStatusMatricula(id, "Ativo", cb));
    }

    // ===== Regras =====

    // Um cadastro que falha no meio não pode deixar nada gravado
    private void cadastroComErroEDesfeito() throws Exception {
        CadastroAluno ruim = new CadastroAluno();
        ruim.setCpf(CPF_ALUNO_DESFEITO);
        ruim.setPrimeiroNome("Aluno");
        ruim.setSobrenome("Desfeito");
        ruim.setDataNascimento("2015-01-01");
        ruim.setNomeEscola("EMEF Teste");
        ruim.setPeriodoEscola("Matutino");
        ruim.setEmail("aluno.desfeito.admin@exemplo.org");
        ruim.setCpfResponsavel(CPF_RESPONSAVEL);
        ruim.setPrimeiroNomeResponsavel("Responsável");
        ruim.setSobrenomeResponsavel("de Teste");
        ruim.setDataNascimentoResponsavel("1985-08-30");
        ruim.setGrauParentesco("Valor que não existe");   // o banco recusa só nesta etapa
        ruim.setNomeTurma(TURMA);
        ruim.setPeriodo("Manhã");
        ruim.setSenhaLogin("abc");
        String erro = tentar(cb -> repo.cadastrarAluno(ruim, cb));
        System.out.println("Cadastro com grau de parentesco inválido: " + erro);
        assertTrue(erro != null && erro.endsWith("Nada foi salvo."));
        Thread.sleep(3000);   // o desfazer roda em segundo plano
        assertEquals("Aluno não encontrado.", tentar(cb -> repo.buscarCadastroAluno(CPF_ALUNO_DESFEITO, cb)));
        boolean livre = SupabaseClient.getClient().create(SupabaseService.class)
                .buscarPessoaPorCpf("eq." + CPF_ALUNO_DESFEITO).execute().body().isEmpty();
        System.out.println("OK      CPF do cadastro desfeito ficou livre: " + livre);
        assertTrue(livre);
    }

    private void regrasDeExclusao() throws Exception {
        String erro = tentar(cb -> repo.excluirTurma(TURMA, cb));
        System.out.println("Excluir turma com aluno: " + erro);
        assertTrue(erro != null && erro.contains("matrícula"));

        passo("Desligar professor", cb -> repo.desligarMembro(CPF_MEMBRO, cb));
        List<AdminMembro> equipe = obter("Equipe depois de desligar", cb -> repo.buscarEquipe(cb));
        for (AdminMembro m : equipe) assertFalse(CPF_MEMBRO.equals(m.getCpf()));
    }

    private void exclusoes() throws Exception {
        passo("Excluir aluno", cb -> repo.excluirAluno(CPF_ALUNO, cb));
        String sumiu = tentar(cb -> repo.buscarCadastroAluno(CPF_ALUNO, cb));
        assertEquals("Aluno não encontrado.", sumiu);

        passo("Excluir professor", cb -> repo.excluirMembro(CPF_MEMBRO, cb));
        passo("Excluir turma", cb -> repo.excluirTurma(TURMA, cb));
        assertEquals("Turma não encontrada.", tentar(cb -> repo.buscarCadastroTurma(TURMA, cb)));
    }

    // ===== Limpeza =====

    // Apaga o que um teste anterior possa ter deixado. Erros aqui são ignorados.
    private void limparDadosDeTeste() throws Exception {
        tentar(cb -> repo.excluirAluno(CPF_ALUNO_DESFEITO, cb));
        tentar(cb -> repo.excluirAluno(CPF_ALUNO, cb));
        tentar(cb -> repo.excluirMembro(CPF_MEMBRO, cb));
        tentar(cb -> repo.excluirTurma(TURMA, cb));
        // O responsável não tem método de exclusão no Repository (pode ser de outro aluno)
        AdminService direto = SupabaseClient.getClient().create(AdminService.class);
        for (String[] alvo : new String[][]{
                {"tb_telefone", "fk_cpf"}, {"tb_responsavel", "fk_cpf"}, {"tb_pessoa", "pk_cpf"}}) {
            Map<String, String> filtro = new HashMap<>();
            filtro.put(alvo[1], "eq." + CPF_RESPONSAVEL);
            direto.excluir(alvo[0], filtro).execute();
        }
    }

    // ===== Auxiliares =====

    private void passo(String nome, Consumer<RepositoryCallback<Void>> chamada) throws Exception {
        Object[] resultado = new Object[2];
        esperar(chamada, resultado);
        String erro = (String) resultado[1];
        System.out.println((erro == null ? "OK    " : "FALHOU") + "  " + nome + (erro == null ? "" : " -> " + erro));
        assertNull(nome + ": " + erro, erro);
    }

    private <T> T obter(String nome, Consumer<RepositoryCallback<T>> chamada) throws Exception {
        Object[] resultado = new Object[2];
        esperar(chamada, resultado);
        System.out.println((resultado[1] == null ? "OK    " : "FALHOU") + "  " + nome
                + (resultado[1] == null ? "" : " -> " + resultado[1]));
        assertNull(nome + ": " + resultado[1], resultado[1]);
        @SuppressWarnings("unchecked") T r = (T) resultado[0];
        return r;
    }

    // Devolve a mensagem de erro, ou null se deu certo.
    // Tipo "cru" de propósito: aqui só importa se deu erro, não o tipo do resultado.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String tentar(Consumer<RepositoryCallback> chamada) throws Exception {
        Object[] resultado = new Object[2];
        esperar((Consumer) chamada, resultado);
        return (String) resultado[1];
    }

    private <T> void esperar(Consumer<RepositoryCallback<T>> chamada, Object[] resultado) throws Exception {
        CountDownLatch fim = new CountDownLatch(1);
        chamada.accept(new RepositoryCallback<T>() {
            @Override public void onSucesso(T r) { resultado[0] = r; fim.countDown(); }
            @Override public void onErro(String m) { resultado[1] = m; fim.countDown(); }
        });
        assertTrue("O servidor não respondeu a tempo", fim.await(30, TimeUnit.SECONDS));
    }
}

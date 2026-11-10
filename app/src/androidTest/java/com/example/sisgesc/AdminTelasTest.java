package com.example.sisgesc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.example.sisgesc.DTOs.AdminAluno;
import com.example.sisgesc.DTOs.AdminMembro;
import com.example.sisgesc.DTOs.AdminPerfil;
import com.example.sisgesc.DTOs.AdminResumo;
import com.example.sisgesc.DTOs.AdminTurma;
import com.example.sisgesc.DTOs.CadastroMembro;
import com.example.sisgesc.Repository.AdminRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

// Passa por todas as telas do Administrador no celular, clicando nos botões,
// e confere se o que aparece na tela é o que o banco devolve.
// Cria um coordenador fictício (CPF 39900000009) para entrar e o exclui no fim.
// DESLIGADO POR PADRÃO: grava no banco que o grupo usa. Para rodar, apague a linha @Ignore.
// O celular Xiaomi não aceita connectedAndroidTest: instalar os APKs com adb install -r e rodar com
//   adb shell am instrument -w -e class com.example.sisgesc.AdminTelasTest com.example.sisgesc.test/androidx.test.runner.AndroidJUnitRunner
// Os prints ficam em /sdcard/Android/data/com.example.sisgesc/files/.
@Ignore("Grava no banco compartilhado. Rodar só de propósito (veja o comentário acima).")
@RunWith(AndroidJUnit4.class)
public class AdminTelasTest {

    private static final String TAG = "AdminTelasTest";
    private static final String CPF = "39900000009";
    private static final String SENHA = "adminTeste1";
    private static final int ESPERA = 20;

    private final AdminRepository repo = new AdminRepository();
    // Cada verificação vira uma linha "OK ..." ou "FALHOU ..."; o teste só falha no fim, com tudo listado
    private final List<String> relatorio = new ArrayList<>();
    private int falhas;

    @Before
    public void criarCoordenadorDeTeste() throws Exception {
        this.<Void>buscar(cb -> repo.excluirMembro(CPF, cb));
        CadastroMembro coordenador = new CadastroMembro();
        coordenador.setCpf(CPF);
        coordenador.setPrimeiroNome("Coordenador");
        coordenador.setSobrenome("de Teste");
        coordenador.setDataNascimento("1980-01-15");
        coordenador.setSenha(SENHA);
        coordenador.setCodigoCargo("1");
        Object[] r = this.<Void>buscar(cb -> repo.cadastrarMembro(coordenador, cb));
        assertTrue("Não criou o coordenador de teste: " + r[1], r[1] == null);
    }

    @After
    public void excluirCoordenadorDeTeste() throws Exception {
        this.<Void>buscar(cb -> repo.excluirMembro(CPF, cb));
    }

    @Test
    public void todasAsTelasDoAdmin() throws Exception {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            login();
            dashboard();
            atalhosDoDashboard();
            alunos();
            turmas();
            equipe();
            perfil();
        }
        assertTrue(falhas + " verificação(ões) falharam:\n" + String.join("\n", relatorio), falhas == 0);
    }

    // ===== Telas =====

    private void login() throws Exception {
        Activity login = esperar(MainActivity.class);
        noMain(() -> {
            ((EditText) login.findViewById(R.id.edtCpf)).setText(CPF);
            ((EditText) login.findViewById(R.id.edtSenha)).setText(SENHA);
            login.findViewById(R.id.btnEntrarAluno).performClick();
        });
        checar("Login abre o Dashboard", esperar(DashboardActivity.class) instanceof DashboardActivity);
    }

    private void dashboard() throws Exception {
        AdminResumo resumo = valor(cb -> repo.buscarResumo(CPF, cb));
        List<AdminAluno> recentes = valor(cb -> repo.buscarAlunosRecentes(5, cb));
        Activity tela = esperar(DashboardActivity.class);

        checarTexto(tela, R.id.tvTotalAlunos, String.valueOf(resumo.getTotalAlunos()), "Dashboard: total de alunos");
        checarTexto(tela, R.id.tvTotalTurmas, String.valueOf(resumo.getTotalTurmas()), "Dashboard: total de turmas");
        checarTexto(tela, R.id.tvTotalProfessores, String.valueOf(resumo.getTotalProfessores()), "Dashboard: total de professores");
        checarTexto(tela, R.id.tvFrequenciaGeral, resumo.getFrequenciaTexto(), "Dashboard: frequência geral");
        checarTexto(tela, R.id.tvNomeUsuario, "Coordenador! 🎓", "Dashboard: nome de quem entrou");
        checar("Dashboard: alunos recentes (" + recentes.size() + ")", esperarAte(() ->
                ((LinearLayout) tela.findViewById(R.id.llAlunosRecentes)).getChildCount() == recentes.size()));
        print("01_dashboard");

        if (!recentes.isEmpty()) {
            clicar(((LinearLayout) tela.findViewById(R.id.llAlunosRecentes)).getChildAt(0));
            formularioEdicao(activity_admin_novo_aluno.class, "Editar aluno",
                    FormularioAdmin.EXTRA_CPF_ALUNO, recentes.get(0).getCpf(), "Dashboard: aluno recente");
        }
    }

    private void atalhosDoDashboard() throws Exception {
        Activity dash = esperar(DashboardActivity.class);

        // Consultar frequência
        List<AdminTurma> turmas = valor(cb -> repo.buscarTurmas(cb));
        int presencas = 0;
        for (AdminTurma t : turmas) presencas += t.getPresencas();
        clicar(dash.findViewById(R.id.cardConsultarFrequencia));
        Activity freq = esperar(activity_admin_frequencia.class);
        checar("Atalho Consultar frequência abre a tela", freq instanceof activity_admin_frequencia);
        if (freq instanceof activity_admin_frequencia) {
            checarTexto(freq, R.id.tvTituloForm, "Consultar frequência", "Frequência: título");
            checarLista(freq, R.id.rvFrequencia, turmas.size(), "Frequência: turmas");
            checarTexto(freq, R.id.tvTotalPresencas, String.valueOf(presencas), "Frequência: total de presenças");
            print("02_frequencia");
            voltar(freq, DashboardActivity.class, "Frequência: seta de voltar");
        }

        // Cadastrar aluno
        clicar(dash.findViewById(R.id.cardCadastrarAluno));
        Activity aluno = formularioNovo(activity_admin_novo_aluno.class, "Cadastrar aluno", "Atalho Cadastrar aluno");
        if (aluno != null) {
            checarSpinner(aluno, R.id.spPeriodoEscola, 2, "Novo aluno: período na escola");
            checarSpinner(aluno, R.id.spTipoTelefone, 3, "Novo aluno: tipo de telefone");
            checarSpinner(aluno, R.id.spGrauParentesco, 4, "Novo aluno: parentesco");
            checarSpinner(aluno, R.id.spPeriodo, 2, "Novo aluno: período da matrícula");
            checar("Novo aluno: começa com Ativo marcado", naMain(() -> aluno.findViewById(R.id.statusAtivo).isSelected()));
            clicar(aluno.findViewById(R.id.statusInativo));
            checar("Novo aluno: tocar em Inativo troca o status", naMain(() ->
                    aluno.findViewById(R.id.statusInativo).isSelected() && !aluno.findViewById(R.id.statusAtivo).isSelected()));
            print("03_novo_aluno");
            voltar(aluno, DashboardActivity.class, "Novo aluno: seta de voltar");
        }

        // Nova turma
        clicar(dash.findViewById(R.id.cardNovaTurma));
        Activity turma = formularioNovo(activity_admin_nova_turma.class, "Nova turma", "Atalho Nova turma");
        if (turma != null) voltar(turma, DashboardActivity.class, "Nova turma: seta de voltar");

        // Cadastrar professor
        clicar(dash.findViewById(R.id.cardCadastrarProfessor));
        Activity membro = formularioNovo(activity_admin_novo_membro.class, "Novo membro", "Atalho Cadastrar professor");
        if (membro != null) {
            checarSpinner(membro, R.id.spTipoTelefone, 3, "Novo membro: tipo de telefone");
            LinearLayout llTurmas = membro.findViewById(R.id.llTurmas);
            clicar(membro.findViewById(R.id.btnAdicionarTurma));
            clicar(membro.findViewById(R.id.btnAdicionarTurma));
            checar("Novo membro: Adicionar turma cria linhas", naMain(() -> llTurmas.getChildCount() == 2));
            print("04_novo_membro");
            clicar(llTurmas.getChildAt(0).findViewById(R.id.btnRemoverTurma));
            checar("Novo membro: X remove a linha", naMain(() -> llTurmas.getChildCount() == 1));
            voltar(membro, DashboardActivity.class, "Novo membro: seta de voltar");
        }
    }

    private void alunos() throws Exception {
        List<AdminAluno> todos = valor(cb -> repo.buscarAlunos("", cb));
        clicar(esperar(DashboardActivity.class).findViewById(R.id.navAlunos));
        Activity tela = esperar(activity_admin_alunos.class);
        checar("Menu Alunos abre a tela", tela instanceof activity_admin_alunos);
        if (!(tela instanceof activity_admin_alunos)) return;

        checarLista(tela, R.id.rvAlunos, todos.size(), "Alunos: lista");
        print("05_alunos");

        if (!todos.isEmpty()) {
            String termo = todos.get(0).getNome().split(" ")[0];
            List<AdminAluno> achados = valor(cb -> repo.buscarAlunos(termo, cb));
            noMain(() -> ((EditText) tela.findViewById(R.id.etBuscar)).setText(termo));
            checarLista(tela, R.id.rvAlunos, achados.size(), "Alunos: pesquisa \"" + termo + "\"");
            print("06_alunos_pesquisa");

            clicarItem(tela, R.id.rvAlunos);
            formularioEdicao(activity_admin_novo_aluno.class, "Editar aluno",
                    FormularioAdmin.EXTRA_CPF_ALUNO, achados.get(0).getCpf(), "Alunos: tocar no aluno");
        }

        clicar(tela.findViewById(R.id.btnNovoAluno));
        Activity novo = formularioNovo(activity_admin_novo_aluno.class, "Cadastrar aluno", "Alunos: botão Novo");
        if (novo != null) voltar(novo, activity_admin_alunos.class, "Alunos: voltar do formulário");
    }

    private void turmas() throws Exception {
        List<AdminTurma> turmas = valor(cb -> repo.buscarTurmas(cb));
        clicar(esperar(activity_admin_alunos.class).findViewById(R.id.navTurmas));
        Activity tela = esperar(activity_admin_turmas.class);
        checar("Menu Turmas abre a tela", tela instanceof activity_admin_turmas);
        if (!(tela instanceof activity_admin_turmas)) return;

        checarLista(tela, R.id.rvTurmas, turmas.size(), "Turmas: lista");
        print("07_turmas");

        if (!turmas.isEmpty()) {
            clicarItem(tela, R.id.rvTurmas);
            Activity edicao = formularioEdicao(activity_admin_nova_turma.class, "Editar turma",
                    FormularioAdmin.EXTRA_NOME_TURMA, turmas.get(0).getNomeTurma(), "Turmas: tocar na turma");
            if (edicao == null) return;
        }

        clicar(tela.findViewById(R.id.btnNovaTurma));
        Activity nova = formularioNovo(activity_admin_nova_turma.class, "Nova turma", "Turmas: botão Nova Turma");
        if (nova != null) voltar(nova, activity_admin_turmas.class, "Turmas: voltar do formulário");
    }

    private void equipe() throws Exception {
        List<AdminMembro> equipe = valor(cb -> repo.buscarEquipe(cb));
        clicar(esperar(activity_admin_turmas.class).findViewById(R.id.navEquipe));
        Activity tela = esperar(activity_admin_equipe.class);
        checar("Menu Equipe abre a tela", tela instanceof activity_admin_equipe);
        if (!(tela instanceof activity_admin_equipe)) return;

        checarLista(tela, R.id.rvEquipe, equipe.size(), "Equipe: lista");
        print("08_equipe");

        if (!equipe.isEmpty()) {
            clicarItem(tela, R.id.rvEquipe);
            Activity edicao = formularioEdicao(activity_admin_novo_membro.class, "Editar membro",
                    FormularioAdmin.EXTRA_CPF_MEMBRO, equipe.get(0).getCpf(), "Equipe: tocar no membro");
            if (edicao == null) return;
        }

        clicar(tela.findViewById(R.id.btnNovoMembro));
        Activity novo = formularioNovo(activity_admin_novo_membro.class, "Novo membro", "Equipe: botão Novo");
        if (novo != null) voltar(novo, activity_admin_equipe.class, "Equipe: voltar do formulário");
    }

    private void perfil() throws Exception {
        AdminPerfil perfil = valor(cb -> repo.buscarPerfil(CPF, cb));
        clicar(esperar(activity_admin_equipe.class).findViewById(R.id.navPerfil));
        Activity tela = esperar(activity_admin_perfil.class);
        checar("Menu Perfil abre a tela", tela instanceof activity_admin_perfil);
        if (!(tela instanceof activity_admin_perfil)) return;

        checarTexto(tela, R.id.txtNomePerfil, perfil.getNomeCompleto(), "Perfil: nome");
        checarTexto(tela, R.id.txtTipoPerfil, perfil.getCargo(), "Perfil: cargo");
        print("09_perfil");

        clicar(tela.findViewById(R.id.navInicio));
        checar("Menu Início volta ao Dashboard", esperar(DashboardActivity.class) instanceof DashboardActivity);

        clicar(esperar(DashboardActivity.class).findViewById(R.id.navPerfil));
        Activity denovo = esperar(activity_admin_perfil.class);
        clicar(denovo.findViewById(R.id.btnSairPerfil));
        checar("Perfil: Sair volta ao login", esperar(MainActivity.class) instanceof MainActivity);
    }

    // ===== Formulários =====

    private Activity formularioNovo(Class<? extends Activity> classe, String titulo, String nome) throws Exception {
        Activity form = esperar(classe);
        checar(nome + " abre o formulário", classe.isInstance(form));
        if (!classe.isInstance(form)) return null;
        checarTexto(form, R.id.tvTituloForm, titulo, nome + ": título");
        checar(nome + ": sem botão Excluir", naMain(() -> form.findViewById(R.id.btnExcluir).getVisibility() == View.GONE));
        return form;
    }

    // Confere o modo edição e volta pela seta para a tela de onde veio
    private Activity formularioEdicao(Class<? extends Activity> classe, String titulo, String extra,
                                      String valorEsperado, String nome) throws Exception {
        Activity origem = atual();
        Activity form = esperar(classe);
        checar(nome + " abre a edição", classe.isInstance(form));
        if (!classe.isInstance(form)) return null;
        checarTexto(form, R.id.tvTituloForm, titulo, nome + ": título");
        String recebido = form.getIntent().getStringExtra(extra);
        checar(nome + ": recebeu " + valorEsperado + " (veio " + recebido + ")", valorEsperado.equals(recebido));
        checar(nome + ": botão Excluir aparece", naMain(() -> form.findViewById(R.id.btnExcluir).getVisibility() == View.VISIBLE));
        noMain(() -> form.findViewById(R.id.btnVoltar).performClick());
        esperarAte(() -> !classe.isInstance(atual()));
        return form;
    }

    private void voltar(Activity form, Class<? extends Activity> destino, String nome) throws Exception {
        clicar(form.findViewById(R.id.btnVoltar));
        checar(nome, destino.isInstance(esperar(destino)));
    }

    // ===== Verificações =====

    private void checar(String nome, boolean ok) {
        String linha = (ok ? "OK      " : "FALHOU  ") + nome;
        relatorio.add(linha);
        Log.i(TAG, linha);
        if (!ok) falhas++;
    }

    private void checarTexto(Activity tela, int id, String esperado, String nome) throws Exception {
        boolean ok = esperarAte(() -> String.valueOf(esperado).equals(((TextView) tela.findViewById(id)).getText().toString()));
        String atual = naMain(() -> ((TextView) tela.findViewById(id)).getText().toString());
        checar(nome + ": \"" + atual + "\"" + (ok ? "" : " (esperado \"" + esperado + "\")"), ok);
    }

    private void checarLista(Activity tela, int id, int esperado, String nome) throws Exception {
        boolean ok = esperarAte(() -> {
            RecyclerView rv = tela.findViewById(id);
            return rv.getAdapter() != null && rv.getAdapter().getItemCount() == esperado;
        });
        int atual = naMain(() -> ((RecyclerView) tela.findViewById(id)).getAdapter().getItemCount());
        checar(nome + ": " + atual + " itens" + (ok ? "" : " (banco tem " + esperado + ")"), ok);
    }

    private void checarSpinner(Activity tela, int id, int esperado, String nome) throws Exception {
        int atual = naMain(() -> {
            Spinner sp = tela.findViewById(id);
            return sp.getAdapter() == null ? 0 : sp.getAdapter().getCount();
        });
        checar(nome + ": " + atual + " opções", atual == esperado);
    }

    // ===== Ferramentas =====

    private void clicar(View view) {
        noMain(view::performClick);
    }

    private void clicarItem(Activity tela, int idLista) throws Exception {
        RecyclerView rv = tela.findViewById(idLista);
        esperarAte(() -> rv.findViewHolderForAdapterPosition(0) != null);
        noMain(() -> rv.findViewHolderForAdapterPosition(0).itemView.performClick());
    }

    private Activity atual() {
        Activity[] aberta = new Activity[1];
        noMain(() -> {
            Collection<Activity> ativas = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            aberta[0] = ativas.isEmpty() ? null : ativas.iterator().next();
        });
        return aberta[0];
    }

    private Activity esperar(Class<?> tela) throws Exception {
        esperarAte(() -> tela.isInstance(atual()));
        return atual();
    }

    private boolean esperarAte(Callable<Boolean> condicao) throws Exception {
        for (int i = 0; i < ESPERA * 4; i++) {
            if (Boolean.TRUE.equals(naMain(condicao))) return true;
            Thread.sleep(250);
        }
        return false;
    }

    private void noMain(Runnable acao) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(acao);
    }

    private <T> T naMain(Callable<T> conta) {
        Object[] r = new Object[1];
        noMain(() -> {
            try {
                r[0] = conta.call();
            } catch (Exception e) {
                r[0] = null;
            }
        });
        @SuppressWarnings("unchecked") T valor = (T) r[0];
        return valor;
    }

    private void print(String nome) throws Exception {
        Thread.sleep(800);
        Bitmap print = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        File pasta = InstrumentationRegistry.getInstrumentation().getTargetContext().getExternalFilesDir(null);
        try (FileOutputStream saida = new FileOutputStream(new File(pasta, nome + ".png"))) {
            print.compress(Bitmap.CompressFormat.PNG, 100, saida);
        }
    }

    private interface Chamada<T> {
        void fazer(RepositoryCallback<T> callback);
    }

    private <T> T valor(Chamada<T> chamada) throws Exception {
        Object[] r = buscar(chamada);
        assertTrue("O banco devolveu erro: " + r[1], r[1] == null);
        @SuppressWarnings("unchecked") T valor = (T) r[0];
        return valor;
    }

    // [0] = resultado, [1] = mensagem de erro (null se deu certo)
    private <T> Object[] buscar(Chamada<T> chamada) throws Exception {
        Object[] r = new Object[2];
        CountDownLatch fim = new CountDownLatch(1);
        chamada.fazer(new RepositoryCallback<T>() {
            @Override public void onSucesso(T resultado) { r[0] = resultado; fim.countDown(); }
            @Override public void onErro(String mensagem) { r[1] = mensagem; fim.countDown(); }
        });
        assertTrue("O servidor não respondeu a tempo", fim.await(30, TimeUnit.SECONDS));
        return r;
    }
}

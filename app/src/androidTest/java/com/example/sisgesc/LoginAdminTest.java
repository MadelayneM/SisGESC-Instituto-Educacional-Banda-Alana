package com.example.sisgesc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

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
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

// Login de verdade, na tela do app: CPF e senha de um Coordenador abrem o Dashboard do Admin.
// Cria um coordenador fictício antes (CPF 39900000009) e o exclui no fim.
// DESLIGADO POR PADRÃO: cria e apaga um coordenador no banco que o grupo usa.
// Para rodar, apague a linha @Ignore e use (com celular ou emulador ligado):
//   gradlew :app:connectedDebugAndroidTest
@Ignore("Grava no banco compartilhado. Rodar só de propósito (veja o comentário acima).")
@RunWith(AndroidJUnit4.class)
public class LoginAdminTest {

    private static final String CPF = "39900000009";
    private static final String SENHA = "adminTeste1";

    private final AdminRepository repo = new AdminRepository();

    @Before
    public void criarCoordenadorDeTeste() throws Exception {
        aguardar(cb -> repo.excluirMembro(CPF, cb));   // sobra de um teste anterior, se houver
        CadastroMembro coordenador = new CadastroMembro();
        coordenador.setCpf(CPF);
        coordenador.setPrimeiroNome("Coordenador");
        coordenador.setSobrenome("de Teste");
        coordenador.setDataNascimento("1980-01-15");
        coordenador.setSenha(SENHA);
        coordenador.setCodigoCargo("1");                // 1 = Coordenador
        assertNull(aguardar(cb -> repo.cadastrarMembro(coordenador, cb)));
    }

    @After
    public void excluirCoordenadorDeTeste() throws Exception {
        aguardar(cb -> repo.excluirMembro(CPF, cb));
    }

    @Test
    public void loginDoAdminAbreODashboard() throws Exception {
        try (ActivityScenario<MainActivity> tela = ActivityScenario.launch(MainActivity.class)) {
            tela.onActivity(login -> {
                ((EditText) login.findViewById(R.id.edtCpf)).setText(CPF);
                ((EditText) login.findViewById(R.id.edtSenha)).setText(SENHA);
                login.findViewById(R.id.btnEntrarAluno).performClick();
            });

            Activity aberta = esperarTelaAberta(DashboardActivity.class, 20);
            assertTrue("O login não abriu o Dashboard do Admin", aberta instanceof DashboardActivity);
            assertEquals("O Dashboard não recebeu o CPF de quem entrou",
                    CPF, aberta.getIntent().getStringExtra(MainActivity.EXTRA_CPF));

            Thread.sleep(1000);
            salvarPrint("login_admin_dashboard.png");
        }
    }

    // Espera até a tela pedida ficar na frente (ou o tempo acabar) e devolve a que estiver aberta
    private Activity esperarTelaAberta(Class<?> tela, int segundos) throws Exception {
        Activity[] aberta = new Activity[1];
        for (int i = 0; i < segundos * 4; i++) {
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                Collection<Activity> ativas = ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.RESUMED);
                aberta[0] = ativas.isEmpty() ? null : ativas.iterator().next();
            });
            if (tela.isInstance(aberta[0])) break;
            Thread.sleep(250);
        }
        return aberta[0];
    }

    private void salvarPrint(String nome) throws Exception {
        Bitmap print = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        File pasta = InstrumentationRegistry.getInstrumentation().getTargetContext().getExternalFilesDir(null);
        try (FileOutputStream saida = new FileOutputStream(new File(pasta, nome))) {
            print.compress(Bitmap.CompressFormat.PNG, 100, saida);
        }
    }

    private interface Chamada {
        void fazer(RepositoryCallback<Void> callback);
    }

    // Devolve a mensagem de erro, ou null se deu certo
    private String aguardar(Chamada chamada) throws Exception {
        String[] erro = new String[1];
        CountDownLatch fim = new CountDownLatch(1);
        chamada.fazer(new RepositoryCallback<Void>() {
            @Override public void onSucesso(Void r) { fim.countDown(); }
            @Override public void onErro(String m) { erro[0] = m; fim.countDown(); }
        });
        assertTrue("O servidor não respondeu a tempo", fim.await(30, TimeUnit.SECONDS));
        return erro[0];
    }
}

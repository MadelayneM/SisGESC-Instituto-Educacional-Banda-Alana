package com.example.sisgesc;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

// Configura o menu inferior das telas do Administrador (layout menu_inferior_admin.xml).
// Cada tela só precisa chamar MenuAdmin.configurar(this, R.id.botaoDaPropriaTela).
public final class MenuAdmin {

    // Tela que abre no botão "Início"
    private static final Class<?> TELA_INICIO = DashboardActivity.class;

    private MenuAdmin() {
    }

    public static void configurar(AppCompatActivity telaAtual, @IdRes int botaoDaTelaAtual) {
        // Marca o botão da tela atual, e as cores em res/color o deixam destacado
        telaAtual.findViewById(botaoDaTelaAtual).setSelected(true);

        telaAtual.findViewById(R.id.navInicio).setOnClickListener(v -> abrirTela(telaAtual, TELA_INICIO));
        telaAtual.findViewById(R.id.navAlunos).setOnClickListener(v -> abrirTela(telaAtual, activity_admin_alunos.class));
        // A tela de lista de Turmas ainda não existe neste módulo
        telaAtual.findViewById(R.id.navTurmas).setOnClickListener(v ->
                Toast.makeText(telaAtual, "A tela de Turmas ainda não está disponível.", Toast.LENGTH_SHORT).show());
        telaAtual.findViewById(R.id.navEquipe).setOnClickListener(v -> abrirTela(telaAtual, activity_admin_equipe.class));
        telaAtual.findViewById(R.id.navPerfil).setOnClickListener(v -> abrirTela(telaAtual, activity_admin_perfil.class));
    }

    private static void abrirTela(AppCompatActivity telaAtual, Class<?> telaDestino) {
        // Clicou no botão da tela que já está aberta: não faz nada
        if (telaAtual.getClass() == telaDestino) {
            return;
        }

        // Passa o CPF adiante para a próxima tela também saber quem entrou
        Intent intent = new Intent(telaAtual, telaDestino);
        intent.putExtra(MainActivity.EXTRA_CPF, telaAtual.getIntent().getStringExtra(MainActivity.EXTRA_CPF));

        if (telaDestino == TELA_INICIO) {
            // Volta para o Início que já está aberto, em vez de criar outro
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        telaAtual.startActivity(intent);

        // Fecha a tela atual, menos o Início. Assim o botão "voltar" do celular sempre leva
        // ao Início, em vez de passar por todas as telas visitadas.
        if (telaAtual.getClass() != TELA_INICIO) {
            telaAtual.finish();
        }
    }
}

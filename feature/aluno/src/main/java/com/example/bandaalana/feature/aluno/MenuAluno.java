package com.example.bandaalana.feature.aluno;

import android.content.Intent;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

// Configura o menu inferior das telas do Aluno (layout menu_inferior_aluno.xml).
// Cada tela só precisa chamar MenuAluno.configurar(this, R.id.botaoDaPropriaTela).
public final class MenuAluno {

    private MenuAluno() {
    }

    public static void configurar(AppCompatActivity telaAtual, @IdRes int botaoDaTelaAtual) {
        // Marca o botão da tela atual, e as cores em res/color o deixam destacado
        telaAtual.findViewById(botaoDaTelaAtual).setSelected(true);

        telaAtual.findViewById(R.id.navInicio).setOnClickListener(v -> abrirTela(telaAtual, dashboardaluno.class));
        telaAtual.findViewById(R.id.navTurma).setOnClickListener(v -> abrirTela(telaAtual, minha_turma.class));
        telaAtual.findViewById(R.id.navFrequencia).setOnClickListener(v -> abrirTela(telaAtual, minha_frequencia.class));
        telaAtual.findViewById(R.id.navPerfil).setOnClickListener(v -> abrirTela(telaAtual, perfil_aluno.class));
    }

    private static void abrirTela(AppCompatActivity telaAtual, Class<?> telaDestino) {
        // Clicou no botão da tela que já está aberta: não faz nada
        if (telaAtual.getClass() == telaDestino) {
            return;
        }

        // Passa o CPF adiante para a próxima tela também saber quem entrou
        Intent intent = new Intent(telaAtual, telaDestino);
        intent.putExtra(LoginAlunoActivity.EXTRA_CPF, telaAtual.getIntent().getStringExtra(LoginAlunoActivity.EXTRA_CPF));

        if (telaDestino == dashboardaluno.class) {
            // Volta para o Início que já está aberto, em vez de criar outro
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        telaAtual.startActivity(intent);

        // Fecha a tela atual, menos o Início. Assim o botão "voltar" do celular sempre leva
        // ao Início, em vez de passar por todas as telas visitadas.
        if (!(telaAtual instanceof dashboardaluno)) {
            telaAtual.finish();
        }
    }
}

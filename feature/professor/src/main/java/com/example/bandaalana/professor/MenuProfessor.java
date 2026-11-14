package com.example.bandaalana.professor;

import android.content.Intent;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

// Configura o menu inferior das telas do Professor (layout menu_inferior_professor.xml).
// Cada tela só precisa chamar MenuProfessor.configurar(this, R.id.botaoDaPropriaTela).
public final class MenuProfessor {

    // Tela que abre no botão "Início"
    private static final Class<?> TELA_INICIO = dashboardProfessor.class;

    private MenuProfessor() {
    }

    public static void configurar(AppCompatActivity telaAtual, @IdRes int botaoDaTelaAtual) {
        // Marca o botão da tela atual, e a cor em res/color/menu_professor_texto.xml o deixa destacado
        telaAtual.findViewById(botaoDaTelaAtual).setSelected(true);

        telaAtual.findViewById(R.id.navInicio).setOnClickListener(v -> abrirTela(telaAtual, TELA_INICIO));
        telaAtual.findViewById(R.id.navAlunos).setOnClickListener(v -> abrirTela(telaAtual, alunosProfessor.class));
        telaAtual.findViewById(R.id.navTurmas).setOnClickListener(v -> abrirTela(telaAtual, turmasProfessor.class));
        telaAtual.findViewById(R.id.navFrequencia).setOnClickListener(v -> abrirTela(telaAtual, frequenciaProfessor.class));
        telaAtual.findViewById(R.id.navPerfil).setOnClickListener(v -> abrirTela(telaAtual, perfilProfessor.class));
    }

    private static void abrirTela(AppCompatActivity telaAtual, Class<?> telaDestino) {
        // Clicou no botão da tela que já está aberta: não faz nada
        if (telaAtual.getClass() == telaDestino) {
            return;
        }

        // Passa o CPF adiante para a próxima tela também saber quem entrou
        Intent intent = new Intent(telaAtual, telaDestino);
        intent.putExtra(LoginProfessorActivity.EXTRA_CPF, telaAtual.getIntent().getStringExtra(LoginProfessorActivity.EXTRA_CPF));

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

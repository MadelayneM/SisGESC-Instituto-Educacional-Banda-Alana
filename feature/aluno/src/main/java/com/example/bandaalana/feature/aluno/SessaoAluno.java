package com.example.bandaalana.feature.aluno;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

// Ajuda as telas do Aluno a saber quem entrou e a sair do app.
public final class SessaoAluno {

    private SessaoAluno() {
    }

    // Devolve o CPF que o login mandou para esta tela.
    // Se ele não veio (tela aberta sem passar pelo login), volta para o login e devolve null.
    public static String cpfOuVoltarAoLogin(AppCompatActivity tela) {
        String cpf = tela.getIntent().getStringExtra(LoginAlunoActivity.EXTRA_CPF);
        if (cpf == null || cpf.isEmpty()) {
            sair(tela);
            return null;
        }
        return cpf;
    }

    // Volta para o login e apaga as telas anteriores, para o botão "voltar" não reabrir a conta
    public static void sair(AppCompatActivity tela) {
        Intent intent = new Intent(tela, LoginAlunoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        tela.startActivity(intent);
        tela.finish();
    }

    // As respostas chegam depois de alguns instantes. Se a tela já fechou nesse meio tempo,
    // mexer nas views dela daria erro.
    public static boolean telaFechada(AppCompatActivity tela) {
        return tela.isFinishing() || tela.isDestroyed();
    }
}

package com.example.navigation;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Destinations {
    // Endereços completos (Pacote + Classe) das telas principais
    public static final String SELECAO_PERFIL = "com.example.bandaalana.MainActivity";
    public static final String ALUNO_INICIO = "com.example.bandaalana.aluno.MainActivity";
    public static final String PROFESSOR_INICIO = "com.example.bandaalana.professor.MainActivity";
    public static final String ADMIN_INICIO = "com.example.bandaalana.administrador.MainActivity";

    // Chaves padronizadas para envio de dados entre telas (Extras)
    public static final String EXTRA_CPF = "extra_cpf";

    private Destinations() {
    }

    // Métodos prontos para navegar entre os módulos
    public static void irParaAluno(Context context) {
        navegar(context, ALUNO_INICIO);
    }

    public static void irParaProfessor(Context context) {
        navegar(context, PROFESSOR_INICIO);
    }

    public static void irParaAdministrador(Context context) {
        navegar(context, ADMIN_INICIO);
    }

    // Volta para a tela inicial de escolha de perfis e limpa o histórico de telas (Logout)
    public static void voltarParaSelecaoPerfil(Context context) {
        try {
            Class<?> classe = Class.forName(SELECAO_PERFIL);
            Intent intent = new Intent(context, classe);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "Tela inicial não encontrada", Toast.LENGTH_SHORT).show();
        }
    }

    private static void navegar(Context context, String destinoClasse) {
        try {
            Class<?> classe = Class.forName(destinoClasse);
            Intent intent = new Intent(context, classe);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "Módulo não encontrado: " + destinoClasse, Toast.LENGTH_SHORT).show();
        }
    }
}

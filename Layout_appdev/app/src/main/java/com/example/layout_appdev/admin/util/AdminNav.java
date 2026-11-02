 package com.example.layout_appdev.admin.util;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.AdminAlunosActivity;
import com.example.layout_appdev.admin.AdminDashboardActivity;
import com.example.layout_appdev.admin.AdminEquipeActivity;
import com.example.layout_appdev.admin.AdminPerfilActivity;
import com.example.layout_appdev.admin.AdminTurmasActivity;

/** Configura a barra de navegação inferior das telas do Administrador. */
public final class AdminNav {

    private AdminNav() {}

    public static void configurar(Activity activity, int itemSelecionado) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        nav.setSelectedItemId(itemSelecionado);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == itemSelecionado) return true;

            Class<?> destino;
            if (id == R.id.nav_inicio) {
                destino = AdminDashboardActivity.class;
            } else if (id == R.id.nav_alunos) {
                destino = AdminAlunosActivity.class;
            } else if (id == R.id.nav_turmas) {
                destino = AdminTurmasActivity.class;
            } else if (id == R.id.nav_equipe) {
                destino = AdminEquipeActivity.class;
            } else {
                destino = AdminPerfilActivity.class;
            }

            Intent intent = new Intent(activity, destino);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            if (id == R.id.nav_inicio) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            activity.startActivity(intent);
            // O Dashboard fica sempre embaixo na pilha; as demais abas se substituem
            if (!(activity instanceof AdminDashboardActivity)) {
                activity.finish();
            }
            activity.overridePendingTransition(0, 0);
            // false: a aba não fica marcada nesta tela; a tela de destino marca a sua própria
            return false;
        });
    }
}

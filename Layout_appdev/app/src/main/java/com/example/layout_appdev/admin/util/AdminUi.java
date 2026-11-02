package com.example.layout_appdev.admin.util;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_appdev.R;

public final class AdminUi {

    private AdminUi() {}

    /** Evita que o conteúdo fique atrás da barra de status/navegação (Android 15+ é edge-to-edge). */
    public static void aplicarInsets(Activity activity) {
        View raiz = activity.findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
    }

    /** Preenche o título e liga a seta de voltar do cabeçalho dos formulários (view_form_header). */
    public static void configurarCabecalho(Activity activity, String titulo) {
        ((TextView) activity.findViewById(R.id.tvTituloForm)).setText(titulo);
        activity.findViewById(R.id.btnVoltar).setOnClickListener(v -> activity.finish());
    }

    public static void adicionarDivisor(RecyclerView rv) {
        DividerItemDecoration divisor =
                new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        Drawable d = ContextCompat.getDrawable(rv.getContext(), R.drawable.divider_list);
        if (d != null) divisor.setDrawable(d);
        rv.addItemDecoration(divisor);
    }

    /** Pinta um "chip" (TextView com bg_chip) com cor de fundo e de texto. */
    public static void pintarChip(TextView chip, @ColorRes int fundo, @ColorRes int texto) {
        chip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(chip.getContext(), fundo)));
        chip.setTextColor(ContextCompat.getColor(chip.getContext(), texto));
    }

    /** Mostra a foto escolhida ou, se não houver (ou der erro), o desenho padrão. */
    public static void carregarFoto(ImageView iv, String uri, @DrawableRes int padrao) {
        if (uri == null) {
            iv.setImageResource(padrao);
            return;
        }
        try {
            iv.setImageURI(Uri.parse(uri));
        } catch (Exception e) {
            iv.setImageResource(padrao);
        }
    }

    /** "João Silva" -> "JS" */
    public static String iniciais(String nome) {
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 0 || partes[0].isEmpty()) return "?";
        String ini = partes[0].substring(0, 1);
        if (partes.length > 1) ini += partes[partes.length - 1].substring(0, 1);
        return ini.toUpperCase();
    }

    public static String soDigitos(String texto) {
        return texto.replaceAll("\\D", "");
    }

    public static boolean emailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

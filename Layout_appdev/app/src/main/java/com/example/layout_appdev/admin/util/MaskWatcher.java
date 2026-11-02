package com.example.layout_appdev.admin.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Máscara de digitação. Use '#' para cada dígito. Aceita várias máscaras:
 * escolhe a primeira que comporta a quantidade de dígitos digitados.
 * Ex.: new MaskWatcher(et, "(##) ####-####", "(##) #####-####")
 */
public class MaskWatcher implements TextWatcher {

    private final EditText campo;
    private final String[] mascaras;
    private boolean atualizando = false;

    public MaskWatcher(EditText campo, String... mascaras) {
        this.campo = campo;
        this.mascaras = mascaras;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (atualizando) return;
        atualizando = true;

        String digitos = AdminUi.soDigitos(s.toString());
        String mascara = mascaras[mascaras.length - 1];
        for (String m : mascaras) {
            if (contarDigitos(m) >= digitos.length()) {
                mascara = m;
                break;
            }
        }

        StringBuilder saida = new StringBuilder();
        int i = 0;
        for (char c : mascara.toCharArray()) {
            if (i >= digitos.length()) break;
            if (c == '#') {
                saida.append(digitos.charAt(i++));
            } else {
                saida.append(c);
            }
        }

        campo.setText(saida.toString());
        campo.setSelection(saida.length());
        atualizando = false;
    }

    private static int contarDigitos(String mascara) {
        int n = 0;
        for (char c : mascara.toCharArray()) if (c == '#') n++;
        return n;
    }
}

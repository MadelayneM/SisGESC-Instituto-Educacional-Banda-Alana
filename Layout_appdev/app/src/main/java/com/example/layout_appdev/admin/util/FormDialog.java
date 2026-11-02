package com.example.layout_appdev.admin.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.layout_appdev.R;

/** Diálogo simples com 1 ou 2 campos, usado nos cadastros rápidos. */
public final class FormDialog {

    public interface Resultado {
        void aoConfirmar(String campo1, String campo2);
    }

    private FormDialog() {}

    /**
     * @param hint2 use null para mostrar só um campo
     */
    public static void mostrar(Context ctx, String titulo, String hint1, String hint2,
                               String textoBotao, Resultado resultado) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_admin_form, null);
        EditText et1 = view.findViewById(R.id.etCampo1);
        EditText et2 = view.findViewById(R.id.etCampo2);
        et1.setHint(hint1);
        if (hint2 == null) {
            et2.setVisibility(View.GONE);
        } else {
            et2.setHint(hint2);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(ctx)
                .setTitle(titulo)
                .setView(view)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(textoBotao, null)
                .create();

        // Sobrescreve o clique para o diálogo só fechar quando os campos estiverem válidos
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String v1 = et1.getText().toString().trim();
            String v2 = et2.getText().toString().trim();
            boolean valido = true;
            if (v1.isEmpty()) {
                et1.setError("Campo obrigatório");
                valido = false;
            }
            if (hint2 != null && v2.isEmpty()) {
                et2.setError("Campo obrigatório");
                valido = false;
            }
            if (!valido) return;
            resultado.aoConfirmar(v1, v2);
            dialog.dismiss();
        }));
        dialog.show();
    }
}

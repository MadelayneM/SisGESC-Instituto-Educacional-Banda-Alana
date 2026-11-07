package com.example.sisgesc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class minha_turma extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_minha_turma);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAluno.configurar(this, R.id.navTurma);
    }

    // Monta a lista de colegas na tela. Chamar com turma.getColegas() dentro do onSucesso de buscarTurma.
    void mostrarColegas(List<String> nomes) {
        LinearLayout lista = findViewById(R.id.listaColegas);
        lista.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (String nome : nomes) {
            View linha = inflater.inflate(R.layout.item_colega, lista, false);
            TextView txtNome = linha.findViewById(R.id.txtNomeColega);
            txtNome.setText(nome);
            lista.addView(linha);
        }

        findViewById(R.id.txtSemColegas).setVisibility(nomes.isEmpty() ? View.VISIBLE : View.GONE);
    }
}

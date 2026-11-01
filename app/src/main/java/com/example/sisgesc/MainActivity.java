package com.example.sisgesc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FrameLayout container;

    private TextView navInicio;
    private TextView navAlunos;
    private TextView navTurmas;
    private TextView navFrequencia;
    private TextView navPerfil;

    private final int corSelecionada = 0xFFB99B00;
    private final int corNormal = 0xFFD6BD43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.fragmentContainer);

        navInicio = findViewById(R.id.navInicio);
        navAlunos = findViewById(R.id.navAlunos);
        navTurmas = findViewById(R.id.navTurmas);
        navFrequencia = findViewById(R.id.navFrequencia);
        navPerfil = findViewById(R.id.navPerfil);

        navInicio.setOnClickListener(v ->
                abrirTela(R.layout.activity_login_professor, navInicio));

        navAlunos.setOnClickListener(v ->
                abrirTela(R.layout.activity_alunos_professor, navAlunos));

        navTurmas.setOnClickListener(v ->
                abrirTela(R.layout.activity_turmas_professor, navTurmas));

        navFrequencia.setOnClickListener(v ->
                abrirTela(R.layout.activity_frequencia_professor, navFrequencia));

        navPerfil.setOnClickListener(v ->
                abrirTela(R.layout.activity_perfil_professor, navPerfil));

        abrirTela(R.layout.activity_main, navInicio);
    }

    private void abrirTela(int layout, TextView itemSelecionado) {

        container.removeAllViews();

        android.view.View tela = LayoutInflater.from(this)
                .inflate(layout, container, false);

        container.addView(tela);

        TextView[] itens = {
                navInicio,
                navAlunos,
                navTurmas,
                navFrequencia,
                navPerfil
        };

        for (TextView item : itens) {
            item.setTextColor(corNormal);
        }

        itemSelecionado.setTextColor(corSelecionada);
    }
}

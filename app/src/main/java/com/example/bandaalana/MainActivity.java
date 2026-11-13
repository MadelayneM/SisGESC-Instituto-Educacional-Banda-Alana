package com.example.bandaalana;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        // Referenciando os 3 MaterialCardView do seu XML
        MaterialCardView cardAluno = findViewById(R.id.cardAluno);
        MaterialCardView cardProfessor = findViewById(R.id.cardProfessor);
        MaterialCardView cardGestor = findViewById(R.id.cardGestor);

        // 1. Card Aluno / Responsável
        cardAluno.setOnClickListener(v -> {
            Toast.makeText(this, "Abrindo Perfil Aluno...", Toast.LENGTH_SHORT).show();
            // Quando importar o módulo do Aluno, descomente as linhas abaixo:
            //Intent intent = new Intent(MainActivity.this, com.example.bandaalana.MainActivity.class);
            //startActivity(intent);
        });

        // 2. Card Professor / Mentor
        cardProfessor.setOnClickListener(v -> {
            Toast.makeText(this, "Abrindo Perfil Professor...", Toast.LENGTH_SHORT).show();
            // Quando importar o módulo do Professor, descomente as linhas abaixo:
            // Intent intent = new Intent(MainActivity.this, TelaInicialProfessorActivity.class);
            // startActivity(intent);
        });

        // 3. Card Gestor / Administrador (Módulo :feature:perfila que você já importou)
        cardGestor.setOnClickListener(v -> {
            //Intent intent = new Intent(MainActivity.this, com.example.bandaalana.feature.MainActivity.class);
            //tartActivity(intent);
        });



    }
}
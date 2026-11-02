package com.example.layout_appdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.admin.AdminLoginActivity;
import com.example.layout_appdev.admin.util.AdminUi;

/**
 * Tela inicial: escolha do perfil de acesso.
 * Os perfis Aluno e Professor serão ligados pelos outros grupos.
 */
public class SelecaoPerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_perfil);
        AdminUi.aplicarInsets(this);

        findViewById(R.id.cardGestor).setOnClickListener(v ->
                startActivity(new Intent(this, AdminLoginActivity.class)));

        // TODO (outros grupos): trocar pelos logins de Aluno e Professor
        findViewById(R.id.cardAluno).setOnClickListener(v ->
                Toast.makeText(this, "Acesso de aluno/responsável em desenvolvimento", Toast.LENGTH_SHORT).show());
        findViewById(R.id.cardProfessor).setOnClickListener(v ->
                Toast.makeText(this, "Acesso de professor/monitor em desenvolvimento", Toast.LENGTH_SHORT).show());
    }
}

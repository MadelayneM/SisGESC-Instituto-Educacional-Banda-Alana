package com.example.sisgesc_adm;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class AdminEditarPerfilActivity extends AppCompatActivity {


    private View btnVoltar;
    private MaterialButton btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_editar_perfil);

        // Encontre o botão de voltar pelo ID (confirme se o ID no view_form_header.xml é btnBack ou similar)
        View btnBack = findViewById(R.id.btnVoltar);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Finaliza a activity atual e retorna para a anterior (AdminTurmasActivity)
                finish();
            });
        }


        //funcao botao salvar

        // 1. Inicializar o botão Salvar
        MaterialButton btnSalvar = findViewById(R.id.btnSalvar); // Confirme se o ID no XML é btnSalvar

        // 2. Configurar a ação de clique do botão
        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                // Aqui você pode adicionar a lógica para capturar os textos dos EditTexts e salvar no banco/preferências

                // Exemplo de aviso de sucesso
                Toast.makeText(this, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show();

                // Finaliza a activity atual e retorna para a tela de Perfil
                finish();
            });
        }

        //funcao alterar senha do adm




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminEdtPerfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
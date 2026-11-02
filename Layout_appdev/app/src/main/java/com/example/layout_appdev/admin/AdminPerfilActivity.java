package com.example.layout_appdev.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.R;
import com.example.layout_appdev.SelecaoPerfilActivity;
import com.example.layout_appdev.admin.model.PerfilAdmin;
import com.example.layout_appdev.admin.util.AdminNav;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MockData;

public class AdminPerfilActivity extends AppCompatActivity {

    private ImageView ivFotoPerfil;

    // Escolher foto da galeria
    private final ActivityResultLauncher<String> escolherFoto = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    // TODO (back-end): enviar a foto para o servidor
                    MockData.perfil().fotoUri = uri.toString();
                    ivFotoPerfil.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_perfil);
        AdminUi.aplicarInsets(this);
        AdminNav.configurar(this, R.id.nav_perfil);

        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);

        findViewById(R.id.flFotoPerfil).setOnClickListener(v -> escolherFoto.launch("image/*"));
        findViewById(R.id.btnEditarDados).setOnClickListener(v ->
                startActivity(new Intent(this, AdminEditarPerfilActivity.class)));
        findViewById(R.id.btnAlterarSenha).setOnClickListener(v ->
                startActivity(new Intent(this, AdminAlterarSenhaActivity.class)));

        findViewById(R.id.btnSair).setOnClickListener(v -> {
            // TODO (back-end): encerrar a sessão do usuário
            Intent intent = new Intent(this, SelecaoPerfilActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega os dados (podem ter sido editados na tela "Editar dados")
        PerfilAdmin p = MockData.perfil();
        ((TextView) findViewById(R.id.tvPerfilNome)).setText(p.nome);
        ((TextView) findViewById(R.id.tvInfoEmail)).setText(p.email);
        ((TextView) findViewById(R.id.tvInfoTelefone)).setText(p.telefone);
        ((TextView) findViewById(R.id.tvInfoTipo)).setText(p.tipo);
        ((TextView) findViewById(R.id.tvInfoInstituto)).setText(p.instituto);
        AdminUi.carregarFoto(ivFotoPerfil, p.fotoUri, R.drawable.ic_avatar_placeholder);
    }
}

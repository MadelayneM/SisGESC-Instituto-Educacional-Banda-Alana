package com.example.bandaalana.administrador.DTOs;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NovoAviso {

    @SerializedName("pk_id_aviso")
    private final int idAviso;

    @SerializedName("fk_cpf_coordenador")
    private final String cpfCoordenador;

    @SerializedName("titulo_aviso")
    private final String titulo;

    @SerializedName("descricao_aviso")
    private final String descricao;

    @SerializedName("data_envio")
    private final String dataEnvio;

    public NovoAviso(String cpfCoordenador, String titulo, String descricao) {
        // Gera um ID int4 único baseado nos segundos atuais
        this.idAviso = (int) (System.currentTimeMillis() / 1000);
        this.cpfCoordenador = cpfCoordenador;
        this.titulo = titulo;
        this.descricao = descricao;
        // Formato timestamp padrão do PostgreSQL/Supabase
        this.dataEnvio = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date());
    }
}

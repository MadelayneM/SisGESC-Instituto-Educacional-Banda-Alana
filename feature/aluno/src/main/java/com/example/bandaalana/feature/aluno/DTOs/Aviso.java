package com.example.bandaalana.feature.aluno.DTOs;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

// Um aviso da tabela tb_aviso. A data vem no formato do banco, ex.: 2026-09-25T18:00:00
public class Aviso {
    @SerializedName("pk_id_aviso") private Integer id;
    @SerializedName("titulo_aviso") private String titulo;
    @SerializedName("descricao_aviso") private String descricao;
    @SerializedName("data_envio") private String dataEnvio;

    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getDataEnvio() { return dataEnvio; }
    // Texto para a tela: "Hoje", "Ontem" ou "25/09"
    public String getDataFormatada() { return FormatoData.relativa(dataEnvio, new Date()); }
}

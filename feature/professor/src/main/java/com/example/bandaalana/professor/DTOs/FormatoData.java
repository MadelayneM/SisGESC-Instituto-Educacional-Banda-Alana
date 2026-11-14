package com.example.bandaalana.professor.DTOs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Transforma as datas do banco (2026-08-25 ou 2026-08-25T14:00:00) nos textos que as telas mostram.
public final class FormatoData {

    private static final Locale PORTUGUES = new Locale("pt", "BR");

    private FormatoData() {
    }

    // 2026-08-25 vira "25/08 — Terça"
    public static String diaComSemana(String dataBanco) {
        Date dia = lerDia(dataBanco);
        if (dia == null) return dataBanco;
        String semana = new SimpleDateFormat("EEEE", PORTUGUES).format(dia).replace("-feira", "");
        semana = semana.substring(0, 1).toUpperCase(PORTUGUES) + semana.substring(1);
        return new SimpleDateFormat("dd/MM", PORTUGUES).format(dia) + " — " + semana;
    }

    // Vira "Hoje", "Ontem" ou, nos outros dias, "25/08"
    public static String relativa(String dataBanco, Date hoje) {
        Date dia = lerDia(dataBanco);
        if (dia == null) return dataBanco;

        SimpleDateFormat comparar = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar ontem = Calendar.getInstance();
        ontem.setTime(hoje);
        ontem.add(Calendar.DAY_OF_MONTH, -1);

        String texto = comparar.format(dia);
        if (texto.equals(comparar.format(hoje))) return "Hoje";
        if (texto.equals(comparar.format(ontem.getTime()))) return "Ontem";
        return new SimpleDateFormat("dd/MM", PORTUGUES).format(dia);
    }

    private static Date lerDia(String dataBanco) {
        if (dataBanco == null || dataBanco.length() < 10) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dataBanco.substring(0, 10));
        } catch (ParseException e) {
            return null;
        }
    }
}

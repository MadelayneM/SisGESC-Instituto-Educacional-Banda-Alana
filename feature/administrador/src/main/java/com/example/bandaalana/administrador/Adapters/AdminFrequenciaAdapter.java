package com.example.bandaalana.administrador.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandaalana.administrador.DTOs.AdminTurma;
import com.example.bandaalana.administrador.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

// Lista da tela "Consultar frequência" (rvFrequencia).
// Cada turma usa o layout activity_item_admin_frequencia_turma.
public class AdminFrequenciaAdapter extends RecyclerView.Adapter<AdminFrequenciaAdapter.FrequenciaViewHolder> {

    // Faixas de cor da frequência: verde a partir de 75%, amarelo a partir de 50%, vermelho abaixo
    private static final int FREQUENCIA_BOA = 75;
    private static final int FREQUENCIA_MEDIA = 50;

    private final List<AdminTurma> turmas = new ArrayList<>();

    public void atualizar(List<AdminTurma> novasTurmas) {
        turmas.clear();
        if (novasTurmas != null) turmas.addAll(novasTurmas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return turmas.size();
    }

    @NonNull
    @Override
    public FrequenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_admin_frequencia_turma, parent, false);
        return new FrequenciaViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull FrequenciaViewHolder holder, int position) {
        AdminTurma turma = turmas.get(position);
        Context contexto = holder.itemView.getContext();

        holder.tvNomeTurma.setText(turma.getNomeTurma());
        String curso = turma.getNomeCurso() == null ? "" : turma.getNomeCurso() + " · ";
        holder.tvCursoProfessor.setText(curso + turma.getProfessoresTexto());

        Integer frequencia = turma.getFrequencia();
        boolean temChamada = frequencia != null;
        int cor = ContextCompat.getColor(contexto, corDaFrequencia(frequencia));

        holder.tvPercentual.setText(temChamada ? frequencia + "%" : "--");
        holder.tvPercentual.setTextColor(temChamada ? cor : ContextCompat.getColor(contexto, R.color.alana_text_secondary));
        holder.pbFrequencia.setProgress(temChamada ? frequencia : 0);
        holder.pbFrequencia.setIndicatorColor(cor);
        holder.tvSemChamada.setVisibility(temChamada ? View.GONE : View.VISIBLE);

        holder.tvPresencas.setText("🙋  " + plural(turma.getPresencas(), "presença", "presenças"));
        holder.tvFaltas.setText("❌  " + plural(turma.getFaltas(), "falta", "faltas"));
        holder.tvAlunos.setText("👥  " + plural(turma.getAlunosAtivos(), "aluno", "alunos"));
    }

    // Também usado pelo card "Frequência" da visão geral
    @ColorRes
    public static int corDaFrequencia(Integer frequencia) {
        if (frequencia == null || frequencia >= FREQUENCIA_BOA) return R.color.alana_green;
        if (frequencia >= FREQUENCIA_MEDIA) return R.color.alana_yellow;
        return R.color.alana_red;
    }

    private static String plural(int quantidade, String singular, String plural) {
        return quantidade + " " + (quantidade == 1 ? singular : plural);
    }

    static class FrequenciaViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNomeTurma;
        final TextView tvPercentual;
        final TextView tvCursoProfessor;
        final LinearProgressIndicator pbFrequencia;
        final TextView tvSemChamada;
        final TextView tvPresencas;
        final TextView tvFaltas;
        final TextView tvAlunos;

        FrequenciaViewHolder(@NonNull View card) {
            super(card);
            tvNomeTurma = card.findViewById(R.id.tvNomeTurma);
            tvPercentual = card.findViewById(R.id.tvPercentual);
            tvCursoProfessor = card.findViewById(R.id.tvCursoProfessor);
            pbFrequencia = card.findViewById(R.id.pbFrequencia);
            tvSemChamada = card.findViewById(R.id.tvSemChamada);
            tvPresencas = card.findViewById(R.id.tvPresencas);
            tvFaltas = card.findViewById(R.id.tvFaltas);
            tvAlunos = card.findViewById(R.id.tvAlunos);
        }
    }
}

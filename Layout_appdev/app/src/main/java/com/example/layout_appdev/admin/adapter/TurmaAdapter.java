package com.example.layout_appdev.admin.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.model.Turma;

import java.util.List;

public class TurmaAdapter extends RecyclerView.Adapter<TurmaAdapter.VH> {

    private final List<Turma> turmas;

    public TurmaAdapter(List<Turma> turmas) {
        this.turmas = turmas;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void atualizar() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_turma, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Turma t = turmas.get(position);
        h.tvNomeTurma.setText(t.getNome());
        h.tvQtdAlunos.setText(String.valueOf(t.getQtdAlunos()));
        h.tvProfessor.setText("🧑‍🏫  " + t.getProfessor());
        h.tvHorario.setText("⏰  " + t.getHorario());
        h.tvDia.setText("📅  " + t.getDiaSemana());
    }

    @Override
    public int getItemCount() {
        return turmas.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvNomeTurma, tvQtdAlunos, tvProfessor, tvHorario, tvDia;

        VH(@NonNull View itemView) {
            super(itemView);
            tvNomeTurma = itemView.findViewById(R.id.tvNomeTurma);
            tvQtdAlunos = itemView.findViewById(R.id.tvQtdAlunos);
            tvProfessor = itemView.findViewById(R.id.tvProfessor);
            tvHorario = itemView.findViewById(R.id.tvHorario);
            tvDia = itemView.findViewById(R.id.tvDia);
        }
    }
}

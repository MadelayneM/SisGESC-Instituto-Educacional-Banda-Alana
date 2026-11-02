package com.example.layout_appdev.admin.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.model.Aluno;
import com.example.layout_appdev.admin.util.AdminUi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.VH> {

    private final List<Aluno> fonte;
    private final List<Aluno> exibidos = new ArrayList<>();
    private String consulta = "";

    public AlunoAdapter(List<Aluno> fonte) {
        this.fonte = fonte;
        atualizar();
    }

    /** Filtra por nome ou turma. Texto vazio mostra todos. */
    public void filtrar(String consulta) {
        this.consulta = consulta == null ? "" : consulta.trim().toLowerCase(Locale.getDefault());
        atualizar();
    }

    /** Reaplica o filtro atual (chame depois de alterar a lista de origem). */
    @SuppressLint("NotifyDataSetChanged")
    public void atualizar() {
        exibidos.clear();
        for (Aluno a : fonte) {
            if (!consulta.isEmpty()) {
                String nome = a.getNome().toLowerCase(Locale.getDefault());
                String turma = a.getTurma().toLowerCase(Locale.getDefault());
                if (!nome.contains(consulta) && !turma.contains(consulta)) continue;
            }
            exibidos.add(a);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_aluno_recente, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        vincular(h.itemView, exibidos.get(position));
    }

    /** Preenche uma linha (item_admin_aluno_recente) com os dados do aluno. Também usado no Dashboard. */
    public static void vincular(View itemView, Aluno a) {
        ImageView ivAvatar = itemView.findViewById(R.id.ivAvatar);
        TextView tvNome = itemView.findViewById(R.id.tvNome);
        TextView tvTurma = itemView.findViewById(R.id.tvTurma);
        TextView tvStatus = itemView.findViewById(R.id.tvStatus);

        tvNome.setText(a.getNome());
        tvTurma.setText(a.getTurma());
        AdminUi.carregarFoto(ivAvatar, a.getFotoUri(), R.drawable.ic_avatar_placeholder);

        switch (a.getStatus()) {
            case ATIVO:
                tvStatus.setText("Ativo");
                AdminUi.pintarChip(tvStatus, R.color.alana_status_ativo_bg, R.color.alana_status_ativo_text);
                break;
            case PENDENTE:
                tvStatus.setText("Pendente");
                AdminUi.pintarChip(tvStatus, R.color.alana_status_pendente_bg, R.color.alana_status_pendente_text);
                break;
            default:
                tvStatus.setText("Inativo");
                AdminUi.pintarChip(tvStatus, R.color.alana_status_inativo_bg, R.color.alana_status_inativo_text);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return exibidos.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        VH(@NonNull View itemView) {
            super(itemView);
        }
    }
}

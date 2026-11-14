package com.example.bandaalana.administrador.Adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandaalana.administrador.DTOs.AdminAluno;
import com.example.bandaalana.administrador.R;

import java.util.ArrayList;
import java.util.List;

// Lista da tela Alunos (rvAlunos). Cada aluno usa o layout activity_item_admin_aluno_recente.
// A foto (ivAvatar) fica com o avatar padrão: o projeto não tem biblioteca de imagens ainda.
public class AdminAlunoAdapter extends RecyclerView.Adapter<AdminAlunoAdapter.AlunoViewHolder> {

    public interface AoClicar {
        void noAluno(AdminAluno aluno);
    }

    private final List<AdminAluno> alunos = new ArrayList<>();
    private final AoClicar aoClicar;

    public AdminAlunoAdapter(AoClicar aoClicar) {
        this.aoClicar = aoClicar;
    }

    public void atualizar(List<AdminAluno> novosAlunos) {
        alunos.clear();
        if (novosAlunos != null) alunos.addAll(novosAlunos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return alunos.size();
    }

    @NonNull
    @Override
    public AlunoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View linha = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_admin_aluno_recente, parent, false);
        return new AlunoViewHolder(linha);
    }

    @Override
    public void onBindViewHolder(@NonNull AlunoViewHolder holder, int position) {
        AdminAluno aluno = alunos.get(position);
        preencher(holder.itemView, aluno);
        holder.itemView.setOnClickListener(v -> aoClicar.noAluno(aluno));
    }

    // Também usado pela lista "Alunos recentes" do Dashboard, que não é RecyclerView
    public static void preencher(View linha, AdminAluno aluno) {
        ((TextView) linha.findViewById(R.id.tvNome)).setText(aluno.getNome());
        ((TextView) linha.findViewById(R.id.tvTurma)).setText(
                aluno.getNomeTurma() == null ? "Sem matrícula" : aluno.getNomeTurma());

        TextView tvStatus = linha.findViewById(R.id.tvStatus);
        String status = aluno.getStatus();
        tvStatus.setVisibility(status == null ? View.GONE : View.VISIBLE);
        tvStatus.setText(status);
        boolean ativo = "Ativo".equals(status);
        tvStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(linha.getContext(),
                ativo ? R.color.alana_status_ativo_bg : R.color.alana_status_inativo_bg)));
        tvStatus.setTextColor(ContextCompat.getColor(linha.getContext(),
                ativo ? R.color.alana_status_ativo_text : R.color.alana_status_inativo_text));
    }

    static class AlunoViewHolder extends RecyclerView.ViewHolder {
        AlunoViewHolder(@NonNull View linha) {
            super(linha);
        }
    }
}

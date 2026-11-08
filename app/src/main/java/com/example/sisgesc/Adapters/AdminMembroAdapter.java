package com.example.sisgesc.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sisgesc.DTOs.AdminMembro;
import com.example.sisgesc.R;

import java.util.ArrayList;
import java.util.List;

// Lista da tela Equipe (rvEquipe). Cada membro usa o layout activity_item_admin_membro.
public class AdminMembroAdapter extends RecyclerView.Adapter<AdminMembroAdapter.MembroViewHolder> {

    public interface AoClicar {
        void noMembro(AdminMembro membro);
    }

    private final List<AdminMembro> membros = new ArrayList<>();
    private final AoClicar aoClicar;

    public AdminMembroAdapter(AoClicar aoClicar) {
        this.aoClicar = aoClicar;
    }

    public void atualizar(List<AdminMembro> novosMembros) {
        membros.clear();
        if (novosMembros != null) membros.addAll(novosMembros);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return membros.size();
    }

    @NonNull
    @Override
    public MembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View linha = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_admin_membro, parent, false);
        return new MembroViewHolder(linha);
    }

    @Override
    public void onBindViewHolder(@NonNull MembroViewHolder holder, int position) {
        AdminMembro membro = membros.get(position);
        holder.tvIniciais.setText(membro.getIniciais());
        holder.tvNome.setText(membro.getNome());
        holder.tvDetalhe.setText(membro.getTurmasTexto());
        holder.tvCargo.setText(membro.getCargo());
        holder.itemView.setOnClickListener(v -> aoClicar.noMembro(membro));
    }

    static class MembroViewHolder extends RecyclerView.ViewHolder {
        final TextView tvIniciais;
        final TextView tvNome;
        final TextView tvDetalhe;
        final TextView tvCargo;

        MembroViewHolder(@NonNull View linha) {
            super(linha);
            tvIniciais = linha.findViewById(R.id.tvIniciais);
            tvNome = linha.findViewById(R.id.tvNome);
            tvDetalhe = linha.findViewById(R.id.tvDetalhe);
            tvCargo = linha.findViewById(R.id.tvCargo);
        }
    }
}

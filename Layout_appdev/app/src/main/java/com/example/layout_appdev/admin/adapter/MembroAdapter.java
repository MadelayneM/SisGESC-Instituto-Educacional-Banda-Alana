package com.example.layout_appdev.admin.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.model.Membro;
import com.example.layout_appdev.admin.util.AdminUi;

import java.util.List;

public class MembroAdapter extends RecyclerView.Adapter<MembroAdapter.VH> {

    private final List<Membro> membros;

    public MembroAdapter(List<Membro> membros) {
        this.membros = membros;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void atualizar() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_membro, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Membro m = membros.get(position);
        h.tvIniciais.setText(AdminUi.iniciais(m.getNome()));
        h.tvNome.setText(m.getNome());
        h.tvDetalhe.setText(m.getTurmas());
        h.tvCargo.setText(m.getCargo());
    }

    @Override
    public int getItemCount() {
        return membros.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvIniciais, tvNome, tvDetalhe, tvCargo;

        VH(@NonNull View itemView) {
            super(itemView);
            tvIniciais = itemView.findViewById(R.id.tvIniciais);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvDetalhe = itemView.findViewById(R.id.tvDetalhe);
            tvCargo = itemView.findViewById(R.id.tvCargo);
        }
    }
}

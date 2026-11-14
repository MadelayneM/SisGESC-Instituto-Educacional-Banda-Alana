package com.example.bandaalana.administrador.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandaalana.administrador.DTOs.Aviso;
import com.example.bandaalana.administrador.R;

import java.util.ArrayList;
import java.util.List;

public class AdminAvisoAdapter extends RecyclerView.Adapter<AdminAvisoAdapter.AvisoViewHolder> {

    public interface AoExcluir {
        void onExcluir(Aviso aviso);
    }

    public interface AoEditar {
        void onEditar(Aviso aviso);
    }

    private final List<Aviso> lista = new ArrayList<>();
    private final AoExcluir aoExcluir;
    private final AoEditar aoEditar;

    public AdminAvisoAdapter(AoExcluir aoExcluir, AoEditar aoEditar) {
        this.aoExcluir = aoExcluir;
        this.aoEditar = aoEditar;
    }

    public void atualizar(List<Aviso> novos) {
        lista.clear();
        if (novos != null) lista.addAll(novos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AvisoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_aviso, parent, false);
        return new AvisoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvisoViewHolder holder, int position) {
        Aviso aviso = lista.get(position);
        holder.tvTitulo.setText(aviso.getTitulo());
        holder.tvDescricao.setText(aviso.getDescricao());

        String dataRaw = aviso.getDataEnvio();
        if (dataRaw != null && dataRaw.length() >= 10) {
            holder.tvData.setText(dataRaw.substring(8, 10) + "/" + dataRaw.substring(5, 7) + "/" + dataRaw.substring(0, 4));
        } else {
            holder.tvData.setText("");
        }

        // Clique na lixeira exclui; clique no restante do card edita
        holder.btnExcluir.setOnClickListener(v -> aoExcluir.onExcluir(aviso));
        holder.itemView.setOnClickListener(v -> aoEditar.onEditar(aviso));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class AvisoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescricao, tvData;
        android.widget.ImageButton btnExcluir;

        AvisoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloAvisoItem);
            tvDescricao = itemView.findViewById(R.id.tvDescricaoAvisoItem);
            tvData = itemView.findViewById(R.id.tvDataAvisoItem);
            btnExcluir = itemView.findViewById(R.id.btnExcluirAvisoItem);
        }
    }
}

package com.example.sisgesc.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sisgesc.DTOs.AdminTurma;
import com.example.sisgesc.R;

import java.util.ArrayList;
import java.util.List;

// Liga a lista de AdminTurma ao RecyclerView rvTurmas da tela Turmas.
// Cada turma vira um card com o layout activity_item_admin_turma.
//
// Como o RecyclerView trabalha:
//  1. Pergunta quantos itens existem        -> getItemCount()
//  2. Cria só os cards que cabem na tela    -> onCreateViewHolder()
//  3. Coloca os dados de uma turma no card  -> onBindViewHolder()
// Quando o usuário rola, os cards que saem da tela são reaproveitados
// para as turmas que entram (por isso se chama "Recycler").
public class AdminTurmaAdapter extends RecyclerView.Adapter<AdminTurmaAdapter.TurmaViewHolder> {

    public interface AoClicar {
        void naTurma(AdminTurma turma);
    }

    private final List<AdminTurma> turmas = new ArrayList<>();
    private final AoClicar aoClicar;

    public AdminTurmaAdapter(AoClicar aoClicar) {
        this.aoClicar = aoClicar;
    }

    // Chamado pela tela quando o Repository devolve as turmas
    public void atualizar(List<AdminTurma> novasTurmas) {
        turmas.clear();
        if (novasTurmas != null) turmas.addAll(novasTurmas);
        // Avisa o RecyclerView que a lista mudou e ele precisa redesenhar os cards
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return turmas.size();
    }

    // Cria um card vazio a partir do XML (ainda sem dados)
    @NonNull
    @Override
    public TurmaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_admin_turma, parent, false);
        return new TurmaViewHolder(card);
    }

    // Preenche o card com os dados da turma da posição "position"
    @Override
    public void onBindViewHolder(@NonNull TurmaViewHolder holder, int position) {
        AdminTurma turma = turmas.get(position);

        holder.tvNomeTurma.setText(turma.getNomeTurma());
        holder.tvQtdAlunos.setText(String.valueOf(turma.getAlunosAtivos()));
        holder.tvProfessor.setText("🧑‍🏫  " + turma.getProfessoresTexto());
        holder.tvHorario.setText("⏰  " + textoHorario(turma));
        holder.tvDia.setText("📅  " + textoOuTraco(turma.getDiaSemana()));
        holder.itemView.setOnClickListener(v -> aoClicar.naTurma(turma));
    }

    // Ex.: "14:00 – 15:30", ou só "14:00" se não houver horário de término
    private static String textoHorario(AdminTurma turma) {
        String inicio = horario(turma.getHorarioInicio());
        String fim = horario(turma.getHorarioFim());
        if (vazio(inicio)) return "—";
        if (vazio(fim)) return inicio;
        return inicio + " – " + fim;
    }

    // O banco pode devolver "14:00:00"; a tela mostra "14:00"
    private static String horario(String texto) {
        return texto != null && texto.length() > 5 ? texto.substring(0, 5) : texto;
    }

    private static String textoOuTraco(String texto) {
        return vazio(texto) ? "—" : texto;
    }

    private static boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // Guarda as views de um card. Assim o findViewById roda uma vez por card
    // criado, e não toda vez que o card é reaproveitado para outra turma.
    static class TurmaViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNomeTurma;
        final TextView tvQtdAlunos;
        final TextView tvProfessor;
        final TextView tvHorario;
        final TextView tvDia;

        TurmaViewHolder(@NonNull View card) {
            super(card);
            tvNomeTurma = card.findViewById(R.id.tvNomeTurma);
            tvQtdAlunos = card.findViewById(R.id.tvQtdAlunos);
            tvProfessor = card.findViewById(R.id.tvProfessor);
            tvHorario = card.findViewById(R.id.tvHorario);
            tvDia = card.findViewById(R.id.tvDia);
        }
    }
}

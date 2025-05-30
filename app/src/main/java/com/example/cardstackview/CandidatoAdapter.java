package com.example.cardstackview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CandidatoAdapter extends RecyclerView.Adapter<CandidatoAdapter.CandidatoViewHolder> {
    private List<Usuario> candidatosList;

    public CandidatoAdapter(List<Usuario> candidatosList) {
        this.candidatosList = candidatosList;
    }

    @NonNull
    @Override
    public CandidatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_candidato, parent, false);
        return new CandidatoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatoViewHolder holder, int position) {
        Usuario candidato = candidatosList.get(position);
        holder.tvNome.setText(candidato.getNome());
        holder.tvEmail.setText(candidato.getEmail());
        holder.tvCargo.setText(candidato.getCargo());

        // Melhoria de acessibilidade
        holder.itemView.setContentDescription(
                "Candidato " + candidato.getNome() +
                        ", E-mail: " + candidato.getEmail() +
                        ", Cargo: " + candidato.getCargo()
        );
    }

    @Override
    public int getItemCount() {
        return candidatosList.size();
    }

    static class CandidatoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEmail, tvCargo;

        public CandidatoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeCandidato);
            tvEmail = itemView.findViewById(R.id.tvEmailCandidato);
            tvCargo = itemView.findViewById(R.id.tvCargoCandidato);
        }
    }
}
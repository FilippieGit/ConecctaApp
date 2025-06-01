package com.example.cardstackview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CandidatoAdapter extends RecyclerView.Adapter<CandidatoAdapter.CandidatoViewHolder> {
    private List<Usuario> candidatosList;
    private OnCandidatoClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnCandidatoClickListener {
        void onCandidatoClick(Usuario usuario);
    }

    public CandidatoAdapter(List<Usuario> candidatosList, OnCandidatoClickListener listener) {
        this.candidatosList = candidatosList;
        this.listener = listener;
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

        // Defina os valores básicos
        holder.tvNome.setText(candidato.getNome());
        holder.tvEmail.setText(candidato.getEmail());
        holder.tvCargo.setText(candidato.getCargo());
        holder.tvStatus.setText(candidato.getStatus());

        // Formate e exiba a data
        if (candidato.getDataCandidatura() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvData.setText(dateFormat.format(candidato.getDataCandidatura()));
        }

        // Defina a cor do status
        int statusColor = getStatusColor(holder, candidato.getStatus());
        holder.tvStatus.setBackgroundColor(statusColor);

        // Configure o listener de clique
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCandidatoClick(candidato);
            }
        });
    }

    private int getStatusColor(CandidatoViewHolder holder, String status) {
        switch (status.toLowerCase()) {
            case "aprovada":
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.verde);
            case "rejeitada":
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.vermelho);
            case "visualizada":
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.azul);
            default:
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.cinza);
        }
    }

    @Override
    public int getItemCount() {
        return candidatosList.size();
    }

    static class CandidatoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEmail, tvCargo, tvStatus, tvData;

        public CandidatoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeCandidato);
            tvEmail = itemView.findViewById(R.id.tvEmailCandidato);
            tvCargo = itemView.findViewById(R.id.tvCargoCandidato);
            tvStatus = itemView.findViewById(R.id.tvStatusCandidato);
            tvData = itemView.findViewById(R.id.tvDataCandidato);
        }
    }
}

package com.example.cardstackview;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private OnStatusChangeListener statusChangeListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnCandidatoClickListener {
        void onCandidatoClick(Usuario usuario);
    }

    public interface OnStatusChangeListener {
        void onStatusChanged(int position, String novoStatus, String motivo);
    }

    public CandidatoAdapter(List<Usuario> candidatosList, OnCandidatoClickListener listener) {
        this.candidatosList = candidatosList;
        this.listener = listener;
    }
    public void setOnStatusChangeListener(OnStatusChangeListener listener) {
        this.statusChangeListener = listener;
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
        holder.tvStatus.setText(candidato.getStatus());

        if (candidato.getDataCandidatura() != null) {
            holder.tvData.setText(dateFormat.format(candidato.getDataCandidatura()));
        }

        int statusColor = getStatusColor(holder, candidato.getStatus());
        holder.tvStatus.setBackgroundColor(statusColor);

        // Configuração dos botões
        holder.btnAceitar.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && statusChangeListener != null) {
                statusChangeListener.onStatusChanged(adapterPosition, "aprovada", null);
            }
        });

        holder.btnRecusar.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                // Mostrar diálogo para inserir motivo
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Motivo da recusa");

                final EditText input = new EditText(holder.itemView.getContext());
                builder.setView(input);

                builder.setPositiveButton("Confirmar", (dialog, which) -> {
                    String motivo = input.getText().toString();
                    if (statusChangeListener != null) {
                        statusChangeListener.onStatusChanged(adapterPosition, "rejeitada", motivo);
                    }
                });

                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });

        // Atualiza a visibilidade dos botões com base no status atual
        updateButtonVisibility(holder, candidato.getStatus());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCandidatoClick(candidato);
            }
        });
    }

    private void updateButtonVisibility(CandidatoViewHolder holder, String status) {
        if (status.equalsIgnoreCase("aprovada")) {
            holder.btnAceitar.setVisibility(View.GONE);
            holder.btnRecusar.setVisibility(View.VISIBLE);
            holder.btnRecusar.setText("Cancelar Aceite");
        } else if (status.equalsIgnoreCase("rejeitada")) {
            holder.btnAceitar.setVisibility(View.VISIBLE);
            holder.btnRecusar.setVisibility(View.GONE);
            holder.btnAceitar.setText("Reverter Recusa");
        } else {
            holder.btnAceitar.setVisibility(View.VISIBLE);
            holder.btnRecusar.setVisibility(View.VISIBLE);
            holder.btnAceitar.setText("Aceitar");
            holder.btnRecusar.setText("Recusar");
        }
    }

    static class CandidatoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEmail, tvCargo, tvStatus, tvData;
        Button btnAceitar, btnRecusar;

        public CandidatoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeCandidato);
            tvEmail = itemView.findViewById(R.id.tvEmailCandidato);
            tvCargo = itemView.findViewById(R.id.tvCargoCandidato);
            tvStatus = itemView.findViewById(R.id.tvStatusCandidato);
            tvData = itemView.findViewById(R.id.tvDataCandidato);
            btnAceitar = itemView.findViewById(R.id.btnAceitar);
            btnRecusar = itemView.findViewById(R.id.btnRecusar);
        }
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

}

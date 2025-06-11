package com.example.cardstackview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdaptadorBancoTalentos extends RecyclerView.Adapter<AdaptadorBancoTalentos.CandidatoViewHolder> {

    private Context context;
    private List<CandidatoRecusado> candidatosList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public AdaptadorBancoTalentos(Context context, List<CandidatoRecusado> candidatosList) {
        this.context = context;
        this.candidatosList = candidatosList;
    }

    @NonNull
    @Override
    public CandidatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_candidato_recusado, parent, false);
        return new CandidatoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatoViewHolder holder, int position) {
        CandidatoRecusado candidato = candidatosList.get(position);

        holder.nomeTextView.setText(candidato.getNome());
        holder.cargoTextView.setText(candidato.getCargoDesejado());
        holder.areaTextView.setText(candidato.getAreaAtuacao());
        holder.nivelTextView.setText(candidato.getNivelExperiencia());

        if (candidato.getDataRecusa() != null) {
            holder.dataRecusaTextView.setText(dateFormat.format(candidato.getDataRecusa()));
        }

        holder.motivoTextView.setText(candidato.getMotivoRecusa());

        // Você pode adicionar uma imagem padrão ou carregar uma foto do candidato se tiver
        // holder.fotoImageView.setImageResource(R.drawable.ic_person);
    }

    @Override
    public int getItemCount() {
        return candidatosList.size();
    }

    public static class CandidatoViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView, cargoTextView, areaTextView, nivelTextView, dataRecusaTextView, motivoTextView;
        ImageView fotoImageView;

        public CandidatoViewHolder(@NonNull View itemView) {
            super(itemView);

            nomeTextView = itemView.findViewById(R.id.nomeTextView);
            cargoTextView = itemView.findViewById(R.id.cargoTextView);
            areaTextView = itemView.findViewById(R.id.areaTextView);
            nivelTextView = itemView.findViewById(R.id.nivelTextView);
            dataRecusaTextView = itemView.findViewById(R.id.dataRecusaTextView);
            motivoTextView = itemView.findViewById(R.id.motivoTextView);
            fotoImageView = itemView.findViewById(R.id.fotoImageView);
        }
    }
}
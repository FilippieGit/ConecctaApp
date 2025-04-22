package com.example.cardstackview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorTelaPrincipal extends RecyclerView.Adapter<AdaptadorTelaPrincipal.ViewHolder> {

    private Context context;
    private List<MatchVaga> listaMatchVagas;
    private OnItemClickListener listener;

    // Interface para o clique
    public interface OnItemClickListener {
        void onItemClick(MatchVaga vaga);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AdaptadorTelaPrincipal(Context context, List<MatchVaga> listamatch) {
        this.context = context;
        this.listaMatchVagas = listamatch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.telaprincipal_modelo_vaga_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < listaMatchVagas.size()) {
            MatchVaga vaga = listaMatchVagas.get(position);
            holder.modeloinfoempresa.setText(vaga.getTitulo());
            holder.modelofotoempresa.setImageResource(vaga.getImage());

            // Clique no item inteiro
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(vaga);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaMatchVagas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView modelofotoempresa;
        TextView modeloinfoempresa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            modelofotoempresa = itemView.findViewById(R.id.idlogodaempresa);
            modeloinfoempresa = itemView.findViewById(R.id.idinfoempresa);
        }
    }
}

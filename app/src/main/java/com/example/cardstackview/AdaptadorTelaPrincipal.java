package com.example.cardstackview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorTelaPrincipal extends RecyclerView.Adapter<AdaptadorTelaPrincipal.ViewHolder> {

    private final Context context;
    private final List<MatchVaga> listaMatchVagaList;
    private OnItemClickListener onItemClickListener;

    public AdaptadorTelaPrincipal(Context context, List<MatchVaga> listaMatchVagaList) {
        this.context = context;
        this.listaMatchVagaList = listaMatchVagaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Verifique se o nome do layout está correto
        View view = LayoutInflater.from(context).inflate(R.layout.item_vaga_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchVaga vaga = listaMatchVagaList.get(position);

        // Exemplo de como configurar os dados no item da lista
        holder.titulo.setText(vaga.getTitulo());
        holder.imagem.setImageResource(vaga.getImage());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(vaga);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMatchVagaList.size();
    }

    // Define o listener de clique
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Interface para o listener de clique
    public interface OnItemClickListener {
        void onItemClick(MatchVaga vaga);
    }

    // ViewHolder para o item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        ImageView imagem;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloItem);
            imagem = itemView.findViewById(R.id.imageItemVaga);
        }
    }
}


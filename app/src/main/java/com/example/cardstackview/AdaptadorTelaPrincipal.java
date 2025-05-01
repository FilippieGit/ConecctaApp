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
    private final List<Vaga> listaVagas;
    private OnItemClickListener onItemClickListener;

    public AdaptadorTelaPrincipal(Context context, List<Vaga> listaVagas) {
        this.context = context;
        this.listaVagas = listaVagas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vaga_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vaga vaga = listaVagas.get(position);

        holder.titulo.setText(vaga.getTitulo());

        // Usando localização como subtítulo, pode ajustar para descrição curta se preferir
        String subtitulo = vaga.getLocalizacao();
        if (subtitulo == null || subtitulo.isEmpty()) {
            subtitulo = vaga.getDescricao();
        }
        holder.subtitulo.setText(subtitulo);

        // Imagem fixa (logo padrão), ajuste se tiver imagem dinâmica
        holder.imagem.setImageResource(R.drawable.logo);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(vaga);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaVagas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Vaga vaga);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, subtitulo;
        ImageView imagem;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloItem);
            subtitulo = itemView.findViewById(R.id.textSubtituloItem);
            imagem = itemView.findViewById(R.id.imageItemVaga);
        }
    }
}

package com.example.cardstackview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorTelaPrincipal extends RecyclerView.Adapter<AdaptadorTelaPrincipal.ViewHolder> {

    private final Context context;
    private final List<Vagas> listaVagas;
    private OnItemClickListener onItemClickListener;

    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY = 0;


    public AdaptadorTelaPrincipal(Context context, List<Vagas> listaVagas) {
        this.context = context;
        this.listaVagas = listaVagas != null ? listaVagas : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vaga_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (listaVagas == null || listaVagas.isEmpty() || position < 0 || position >= listaVagas.size()) {
            return;
        }

        Vagas vaga = listaVagas.get(position);
        if (vaga == null) {
            return;
        }

        // Configura título
        holder.titulo.setText(vaga.getTitulo() != null ? vaga.getTitulo() : "Vaga sem título");

        // Configura subtítulo
        String localizacao = vaga.getLocalizacao();
        String descricao = vaga.getDescricao();

        if (localizacao != null && !localizacao.isEmpty()) {
            holder.subtitulo.setText(localizacao);
        } else if (descricao != null && !descricao.isEmpty()) {
            holder.subtitulo.setText(descricao.length() > 50 ?
                    descricao.substring(0, 47) + "..." : descricao);
        } else {
            holder.subtitulo.setText("Sem informações adicionais");
        }

        // Configura imagem (pode ser personalizada posteriormente)
        holder.imagem.setImageResource(R.drawable.logo);

        // Configura clique
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(vaga);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listaVagas == null || listaVagas.isEmpty()) {
            return 1; // Retorna 1 para mostrar um item de "lista vazia"
        }
        return listaVagas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (listaVagas == null || listaVagas.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_NORMAL;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Vagas vaga);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, subtitulo;
        ImageView imagem;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloItem);
            subtitulo = itemView.findViewById(R.id.textSubtituloItem);
            imagem = itemView.findViewById(R.id.imageItemVaga);

            // Verificação adicional para evitar null pointer
            if (titulo == null || subtitulo == null || imagem == null) {
                throw new IllegalStateException("Views não encontradas no layout do item");
            }
        }
    }
}


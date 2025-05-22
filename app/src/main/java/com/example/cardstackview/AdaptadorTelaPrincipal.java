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

public class AdaptadorTelaPrincipal extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = inflater.inflate(R.layout.item_lista_vazia, parent, false);
            return new EmptyViewHolder(emptyView);
        }

        View view = inflater.inflate(R.layout.item_vaga_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_EMPTY) {
            return; // Não faz nada para o view holder vazio
        }

        // Cast seguro para ViewHolder
        if (!(holder instanceof ViewHolder)) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) holder;

        if (listaVagas == null || position < 0 || position >= listaVagas.size()) {
            return;
        }

        Vagas vaga = listaVagas.get(position);
        if (vaga == null) {
            return;
        }

        // Configura título
        viewHolder.titulo.setText(vaga.getTitulo() != null ? vaga.getTitulo() : "Vaga sem título");

        // Configura subtítulo
        String localizacao = vaga.getLocalizacao();
        String descricao = vaga.getDescricao();

        if (localizacao != null && !localizacao.isEmpty()) {
            viewHolder.subtitulo.setText(localizacao);
        } else if (descricao != null && !descricao.isEmpty()) {
            viewHolder.subtitulo.setText(descricao.length() > 50 ?
                    descricao.substring(0, 47) + "..." : descricao);
        } else {
            viewHolder.subtitulo.setText("Sem informações adicionais");
        }

        // Configura imagem
        viewHolder.imagem.setImageResource(R.drawable.logo);

        // Configura clique
        viewHolder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(vaga);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaVagas.isEmpty() ? 1 : listaVagas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listaVagas.isEmpty() ? VIEW_TYPE_EMPTY : VIEW_TYPE_NORMAL;
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

            if (titulo == null || subtitulo == null || imagem == null) {
                throw new IllegalStateException("Views não encontradas no layout do item");
            }
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
            // Configurações para a view vazia, se necessário
        }
    }
}
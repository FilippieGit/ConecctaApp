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

public class AdaptadorBancoTalentos extends RecyclerView.Adapter<AdaptadorBancoTalentos.ViewHolder> {

    private Context context;
    private List<Lista> listaList;

    public AdaptadorBancoTalentos(Context context, List<Lista> listaList) {
        this.context = context;
        this.listaList = listaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout de item da lista
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.modelo_lista_activity, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Verifica se a posição é válida dentro da lista
        if (position < listaList.size()) {
            // Preenche os dados do item no ViewHolder
            Lista listaItem = listaList.get(position);
            holder.modeloinfoempresa.setText(listaItem.getTitulo());  // Setando o nome da empresa
            holder.modelofotoempresa.setImageResource(listaItem.getImage());  // Setando a imagem da empresa
        }
    }

    @Override
    public int getItemCount() {
        // Limita o número de itens a 5, como no exemplo anterior
        return Math.min(listaList.size(), 5);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Definindo os componentes do item da lista
        ImageView modelofotoempresa;
        TextView modeloinfoempresa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializando os componentes com os IDs do layout
            modelofotoempresa = itemView.findViewById(R.id.idlogodaempresa);
            modeloinfoempresa = itemView.findViewById(R.id.idinfoempresa);
        }
    }
}

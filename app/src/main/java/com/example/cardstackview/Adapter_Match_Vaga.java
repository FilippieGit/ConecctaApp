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

public class Adapter_Match_Vaga extends RecyclerView.Adapter<Adapter_Match_Vaga.ViewHolder> {

    private Context context;
    private List<ListaMatchVaga> listaMatchVagas;

    public Adapter_Match_Vaga(Context context, List<ListaMatchVaga> listamatch) {
        this.context = context;
        this.listaMatchVagas = listamatch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.modelo_vaga, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < listaMatchVagas.size()) {
            holder.modeloinfoempresa.setText(listaMatchVagas.get(position).getTitulo());
            holder.modelofotoempresa.setImageResource(listaMatchVagas.get(position).getImage());
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(listaMatchVagas.size(), 5);
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

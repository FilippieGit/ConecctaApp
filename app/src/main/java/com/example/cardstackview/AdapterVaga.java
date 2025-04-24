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

public class AdapterVaga extends RecyclerView.Adapter<AdapterVaga.ViewHolder> {

    private Context context;
    private List<ListaVaga> listavaga;

    public AdapterVaga(Context context, List<ListaVaga> listavaga) {
        this.context = context;
        this.listavaga = listavaga;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.telaprincipal_modelo_vaga_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < listavaga.size()) {
            holder.modeloinfoempresa.setText(listavaga.get(position).getTitulo());
            holder.modelofotoempresa.setImageResource(listavaga.get(position).getImage());
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(listavaga.size(), 5);
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

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

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context context;
    private List<Lista> listaList;

    public Adapter(Context context, List<Lista> listaList) {
        this.context = context;
        this.listaList = listaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.modelo_lista_activity,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < listaList.size()) {
            holder.modeloinfoempresa.setText(listaList.get(position).getTitulo());
            holder.modelofotoempresa.setImageResource(listaList.get(position).getImage());
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(listaList.size(), 5);
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView modelofotoempresa;

        TextView modeloinfoempresa;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            modelofotoempresa = itemView.findViewById(R.id.idlogodaempresa);
            modeloinfoempresa = itemView.findViewById(R.id.idinfoempresa);

        }
    }

}

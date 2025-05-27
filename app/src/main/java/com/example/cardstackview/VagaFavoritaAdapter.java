package com.example.cardstackview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VagaFavoritaAdapter extends RecyclerView.Adapter<VagaFavoritaAdapter.VagaViewHolder> {

    private List<Vaga> vagas;
    private final OnVagaClickListener listener;

    public interface OnVagaClickListener {
        void onVagaClick(Vaga vaga);
    }

    public VagaFavoritaAdapter(List<Vaga> vagas, OnVagaClickListener listener) {
        this.vagas = vagas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VagaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vaga_favorita, parent, false);
        return new VagaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VagaViewHolder holder, int position) {
        Vaga vaga = vagas.get(position);
        holder.bind(vaga, listener);
    }

    @Override
    public int getItemCount() {
        return vagas.size();
    }

    public void updateList(List<Vaga> newList) {
        vagas = newList;
        notifyDataSetChanged();
    }

    static class VagaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTituloVaga;
        private final TextView tvEmpresa;
        private final TextView tvLocalizacao;
        private final TextView tvTipoContrato;
        private final TextView tvSalario;
        private final TextView tvDataPublicacao;
        private final ImageButton btnRemoveFavorito;

        public VagaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloVaga = itemView.findViewById(R.id.tvTituloVaga);
            tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
            tvLocalizacao = itemView.findViewById(R.id.tvLocalizacao);
            tvTipoContrato = itemView.findViewById(R.id.tvTipoContrato);
            tvSalario = itemView.findViewById(R.id.tvSalario);
            tvDataPublicacao = itemView.findViewById(R.id.tvDataPublicacao);
            btnRemoveFavorito = itemView.findViewById(R.id.btnRemoveFavorito);
        }

        public void bind(Vaga vaga, OnVagaClickListener listener) {
            tvTituloVaga.setText(vaga.getTitulo());
            tvEmpresa.setText(vaga.getEmpresa());
            tvLocalizacao.setText(vaga.getLocalizacao());
            tvTipoContrato.setText(vaga.getTipoContrato());
            tvSalario.setText(vaga.getSalario());
            tvDataPublicacao.setText(formatarData(vaga.getDataPublicacao()));

            btnRemoveFavorito.setOnClickListener(v -> listener.onVagaClick(vaga));
        }

        private String formatarData(String dataOriginal) {
            // Implemente a formatação da data conforme necessário
            return "Publicada em " + dataOriginal;
        }
    }
}
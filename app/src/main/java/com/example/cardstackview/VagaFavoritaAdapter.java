package com.example.cardstackview;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VagaFavoritaAdapter extends RecyclerView.Adapter<VagaFavoritaAdapter.VagaViewHolder> {

    private List<Vagas> vagas;
    private final OnVagaClickListener listener;

    public interface OnVagaClickListener {
        void onVagaClick(Vagas vaga);
        void onVagaDetalhesClick(Vagas vaga);
    }

    public VagaFavoritaAdapter(List<Vagas> vagas, OnVagaClickListener listener) {
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
        Vagas vaga = vagas.get(position);
        holder.bind(vaga, listener);
    }

    @Override
    public int getItemCount() {
        return vagas.size();
    }

    public void updateList(List<Vagas> newList) {
        vagas = newList;
        notifyDataSetChanged();
    }

    static class VagaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTituloVaga, tvEmpresa, tvLocalizacao, tvTipoContrato, tvSalario, tvDataPublicacao;
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

        public void bind(Vagas vaga, OnVagaClickListener listener) {
            tvTituloVaga.setText(vaga.getTitulo());
            tvEmpresa.setText(vaga.getNome_empresa());
            tvLocalizacao.setText(vaga.getLocalizacao());
            tvTipoContrato.setText(vaga.getTipo_contrato());
            tvSalario.setText(vaga.getSalario());
            tvDataPublicacao.setText("Publicada recentemente");

            // Configura o ícone inicial (cheio e vermelho)
            btnRemoveFavorito.setImageResource(R.drawable.ic_favorite_filled);
            btnRemoveFavorito.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ED0707")));

            btnRemoveFavorito.setOnClickListener(v -> {
                // Remove a tintagem para evitar sobreposição de cores
                btnRemoveFavorito.setImageTintList(null);

                // Animação de escala
                btnRemoveFavorito.animate()
                        .scaleX(0.7f)
                        .scaleY(0.7f)
                        .setDuration(150)
                        .withEndAction(() -> {
                            // Troca para o ícone vazio (apenas borda preta)
                            btnRemoveFavorito.setImageResource(R.drawable.ic_favorite_empty);

                            // Volta ao tamanho normal
                            btnRemoveFavorito.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .withEndAction(() -> {
                                        // Notifica o clique só após toda animação
                                        listener.onVagaClick(vaga);
                                    })
                                    .start();
                        })
                        .start();
            });

            itemView.setOnClickListener(v -> listener.onVagaDetalhesClick(vaga));
        }
    }
}
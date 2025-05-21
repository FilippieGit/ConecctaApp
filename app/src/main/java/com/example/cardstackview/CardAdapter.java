package com.example.cardstackview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cardstackview.databinding.CardBinding;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.VagasViewHolder> {
    private List<Vagas> vagasList;
    private Context context;

    public CardAdapter(Context context, List<Vagas> vagasList) {
        this.context = context;
        this.vagasList = vagasList;
    }

    @NonNull
    @Override
    public VagasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);
        return new VagasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VagasViewHolder holder, int position) {
        Vagas vaga = vagasList.get(position);

        holder.tvJobTitle.setText(vaga.getTitulo());
        holder.tvBranch.setText(vaga.getArea_atuacao());
        holder.tvLocation.setText(vaga.getLocalizacao());

        // Tratamento robusto para benefícios no Adapter
        String beneficios = vaga.getBeneficios();
        if (beneficios == null || beneficios.trim().isEmpty()) {
            beneficios = "Não informado";
        }
        holder.tvBenefits.setText(beneficios);


        holder.tvrequisitos.setText(vaga.getRequisitos());
        holder.tvCompanyName.setText(vaga.getNome_empresa());

        holder.imgCompanyLogo.setImageResource(R.drawable.logo);

        holder.btnViewDetails.setOnClickListener(v -> {
            SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            boolean isPessoaJuridica = prefs.getBoolean("isPessoaJuridica", false);

            Intent intent = new Intent(context, DetalheVagaActivity.class);
            intent.putExtra("vaga", vaga);
            intent.putExtra("isPessoaJuridica", isPessoaJuridica);
            context.startActivity(intent);
        });
    }



    private String formatarSalario(String salario) {
        try {
            double valor = Double.parseDouble(salario);
            return "Salário: " + NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
        } catch (NumberFormatException e) {
            return "Salário: " + salario;
        }
    }

    @Override
    public int getItemCount() {
        return vagasList.size();
    }

    public static class VagasViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCompanyLogo;
        TextView tvJobTitle, tvCompanyName, tvBranch, tvLocation, tvBenefits, tvrequisitos;
        Button btnViewDetails;

        public VagasViewHolder(View view) {
            super(view);
            imgCompanyLogo = view.findViewById(R.id.imgCompanyLogo);
            tvJobTitle = view.findViewById(R.id.tvJobTitle);
            tvCompanyName = view.findViewById(R.id.tvCompanyName);
            tvBranch = view.findViewById(R.id.tvBranch);
            tvLocation = view.findViewById(R.id.tvLocation);
            tvBenefits = view.findViewById(R.id.tvBenefits);
            tvrequisitos = view.findViewById(R.id.tvrequisitos);
            btnViewDetails = view.findViewById(R.id.btnViewDetails);
        }
    }
}
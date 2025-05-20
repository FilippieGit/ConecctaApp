package com.example.cardstackview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CertificadoAdapter extends RecyclerView.Adapter<CertificadoAdapter.ViewHolder> {
    private List<Certificado> lista;
    public CertificadoAdapter(List<Certificado> lista) { this.lista = lista; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtInstituicao, txtAno, txtDuracao, txtDescricao;
        ImageButton btnRemover;
        public ViewHolder(View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.editTextNomeCertificado);
            txtInstituicao = itemView.findViewById(R.id.editTextInstituicao);
            txtAno = itemView.findViewById(R.id.editTextAno);
            txtDuracao = itemView.findViewById(R.id.editTextDuracao);
            txtDescricao = itemView.findViewById(R.id.editTextDescricao);
            btnRemover = itemView.findViewById(R.id.buttonSalvarCertificado);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificado, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        Certificado c = lista.get(position);
        holder.txtNome.setText(c.nome);
        holder.txtInstituicao.setText(c.instituicao);
        holder.txtAno.setText(c.ano);
        holder.txtDuracao.setText(c.duracao);
        holder.txtDescricao.setText(c.descricao);
        holder.btnRemover.setOnClickListener(v -> {
            lista.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, lista.size());
        });
    }

    @Override public int getItemCount() { return lista.size(); }
}
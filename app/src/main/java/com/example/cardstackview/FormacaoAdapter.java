package com.example.cardstackview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FormacaoAdapter extends RecyclerView.Adapter<FormacaoAdapter.ViewHolder> {
    private List<Formacao> lista;
    public FormacaoAdapter(List<Formacao> lista) { this.lista = lista; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCurso, txtInstituicao, txtAnoInicio, txtAnoConclusao, txtDescricao;
        ImageButton btnRemover;
        public ViewHolder(View itemView) {
            super(itemView);
            txtCurso = itemView.findViewById(R.id.editTextCurso);
            txtInstituicao = itemView.findViewById(R.id.editTextInstituicao);
            txtAnoInicio = itemView.findViewById(R.id.editTextAnoInicio);
            txtAnoConclusao = itemView.findViewById(R.id.editTextAnoConclusao);
            txtDescricao = itemView.findViewById(R.id.editTextDescricao);
            btnRemover = itemView.findViewById(R.id.buttonSalvarFormacao);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_adicionar_formacao_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Formacao f = lista.get(position);
        holder.txtCurso.setText(f.curso);
        holder.txtInstituicao.setText(f.instituicao);
        holder.txtAnoInicio.setText(f.anoInicio);
        holder.txtAnoConclusao.setText(f.anoConclusao);
        holder.txtDescricao.setText(f.descricao);

        holder.btnRemover.setOnClickListener(v -> {
            lista.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, lista.size());
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}

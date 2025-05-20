package com.example.cardstackview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExperienciaAdapter extends RecyclerView.Adapter<ExperienciaAdapter.ViewHolder> {
    private List<Experiencia> lista;
    public ExperienciaAdapter(List<Experiencia> lista) { this.lista = lista; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCargo, txtEmpresa, txtPeriodo, txtLocal, txtDescricao;
        ImageButton btnRemover;
        public ViewHolder(View itemView) {
            super(itemView);
            txtCargo = itemView.findViewById(R.id.editTextCargo);
            txtEmpresa = itemView.findViewById(R.id.editTextEmpresa);
            txtPeriodo = itemView.findViewById(R.id.editTextPeriodo);
            txtLocal = itemView.findViewById(R.id.editTextLocal);
            txtDescricao = itemView.findViewById(R.id.editTextDescricao);
            btnRemover = itemView.findViewById(R.id.buttonSalvarExperiencia);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_adicionar_experiencia_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Experiencia e = lista.get(position);
        holder.txtCargo.setText(e.cargo);
        holder.txtEmpresa.setText(e.empresa);
        holder.txtPeriodo.setText(e.periodo);
        holder.txtLocal.setText(e.local);
        holder.txtDescricao.setText(e.descricao);

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
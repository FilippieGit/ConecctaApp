package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PerfilEmpresaActivity extends Fragment {
    private Button btnJEditarPerfil;

    public PerfilEmpresaActivity() {
        // Construtor vazio obrigatório
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Infla o layout XML
        View view = inflater.inflate(R.layout.perfil_empresa_layout, container, false);

        // Inicializa os componentes
        btnJEditarPerfil = view.findViewById(R.id.btnJEditarPerfil);

        // Função de editar perfil
        btnJEditarPerfil.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EdicaoPerfilEmpresaActivity.class));
            requireActivity().finish(); // Encerra se desejar remover a tela atual da stack
        });

        return view;
    }
}

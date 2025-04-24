package com.example.cardstackview;

import java.io.Serializable;

public class VagaActivity implements Serializable {

    private String titulo;
    private String descricao;
    private String localizacao;
    private String salario;
    private String requisitos;

    public VagaActivity(String titulo, String descricao, String localizacao, String salario, String requisitos) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.salario = salario;
        this.requisitos = requisitos;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public String getSalario() {
        return salario;
    }

    public String getRequisitos() {
        return requisitos;
    }
}

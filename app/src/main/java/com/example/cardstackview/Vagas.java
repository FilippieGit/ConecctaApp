package com.example.cardstackview;

import java.io.Serializable;

public class Vagas implements Serializable {
    private int vaga_id;
    private String titulo;
    private String descricao;
    private String localizacao;
    private String salario;
    private String requisitos;
    private String nivel_experiencia;
    private String tipo_contrato;
    private String area_atuacao;
    private String beneficios;
    private int empresa_id;
    private String nome_empresa;  // novo campo para nome da empresa

    public Vagas(int vaga_id, String titulo, String descricao, String localizacao,
                 String salario, String requisitos, String nivel_experiencia,
                 String tipo_contrato, String area_atuacao, String beneficios,
                 int empresa_id, String nome_empresa) {
        this.vaga_id = vaga_id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.salario = salario;
        this.requisitos = requisitos;
        this.nivel_experiencia = nivel_experiencia;
        this.tipo_contrato = tipo_contrato;
        this.area_atuacao = area_atuacao;
        this.beneficios = beneficios;
        this.empresa_id = empresa_id;
        this.nome_empresa = nome_empresa;
    }

    // Getter para nome_empresa
    public String getNome_empresa() {
        return nome_empresa;
    }


    // Getter para benefícios
    public String getBeneficios() {
        return beneficios;
    }


    // Getters
    public int getVaga_id() {
        return vaga_id;
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

    public String getNivel_experiencia() {
        return nivel_experiencia;
    }

    public String getTipo_contrato() {
        return tipo_contrato;
    }

    public String getArea_atuacao() {
        return area_atuacao;
    }

    public int getEmpresa_id() {
        return empresa_id;
    }
}
package com.example.cardstackview;

public class Vagas {
    private int vaga_id;
    private String titulo;
    private String descricao;
    private String localizacao;
    private String salario;
    private String requisitos;
    private String nivel_experiencia;
    private String tipo_contrato;
    private String area_atuacao;
    private int empresa_id;

    public Vagas(int vaga_id, String titulo, String descricao, String localizacao,
                 String salario, String requisitos, String nivel_experiencia,
                 String tipo_contrato, String area_atuacao, int empresa_id) {
        this.vaga_id = vaga_id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.salario = salario;
        this.requisitos = requisitos;
        this.nivel_experiencia = nivel_experiencia;
        this.tipo_contrato = tipo_contrato;
        this.area_atuacao = area_atuacao;
        this.empresa_id = empresa_id;
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
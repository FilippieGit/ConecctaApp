package com.example.cardstackview;

import java.io.Serializable;

public class Vagas implements Serializable {

    private String titulo, descricao, localizacao, salario, requisitos;
    private String nivel, contrato, area;
    private String idEmpresa;

    private int vaga_id;
    private String nivel_experiencia;
    private String tipo_contrato;
    private String area_atuacao;
    private String beneficios;
    private int empresa_id;
    private String nome_empresa;

    // Construtor completo
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

    // Construtor sem benefícios e nome_empresa (caso não tenha esses dados)
    public Vagas(int vaga_id, String titulo, String descricao, String localizacao,
                 String salario, String requisitos, String nivel_experiencia,
                 String tipo_contrato, String area_atuacao, int empresa_id) {
        this(vaga_id, titulo, descricao, localizacao, salario, requisitos, nivel_experiencia,
                tipo_contrato, area_atuacao, null, empresa_id, null);
    }

    // Construtor vazio (para uso com setters)
    public Vagas() {}

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

    public String getBeneficios() {
        return beneficios;
    }

    public int getEmpresa_id() {
        return empresa_id;
    }

    public String getNome_empresa() {
        return nome_empresa;
    }

    // Setters (útil para construir o objeto após o cadastro, se necessário)
    public void setVaga_id(int vaga_id) {
        this.vaga_id = vaga_id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public void setNivel_experiencia(String nivel_experiencia) {
        this.nivel_experiencia = nivel_experiencia;
    }

    public void setTipo_contrato(String tipo_contrato) {
        this.tipo_contrato = tipo_contrato;
    }

    public void setArea_atuacao(String area_atuacao) {
        this.area_atuacao = area_atuacao;
    }

    public void setBeneficios(String beneficios) {
        this.beneficios = beneficios;
    }

    public void setEmpresa_id(int empresa_id) {
        this.empresa_id = empresa_id;
    }

    public void setNome_empresa(String nome_empresa) {
        this.nome_empresa = nome_empresa;
    }
}

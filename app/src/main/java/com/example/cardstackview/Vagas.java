package com.example.cardstackview;

import java.io.Serializable;
import java.util.List;

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
    private String nome_empresa;
    private List<String> habilidadesDesejaveis; // Opcional

    // Construtor completo
    public Vagas(int vaga_id, String titulo, String descricao, String localizacao,
                 String salario, String requisitos, String nivel_experiencia,
                 String tipo_contrato, String area_atuacao, String beneficios,
                 int empresa_id, String nome_empresa, List<String> habilidadesDesejaveis) {
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
        this.habilidadesDesejaveis = habilidadesDesejaveis;
    }

    // Construtor para criação (sem vaga_id, nome_empresa e habilidades)
    public Vagas(String titulo, String descricao, String localizacao, String salario,
                 String requisitos, String nivel_experiencia, String tipo_contrato,
                 String area_atuacao, String beneficios, int empresa_id) {
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
    }

    public Vagas(int vaga_id, String titulo, String descricao, String localizacao,
                 String salario, String requisitos, String nivel_experiencia,
                 String tipo_contrato, String area_atuacao, String beneficios,
                 int empresa_id, String nome_empresa) {
        this(vaga_id, titulo, descricao, localizacao, salario, requisitos, nivel_experiencia,
                tipo_contrato, area_atuacao, beneficios, empresa_id, nome_empresa, null);
    }


    // Construtor para pré-visualização (sem benefícios)
    public Vagas(String titulo, String descricao, String localizacao, String salario,
                 String requisitos, String nivel_experiencia, String tipo_contrato,
                 String area_atuacao, int empresa_id) {
        this(titulo, descricao, localizacao, salario, requisitos, nivel_experiencia, tipo_contrato, area_atuacao, null, empresa_id);
    }

    // Construtor vazio
    public Vagas() {}

    // Getters e setters (omitidos aqui para brevidade, mas devem estar todos)

    public int getVaga_id() {
        return vaga_id;
    }

    public void setVaga_id(int vaga_id) {
        this.vaga_id = vaga_id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public String getNivel_experiencia() {
        return nivel_experiencia;
    }

    public void setNivel_experiencia(String nivel_experiencia) {
        this.nivel_experiencia = nivel_experiencia;
    }

    public String getTipo_contrato() {
        return tipo_contrato;
    }

    public void setTipo_contrato(String tipo_contrato) {
        this.tipo_contrato = tipo_contrato;
    }

    public String getArea_atuacao() {
        return area_atuacao;
    }

    public void setArea_atuacao(String area_atuacao) {
        this.area_atuacao = area_atuacao;
    }

    public String getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(String beneficios) {
        this.beneficios = beneficios;
    }

    public int getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(int empresa_id) {
        this.empresa_id = empresa_id;
    }

    public String getNome_empresa() {
        return nome_empresa;
    }

    public void setNome_empresa(String nome_empresa) {
        this.nome_empresa = nome_empresa;
    }

    public List<String> getHabilidadesDesejaveis() {
        return habilidadesDesejaveis;
    }

    public void setHabilidadesDesejaveis(List<String> habilidadesDesejaveis) {
        this.habilidadesDesejaveis = habilidadesDesejaveis;
    }
}

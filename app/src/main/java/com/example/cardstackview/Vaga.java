package com.example.cardstackview;

import java.io.Serializable;
import java.util.List;

public class Vaga implements Serializable {
    private String id; // pode ser String (Firebase) ou int (SQL)
    private String titulo;
    private String descricao;
    private String localizacao;
    private String salario;
    private String requisitos;
    private String nivelExperiencia;
    private String tipoContrato;
    private String areaAtuacao;
    private String idEmpresa; // Para associar ao usuário logado
    private List<String> habilidadesDesejaveis; // Opcional: para chips

    // Construtor padrão
    public Vaga() {}

    // Construtor completo (sem habilidades)
    public Vaga(String titulo, String descricao, String localizacao, String salario,
                String requisitos, String nivelExperiencia, String tipoContrato,
                String areaAtuacao, String idEmpresa) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.salario = salario;
        this.requisitos = requisitos;
        this.nivelExperiencia = nivelExperiencia;
        this.tipoContrato = tipoContrato;
        this.areaAtuacao = areaAtuacao;
        this.idEmpresa = idEmpresa;
    }

    // Construtor completo (com habilidades)
    public Vaga(String titulo, String descricao, String localizacao, String salario,
                String requisitos, String nivelExperiencia, String tipoContrato,
                String areaAtuacao, String idEmpresa, List<String> habilidadesDesejaveis) {
        this(titulo, descricao, localizacao, salario, requisitos, nivelExperiencia, tipoContrato, areaAtuacao, idEmpresa);
        this.habilidadesDesejaveis = habilidadesDesejaveis;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getSalario() { return salario; }
    public void setSalario(String salario) { this.salario = salario; }

    public String getRequisitos() { return requisitos; }
    public void setRequisitos(String requisitos) { this.requisitos = requisitos; }

    public String getNivelExperiencia() { return nivelExperiencia; }
    public void setNivelExperiencia(String nivelExperiencia) { this.nivelExperiencia = nivelExperiencia; }

    public String getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(String tipoContrato) { this.tipoContrato = tipoContrato; }

    public String getAreaAtuacao() { return areaAtuacao; }
    public void setAreaAtuacao(String areaAtuacao) { this.areaAtuacao = areaAtuacao; }

    public String getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(String idEmpresa) { this.idEmpresa = idEmpresa; }

    public List<String> getHabilidadesDesejaveis() { return habilidadesDesejaveis; }
    public void setHabilidadesDesejaveis(List<String> habilidadesDesejaveis) { this.habilidadesDesejaveis = habilidadesDesejaveis; }
}

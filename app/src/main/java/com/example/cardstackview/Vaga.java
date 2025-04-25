package com.example.cardstackview;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;


public class Vaga implements Serializable {

    private String id; // ou int id

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    private String titulo;
    private String descricao;
    private String localizacao;
    private String salario;
    private String requisitos;
    private String nivelExperiencia;
    private String tipoContrato;
    private String areaAtuacao;

    // Construtor padrão (sem argumentos)
    public Vaga() {
    }

    // Construtor com 5 argumentos
    public Vaga(String titulo, String descricao, String localizacao, String salario, String requisitos) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.salario = salario;
        this.requisitos = requisitos;
    }

    // Construtor com 8 argumentos
    public Vaga(String titulo, String descricao, String localizacao, String salario, String requisitos, String nivelExperiencia, String tipoContrato, String areaAtuacao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.salario = salario;
        this.requisitos = requisitos;
        this.nivelExperiencia = nivelExperiencia;
        this.tipoContrato = tipoContrato;
        this.areaAtuacao = areaAtuacao;
    }

    // Getters e setters para os atributos
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

    public String getNivelExperiencia() {
        return nivelExperiencia;
    }

    public void setNivelExperiencia(String nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }
}

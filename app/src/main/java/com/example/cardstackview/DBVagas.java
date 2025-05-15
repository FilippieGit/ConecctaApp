package com.example.cardstackview;

public class DBVagas {

    //declarar os itens

    private String nome;

    private int id;

    private String descricao;

    private double salario;

    private String cargaHoraria;
    private String local;

    private String regime;

    //criar constructor alt + insert

    public DBVagas(String nome, int id, String descricao, double salario, String cargaHoraria, String local, String regime) {

        this.nome = nome;
        this.id = id;
        this.descricao = descricao;
        this.salario = salario;
        this.cargaHoraria = cargaHoraria;
        this.local = local;
        this.regime = regime;
    }

    //criar getter and setter alt + insert

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(String cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getRegime() {
        return regime;
    }

    public void setRegime(String regime) {
        this.regime = regime;
    }


}


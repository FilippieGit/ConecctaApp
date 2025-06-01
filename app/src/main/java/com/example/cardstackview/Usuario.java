package com.example.cardstackview;

import org.json.JSONObject;

import java.util.Date;

public class Usuario {
    private long id;
    private String nome;
    private String email;
    private String cargo;
    private String status;
    private Date dataCandidatura;
    // Adicione os novos campos
    private String telefone;
    private String descricao;
    private JSONObject respostas; // ou crie uma classe Respostas
    private int idCandidatura;

    // Construtor atualizado
    public Usuario(long id, String nome, String email, String cargo, String status,
                   Date dataCandidatura, String telefone, String descricao,
                   JSONObject respostas, int idCandidatura) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.status = status;
        this.dataCandidatura = dataCandidatura;
        this.telefone = telefone;
        this.descricao = descricao;
        this.respostas = respostas;
        this.idCandidatura = idCandidatura;
    }

    public JSONObject getRespostas() {
        return respostas;
    }

    public void setRespostas(JSONObject respostas) {
        this.respostas = respostas;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataCandidatura() {
        return dataCandidatura;
    }

    public void setDataCandidatura(Date dataCandidatura) {
        this.dataCandidatura = dataCandidatura;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getIdCandidatura() {
        return idCandidatura;
    }

    public void setIdCandidatura(int idCandidatura) {
        this.idCandidatura = idCandidatura;
    }
}
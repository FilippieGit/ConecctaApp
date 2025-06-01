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
    private String telefone;
    private String descricao;
    private String experienciaProfissional;
    private String formacaoAcademica;
    private String certificados;
    private String username;
    private String genero;
    private String idade;

    // Construtor completo
    public Usuario(long id, String nome, String email, String cargo, String status,
                   Date dataCandidatura, String telefone, String descricao,
                   String experienciaProfissional, String formacaoAcademica,
                   String certificados, String username, String genero, String idade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.status = status;
        this.dataCandidatura = dataCandidatura;
        this.telefone = telefone;
        this.descricao = descricao;
        this.experienciaProfissional = experienciaProfissional;
        this.formacaoAcademica = formacaoAcademica;
        this.certificados = certificados;
        this.username = username;
        this.genero = genero;
        this.idade = idade;
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

    public String getExperienciaProfissional() {
        return experienciaProfissional;
    }

    public void setExperienciaProfissional(String experienciaProfissional) {
        this.experienciaProfissional = experienciaProfissional;
    }

    public String getFormacaoAcademica() {
        return formacaoAcademica;
    }

    public void setFormacaoAcademica(String formacaoAcademica) {
        this.formacaoAcademica = formacaoAcademica;
    }

    public String getCertificados() {
        return certificados;
    }

    public void setCertificados(String certificados) {
        this.certificados = certificados;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }
}
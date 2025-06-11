package com.example.cardstackview;

import java.util.Date;

public class CandidatoRecusado {
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String cargoDesejado;
    private String nivelExperiencia; // Júnior, Pleno, Sênior
    private String areaAtuacao; // TI, Marketing, RH, etc.
    private String motivoRecusa; // "Perfil não adequado", "Salário", etc.
    private Date dataCandidatura;
    private Date dataRecusa;
    private String vagaRecusada; // ID ou nome da vaga
    private String curriculoUrl; // URL do currículo armazenado
    private double pontuacao; // Pontuação na avaliação (se aplicável)
    private String observacoes;
    private boolean disponivelContato; // Se o candidato está disponível para novas oportunidades

    // Construtor
    public CandidatoRecusado() {
        // Construtor vazio necessário para Firebase
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCargoDesejado() {
        return cargoDesejado;
    }

    public void setCargoDesejado(String cargoDesejado) {
        this.cargoDesejado = cargoDesejado;
    }

    public String getNivelExperiencia() {
        return nivelExperiencia;
    }

    public void setNivelExperiencia(String nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public Date getDataCandidatura() {
        return dataCandidatura;
    }

    public void setDataCandidatura(Date dataCandidatura) {
        this.dataCandidatura = dataCandidatura;
    }

    public Date getDataRecusa() {
        return dataRecusa;
    }

    public void setDataRecusa(Date dataRecusa) {
        this.dataRecusa = dataRecusa;
    }

    public String getVagaRecusada() {
        return vagaRecusada;
    }

    public void setVagaRecusada(String vagaRecusada) {
        this.vagaRecusada = vagaRecusada;
    }

    public String getCurriculoUrl() {
        return curriculoUrl;
    }

    public void setCurriculoUrl(String curriculoUrl) {
        this.curriculoUrl = curriculoUrl;
    }

    public double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(double pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isDisponivelContato() {
        return disponivelContato;
    }

    public void setDisponivelContato(boolean disponivelContato) {
        this.disponivelContato = disponivelContato;
    }
}
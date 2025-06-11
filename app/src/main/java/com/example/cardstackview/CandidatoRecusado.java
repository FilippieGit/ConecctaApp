package com.example.cardstackview;

import java.io.Serializable;
import java.util.Date;

public class CandidatoRecusado implements Serializable {
    private String id; // ID da candidatura no Firestore, se você ainda usar. Ou id_candidatura do SQL.
    private String nome;
    private String cargoDesejado;
    private String areaAtuacao;
    private String nivelExperiencia;
    private Date dataRecusa; // Corresponde a 'data_atualizacao' do backend (status rejeitado)
    private String motivoRecusa; // Corresponde a 'motivo_rejeicao' do backend

    public CandidatoRecusado() {
        // Construtor vazio necessário para o Firestore ou desserialização de JSON
    }

    // Construtor para conveniência (opcional, pode ser preenchido pelos setters)
    public CandidatoRecusado(String id, String nome, String cargoDesejado, String areaAtuacao, String nivelExperiencia, Date dataRecusa, String motivoRecusa) {
        this.id = id;
        this.nome = nome;
        this.cargoDesejado = cargoDesejado;
        this.areaAtuacao = areaAtuacao;
        this.nivelExperiencia = nivelExperiencia;
        this.dataRecusa = dataRecusa;
        this.motivoRecusa = motivoRecusa;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCargoDesejado() {
        return cargoDesejado;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public String getNivelExperiencia() {
        return nivelExperiencia;
    }

    public Date getDataRecusa() {
        return dataRecusa;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCargoDesejado(String cargoDesejado) {
        this.cargoDesejado = cargoDesejado;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public void setNivelExperiencia(String nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }

    public void setDataRecusa(Date dataRecusa) {
        this.dataRecusa = dataRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }
}
package com.example.cardstackview;

import org.json.JSONObject;

import java.util.Date;

public class Usuario {
    private long id;
    private long idCandidatura; // Novo campo para armazenar o ID da candidatura
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
    private String motivoRejeicao; // Novo campo para armazenar motivo de rejeição
    private long recrutadorId; // Novo campo para armazenar quem alterou o status

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

    // Construtor a partir de JSONObject
    public Usuario(JSONObject json) throws Exception {
        this.id = json.getLong("id");
        this.idCandidatura = json.optLong("id_candidatura", 0);
        this.nome = json.getString("nome");
        this.email = json.getString("email");
        this.cargo = json.optString("cargo", "");
        this.status = json.getString("status");

        // Tratamento da data de candidatura
        if (json.has("data_candidatura") && !json.isNull("data_candidatura")) {
            String dataStr = json.getString("data_candidatura");
            // Implemente o parse da data conforme seu formato
            // this.dataCandidatura = ...;
        } else {
            this.dataCandidatura = new Date(json.optLong("data_candidatura", System.currentTimeMillis()));
        }

        this.telefone = json.optString("telefone", "");
        this.descricao = json.optString("descricao", "");
        this.experienciaProfissional = json.optString("experiencia_profissional", "");
        this.formacaoAcademica = json.optString("formacao_academica", "");
        this.certificados = json.optString("certificados", "");
        this.username = json.optString("username", "");
        this.genero = json.optString("genero", "");
        this.idade = json.optString("idade", "");
        this.motivoRejeicao = json.optString("motivo_rejeicao", null);
        this.recrutadorId = json.optLong("recrutador_id", 0);
    }

    // Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdCandidatura() {
        return idCandidatura;
    }

    public void setIdCandidatura(long idCandidatura) {
        this.idCandidatura = idCandidatura;
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

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public long getRecrutadorId() {
        return recrutadorId;
    }

    public void setRecrutadorId(long recrutadorId) {
        this.recrutadorId = recrutadorId;
    }

    // Método para converter para JSON
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("id_candidatura", idCandidatura);
        json.put("nome", nome);
        json.put("email", email);
        json.put("cargo", cargo);
        json.put("status", status);
        json.put("data_candidatura", dataCandidatura != null ? dataCandidatura.getTime() : null);
        json.put("telefone", telefone);
        json.put("descricao", descricao);
        json.put("experiencia_profissional", experienciaProfissional);
        json.put("formacao_academica", formacaoAcademica);
        json.put("certificados", certificados);
        json.put("username", username);
        json.put("genero", genero);
        json.put("idade", idade);
        json.put("motivo_rejeicao", motivoRejeicao);
        json.put("recrutador_id", recrutadorId);
        return json;
    }

    // Método para verificar se está aprovado
    public boolean isAprovado() {
        return "aprovada".equalsIgnoreCase(status);
    }

    // Método para verificar se está rejeitado
    public boolean isRejeitado() {
        return "rejeitada".equalsIgnoreCase(status);
    }
}
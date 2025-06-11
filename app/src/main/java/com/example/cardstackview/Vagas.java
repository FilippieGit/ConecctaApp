package com.example.cardstackview;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vagas implements Serializable {
    @SerializedName("id_vagas")
    private int vaga_id;

    @SerializedName("titulo_vagas")
    private String titulo;

    @SerializedName("descricao_vagas")
    private String descricao;

    @SerializedName("local_vagas")
    private String localizacao;

    @SerializedName("salario_vagas")
    private String salario;

    @SerializedName("requisitos_vagas")
    private String requisitos;

    @SerializedName("id_usuario")
    private Long id_usuario; // CHANGED: From 'long' to 'Long'

    @SerializedName("nivel_experiencia")
    private String nivel_experiencia;

    @SerializedName("tipo_contrato")
    private String tipo_contrato;

    @SerializedName("area_atuacao")
    private String area_atuacao;

    @SerializedName("beneficios_vagas")
    private String beneficios;

    @SerializedName("vinculo_vagas")
    private String vinculo;

    @SerializedName("ramo_vagas")
    private String ramo;

    @SerializedName("id_empresa")
    private int empresa_id;

    @SerializedName("nome_empresa")
    private String nome_empresa;

    @SerializedName("habilidades_desejaveis")
    private String habilidadesDesejaveisStr;

    // Construtores
    public Vagas() {}

    public Vagas(int vaga_id, String titulo, String descricao, String localizacao,
                 String salario, String requisitos, Long id_usuario, String nivel_experiencia, // CHANGED: Parameter type to 'Long'
                 String tipo_contrato, String area_atuacao, String beneficios,
                 String vinculo, String ramo, int empresa_id, String nome_empresa,
                 String habilidadesDesejaveisStr) {
        this.vaga_id = vaga_id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.salario = salario;
        this.requisitos = requisitos;
        this.id_usuario = id_usuario;
        this.nivel_experiencia = nivel_experiencia;
        this.tipo_contrato = tipo_contrato;
        this.area_atuacao = area_atuacao;
        this.beneficios = beneficios;
        this.vinculo = vinculo;
        this.ramo = ramo;
        this.empresa_id = empresa_id;
        this.nome_empresa = nome_empresa;
        this.habilidadesDesejaveisStr = habilidadesDesejaveisStr;
    }

    // Método adicionado para obter o ID do recrutador
    public Long getRecrutadorId() { // CHANGED: Return type to 'Long'
        return id_usuario;
    }

    // Getters e Setters
    public int getVaga_id() {
        return vaga_id;
    }

    public void setVaga_id(int vaga_id) {
        this.vaga_id = vaga_id;
    }

    public String getTitulo() {
        return titulo != null ? titulo : "Não informado";
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao != null ? descricao : "Não informado";
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocalizacao() {
        return localizacao != null ? localizacao : "Não informado";
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getSalario() {
        return salario != null ? salario : "Não informado";
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getRequisitos() {
        return requisitos != null ? requisitos : "Não informado";
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public Long getId_usuario() { // CHANGED: Return type to 'Long'
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) { // CHANGED: Parameter type to 'Long'
        this.id_usuario = id_usuario;
    }

    public String getNivel_experiencia() {
        return nivel_experiencia != null ? nivel_experiencia : "Não informado";
    }

    public void setNivel_experiencia(String nivel_experiencia) {
        this.nivel_experiencia = nivel_experiencia;
    }

    public String getTipo_contrato() {
        return tipo_contrato != null ? tipo_contrato : "Não informado";
    }

    public void setTipo_contrato(String tipo_contrato) {
        this.tipo_contrato = tipo_contrato;
    }

    public String getArea_atuacao() {
        return area_atuacao != null ? area_atuacao : "Não informado";
    }

    public void setArea_atuacao(String area_atuacao) {
        this.area_atuacao = area_atuacao;
    }

    public String getBeneficios() {
        return beneficios != null ? beneficios : "Não informado";
    }

    public void setBeneficios(String beneficios) {
        this.beneficios = beneficios;
    }

    public String getVinculo() {
        return vinculo != null ? vinculo : "Não informado";
    }

    public void setVinculo(String vinculo) {
        this.vinculo = vinculo;
    }

    public String getRamo() {
        return ramo != null ? ramo : "Não informado";
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public int getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(int empresa_id) {
        this.empresa_id = empresa_id;
    }

    public String getNome_empresa() {
        return nome_empresa != null ? nome_empresa : "Empresa não informada";
    }

    public String getNomeEmpresa() {
        return getNome_empresa();
    }

    public void setNome_empresa(String nome_empresa) {
        this.nome_empresa = nome_empresa;
    }

    public String getHabilidadesDesejaveisStr() {
        return habilidadesDesejaveisStr;
    }

    public void setHabilidadesDesejaveisStr(String habilidadesDesejaveisStr) {
        this.habilidadesDesejaveisStr = habilidadesDesejaveisStr;
    }

    // Métodos utilitários
    public List<String> getHabilidadesDesejaveis() {
        if (habilidadesDesejaveisStr != null && !habilidadesDesejaveisStr.trim().isEmpty()) {
            String[] arr = habilidadesDesejaveisStr.split(",");
            List<String> list = new ArrayList<>();
            for (String s : arr) {
                list.add(s.trim());
            }
            return list;
        }
        return new ArrayList<>();
    }

    public List<String> getBeneficiosLista() {
        List<String> beneficiosList = new ArrayList<>();
        if (beneficios != null && !beneficios.trim().isEmpty()) {
            String[] beneficiosArray = beneficios.split(",");
            for (String beneficio : beneficiosArray) {
                String trimmed = beneficio.trim();
                if (!trimmed.isEmpty()) {
                    beneficiosList.add(trimmed);
                }
            }
        }
        return beneficiosList;
    }

    public String getSalarioFormatado() {
        try {
            double valor = Double.parseDouble(salario.replaceAll("[^\\d.]", ""));
            return "R$ " + String.format("%,.2f", valor);
        } catch (NumberFormatException e) {
            return salario;
        }
    }

    public String getInfoResumida() {
        return String.format("%s | %s | %s",
                getLocalizacao(),
                getVinculo(),
                getSalarioFormatado());
    }

    public String getVagaIdAsString() {
        return String.valueOf(vaga_id);
    }

    public boolean isCompleta() {
        return titulo != null && !titulo.isEmpty() &&
                descricao != null && !descricao.isEmpty() &&
                localizacao != null && !localizacao.isEmpty() &&
                salario != null && !salario.isEmpty();
    }

    public boolean pertenceAoUsuario(long userId) {
        // You'll need to handle the Long comparison carefully here, as it can be null
        return this.id_usuario != null && this.id_usuario == userId;
    }

    @Override
    public String toString() {
        return "Vaga{" +
                "id=" + vaga_id +
                ", titulo='" + titulo + '\'' +
                ", empresa='" + nome_empresa + '\'' +
                ", localizacao='" + localizacao + '\'' +
                ", salario='" + salario + '\'' +
                ", id_usuario=" + id_usuario + // This will now show 'null' if it's null
                ", habilidades=" + habilidadesDesejaveisStr +
                '}';
    }
}
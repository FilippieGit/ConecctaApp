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
    private String vinculo;
    private String ramo;
    private int empresa_id;
    private String nome_empresa;
    private List<String> habilidadesDesejaveis;

    // Construtor completo
    public Vagas(int vaga_id, String titulo, String descricao, String localizacao,
                 String salario, String requisitos, String nivel_experiencia,
                 String tipo_contrato, String area_atuacao, String beneficios,
                 String vinculo, String ramo, int empresa_id, String nome_empresa,
                 List<String> habilidadesDesejaveis) {
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
        this.vinculo = vinculo;
        this.ramo = ramo;
        this.empresa_id = empresa_id;
        this.nome_empresa = nome_empresa;
        this.habilidadesDesejaveis = habilidadesDesejaveis;
    }

    // Construtor para criação de novas vagas
    public Vagas(String titulo, String descricao, String localizacao, String salario,
                 String requisitos, String nivel_experiencia, String tipo_contrato,
                 String area_atuacao, String beneficios, String vinculo, String ramo,
                 int empresa_id, List<String> habilidadesDesejaveis) {
        this(0, titulo, descricao, localizacao, salario, requisitos, nivel_experiencia,
                tipo_contrato, area_atuacao, beneficios, vinculo, ramo, empresa_id,
                null, habilidadesDesejaveis);
    }

    // Construtor para visualização
    public Vagas(int vaga_id, String titulo, String descricao, String localizacao,
                 String salario, String requisitos, String nivel_experiencia,
                 String tipo_contrato, String area_atuacao, String beneficios,
                 String vinculo, String ramo, int empresa_id, String nome_empresa) {
        this(vaga_id, titulo, descricao, localizacao, salario, requisitos, nivel_experiencia,
                tipo_contrato, area_atuacao, beneficios, vinculo, ramo, empresa_id,
                nome_empresa, null);
    }

    // Construtor vazio
    public Vagas() {}

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

    public void setNome_empresa(String nome_empresa) {
        this.nome_empresa = nome_empresa;
    }

    public List<String> getHabilidadesDesejaveis() {
        return habilidadesDesejaveis;
    }

    // Método de compatibilidade se necessário
    public String getHabilidades_desejaveis() {
        if (habilidadesDesejaveis != null && !habilidadesDesejaveis.isEmpty()) {
            return String.join(", ", habilidadesDesejaveis);
        }
        return "Não informado";
    }

    public void setHabilidadesDesejaveis(List<String> habilidadesDesejaveis) {
        this.habilidadesDesejaveis = habilidadesDesejaveis;
    }

    // Método para formatar salário (opcional)
    public String getSalarioFormatado() {
        try {
            double valor = Double.parseDouble(salario.replaceAll("[^\\d.]", ""));
            return "R$ " + String.format("%,.2f", valor);
        } catch (NumberFormatException e) {
            return salario;
        }
    }

    @Override
    public String toString() {
        return "Vaga{" +
                "titulo='" + titulo + '\'' +
                ", empresa='" + nome_empresa + '\'' +
                ", localizacao='" + localizacao + '\'' +
                ", salario='" + salario + '\'' +
                '}';
    }
}
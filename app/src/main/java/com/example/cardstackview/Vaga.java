package com.example.cardstackview;

import android.os.Parcel;
import android.os.Parcelable;

public class Vaga implements Parcelable {
    private String titulo;
    private String descricao;
    private String localizacao;
    private String salario;
    private String requisitos;

    public Vaga(String titulo, String descricao, String localizacao, String salario, String requisitos) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.salario = salario;
        this.requisitos = requisitos;
    }

    protected Vaga(Parcel in) {
        titulo = in.readString();
        descricao = in.readString();
        localizacao = in.readString();
        salario = in.readString();
        requisitos = in.readString();
    }

    public static final Creator<Vaga> CREATOR = new Creator<Vaga>() {
        @Override
        public Vaga createFromParcel(Parcel in) {
            return new Vaga(in);
        }

        @Override
        public Vaga[] newArray(int size) {
            return new Vaga[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titulo);
        parcel.writeString(descricao);
        parcel.writeString(localizacao);
        parcel.writeString(salario);
        parcel.writeString(requisitos);
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getLocalizacao() { return localizacao; }
    public String getSalario() { return salario; }
    public String getRequisitos() { return requisitos; }
}


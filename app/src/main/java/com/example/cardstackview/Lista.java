package com.example.cardstackview;

public class Lista {
    private String titulo;



    private int image;

    public Lista(String titulo, int image) {
        this.titulo = titulo;
        this.image = image;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}

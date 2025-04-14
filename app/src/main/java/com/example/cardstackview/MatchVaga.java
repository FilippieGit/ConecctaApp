// Arquivo: MatchVaga.java
package com.example.cardstackview;
public class MatchVaga {
    private String titulo;
    private int image;

    public MatchVaga(String titulo, int image) {
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
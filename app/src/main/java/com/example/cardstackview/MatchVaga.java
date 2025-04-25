// Arquivo: MatchVaga.java
package com.example.cardstackview;
public class MatchVaga {
    private String titulo;
    private String descricao; // novo campo
    private int image;

    public MatchVaga(String titulo, String descricao, int image) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.image = image;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getImage() {
        return image;
    }
}

package com.example.cardstackview;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String cargo;

    public Usuario(int id, String nome, String email, String cargo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCargo() { return cargo; }
}
package com.example.edutransporte;

public class Aluno {
    private String nome;
    private String destino;
    private boolean confirmado;
    private String horario;
    private boolean embarcou;  // atributo novo

    public Aluno(String nome, String destino, boolean confirmado, String horario) {
        this.nome = nome;
        this.destino = destino;
        this.confirmado = confirmado;
        this.horario = horario;
        this.embarcou = false;  // valor inicial padrão
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public boolean isEmbarcou() { return embarcou; }      // getter embarcou
    public void setEmbarcou(boolean embarcou) { this.embarcou = embarcou; }  // setter embarcou
}

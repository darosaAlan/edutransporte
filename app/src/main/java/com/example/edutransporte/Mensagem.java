// Classe Mensagem.java
package com.example.edutransporte;

public class Mensagem {
    private String remetente;
    private String texto;
    private String horario;
    private boolean isEnviada;

    public Mensagem(String remetente, String texto, String horario, boolean isEnviada) {
        this.remetente = remetente;
        this.texto = texto;
        this.horario = horario;
        this.isEnviada = isEnviada;
    }

    // Getters e Setters
    public String getRemetente() { return remetente; }
    public void setRemetente(String remetente) { this.remetente = remetente; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public boolean isEnviada() { return isEnviada; }
    public void setEnviada(boolean enviada) { isEnviada = enviada; }
}
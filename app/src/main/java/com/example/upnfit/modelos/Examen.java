package com.example.upnfit.modelos;

public class Examen {
    private String curso;
    private String tipo; // Parcial, Final, PC1, etc.
    private String fecha;
    private String hora;
    private String tema;

    public Examen(String curso, String tipo, String fecha, String hora, String tema) {
        this.curso = curso;
        this.tipo = tipo;
        this.fecha = fecha;
        this.hora = hora;
        this.tema = tema;
    }

    public String getCurso() { return curso; }
    public String getTipo() { return tipo; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getTema() { return tema; }
}

package com.example.upnfit.modelos;

public class Nota {
    private String curso;
    private double nota;
    private String unidad; // T1, Parcial, T2, Final

    public Nota(String curso, double nota, String unidad) {
        this.curso = curso;
        this.nota = nota;
        this.unidad = unidad;
    }

    public String getCurso() { return curso; }
    public double getNota() { return nota; }
    public String getUnidad() { return unidad; }
}

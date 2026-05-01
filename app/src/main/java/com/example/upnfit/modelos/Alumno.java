package com.example.upnfit.modelos;

public class Alumno {
    private String nombre;
    private boolean asistio;
    private double nota;

    public Alumno(String nombre) {
        this.nombre = nombre;
        this.asistio = true; // Valor por defecto
        this.nota = 0.0;
    }

    public String getNombre() { return nombre; }
    public boolean isAsistio() { return asistio; }
    public void setAsistio(boolean asistio) { this.asistio = asistio; }
    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }
}

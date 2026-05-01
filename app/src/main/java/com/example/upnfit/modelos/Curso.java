package com.example.upnfit.modelos;

public class Curso {
    private String nombre;
    private String profesor;
    private String horario;
    private int imagenResId;

    public Curso(String nombre, String profesor, String horario, int imagenResId) {
        this.nombre = nombre;
        this.profesor = profesor;
        this.horario = horario;
        this.imagenResId = imagenResId;
    }

    public String getNombre() { return nombre; }
    public String getProfesor() { return profesor; }
    public String getHorario() { return horario; }
    public int getImagenResId() { return imagenResId; }
}

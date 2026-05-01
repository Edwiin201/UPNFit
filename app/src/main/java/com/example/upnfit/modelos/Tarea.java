package com.example.upnfit.modelos;

public class Tarea {
    private int id;
    private String curso;
    private String descripcion;
    private String fechaEntrega;
    private String profesor;
    private boolean completada;

    public Tarea(int id, String curso, String descripcion, String fechaEntrega, String profesor, boolean completada) {
        this.id = id;
        this.curso = curso;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.profesor = profesor;
        this.completada = completada;
    }

    public int getId() { return id; }
    public String getCurso() { return curso; }
    public String getDescripcion() { return descripcion; }
    public String getFechaEntrega() { return fechaEntrega; }
    public String getProfesor() { return profesor; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}

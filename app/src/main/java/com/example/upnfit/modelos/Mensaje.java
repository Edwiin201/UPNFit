package com.example.upnfit.modelos;

public class Mensaje {
    private String remitente;
    private String contenido;
    private String fecha;
    private boolean esMio;

    public Mensaje(String remitente, String contenido, String fecha, boolean esMio) {
        this.remitente = remitente;
        this.contenido = contenido;
        this.fecha = fecha;
        this.esMio = esMio;
    }

    public String getRemitente() { return remitente; }
    public String getContenido() { return contenido; }
    public String getFecha() { return fecha; }
    public boolean isEsMio() { return esMio; }
}

package com.dan21az.bioedu.modelo.actividad;

import java.io.Serializable;

/**
 * Representa una actividad de carácter personal, la cual incluye el lugar donde se realiza.
 */
public class Personal extends Actividad implements Serializable {
    private String lugar;

    public Personal(String nombre, String categoria, String fechaVencimiento, String prioridad, String tipo, float tiempoEstimado, int progreso, String id, String estado, String descripcion, String lugar) {
        super(nombre, categoria, fechaVencimiento, prioridad, tipo, tiempoEstimado, progreso, id, estado, descripcion);
        this.lugar = lugar;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}

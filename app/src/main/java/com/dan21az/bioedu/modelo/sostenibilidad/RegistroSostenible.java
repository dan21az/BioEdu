package com.dan21az.bioedu.modelo.sostenibilidad;

import java.io.Serializable;
import java.util.ArrayList;

public class RegistroSostenible implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fechaIso; // yyyy-MM-dd
    private ArrayList<String> acciones;

    public RegistroSostenible(String fechaIso) {
        this.fechaIso = fechaIso;
        this.acciones = new ArrayList<>();
    }

    public String getFechaIso() { return fechaIso; }

    public ArrayList<String> getAcciones() { return acciones; }

    public void setAcciones(ArrayList<String> nuevas) {
        this.acciones = (nuevas != null) ? new ArrayList<>(nuevas) : new ArrayList<>();
    }

    public int getPuntos() { return acciones.size(); }
}


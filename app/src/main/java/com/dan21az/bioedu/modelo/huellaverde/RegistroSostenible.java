package com.dan21az.bioedu.modelo.huellaverde;

import java.io.Serializable;
import java.util.ArrayList;

public class RegistroSostenible implements Serializable {
    private String fecha; // yyyy-MM-dd
    private ArrayList<String> acciones;

    public RegistroSostenible(String fecha) {
        this.fecha = fecha;
        this.acciones = new ArrayList<>();
    }

    public String getFecha() { return fecha; }

    public ArrayList<String> getAcciones() { return acciones; }

    public void setAcciones(ArrayList<String> nuevas) {
        this.acciones = (nuevas != null) ? new ArrayList<>(nuevas) : new ArrayList<>();
    }

    public int getPuntos() { return acciones.size(); }
}


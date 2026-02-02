package com.dan21az.bioedu.modelo.hidratacion;


import java.io.Serializable;

public class RegistroHidratacion implements Serializable {

    private long fechaHora;
    private int cantidadAgua;

    public RegistroHidratacion(int cantidadAgua) {
        this.fechaHora = System.currentTimeMillis();
        this.cantidadAgua = cantidadAgua;
    }

    public RegistroHidratacion(long fechaHora, int cantidadAgua) {
        this.fechaHora = fechaHora;
        this.cantidadAgua = cantidadAgua;
    }

    public long getFechaHora() {
        return fechaHora;
    }

    public int getCantidadAgua() {
        return cantidadAgua;
    }

    public void setCantidadAgua(int cantidadAgua) {
        this.cantidadAgua = cantidadAgua;
    }
    //sobrescritura del metodo toString para mostrar la hora y la cantidad de agua ingerida
    @Override
    public String toString() {
        return "- " + fechaHora + ": " + cantidadAgua + " ml";
    }

}
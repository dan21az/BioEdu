package com.dan21az.bioedu.modelo.juegomemoria;

public class Carta {

    private int imagenId;
    private boolean descubierta;
    private boolean emparejada;

    public Carta(int imagenId) {
        this.imagenId = imagenId;
        this.descubierta = false;
        this.emparejada = false;
    }

    public int getImagenId() {
        return imagenId;
    }

    public boolean estaDescubierta() {
        return descubierta;
    }

    public boolean estaEmparejada() {
        return emparejada;
    }

    public void descubrir() {
        descubierta = true;
    }

    public void ocultar() {
        descubierta = false;
    }

    public void emparejar() {
        emparejada = true;
    }
}

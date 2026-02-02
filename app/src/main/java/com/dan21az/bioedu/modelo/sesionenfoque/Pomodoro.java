package com.dan21az.bioedu.modelo.sesionenfoque;


import com.dan21az.bioedu.modelo.actividad.Actividad;

import java.io.Serializable;

public class Pomodoro extends SesionEnfoque implements Serializable {
    private int ciclos, cicloActual;
    private long tiempoEnfoque;

    // --- Constructores ---

    // Constructor para el servicio de sesiones
    public Pomodoro(long duracionEnfoqueMs, String fecha, Actividad actividad, int ciclos, int descanso) {
        super(duracionEnfoqueMs, descanso, "Pomodoro", fecha, actividad);
        this.ciclos = ciclos;
        this.cicloActual = 1;
    }

    // Constructor para datos de prueba con 25 minutos
    public Pomodoro(String fecha, Actividad actividad, int ciclos ){
        super(25L* 60 * 1000L, 5, "Pomodoro", fecha, actividad);

        this.ciclos = ciclos;
        this.cicloActual = 1;
    }


    // --- Métodos Getter (Accesores) ---
    public int getCiclos() {
        return ciclos;
    }
    public int getCicloActual() {
        return cicloActual;
    }
    public long getTiempoEnfoque() {
        return tiempoEnfoque;
    }

    // --- Otros Métodos Setter
    public void setTiempoEnfoque(long duration) {
        this.tiempoEnfoque = duration;
    }

    public void setCiclos(int ciclos) {
        this.ciclos = ciclos;
    }

    public void setCicloActual(int cicloActual) {
        this.cicloActual = cicloActual;
    }


    // --- Métodos de Lógica

    public void avanzarCiclo(){
        if(cicloActual<ciclos){
            cicloActual++;
        }
    }
}
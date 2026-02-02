package com.dan21az.bioedu.modelo.sesionenfoque;

import com.dan21az.bioedu.modelo.actividad.Actividad;

import java.io.Serializable;

public class DeepWork extends SesionEnfoque implements Serializable {

    // --- Constructores ---

    //Constructor para datos de pruebas, por defecto 90 minutos
    public DeepWork(String fecha, Actividad actividad){
        super(90L* 60 * 1000L, 0, "DeepWork", fecha, actividad);
    }

    // Constructor para el servicio de las sesiones
    public DeepWork(long duracionEnfoqueMinutos, String fecha, Actividad actividad){
        super(duracionEnfoqueMinutos, 0, "DeepWork", fecha, actividad);
    }

}

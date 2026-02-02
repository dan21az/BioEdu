package com.dan21az.bioedu.modelo.actividad;

import com.dan21az.bioedu.modelo.sesionenfoque.SesionEnfoque;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representa una actividad académica, la cual incluye sesiones de enfoque y una asignatura asociada.
 */
public class Academica extends Actividad implements Serializable {

    private ArrayList<SesionEnfoque> sesiones;
    private String asignatura;

    public Academica(String nombre, String categoria, String fechaVencimiento, String prioridad, String tipo, float tiempoEstimado, int progreso, String id, String estado, String descripcion, String asignatura) {
        super(nombre, categoria, fechaVencimiento, prioridad, tipo, tiempoEstimado, progreso, id, estado, descripcion);
        this.asignatura = asignatura;
        this.sesiones = new ArrayList<>();
    }

    public ArrayList<SesionEnfoque> getSesiones() {
        return sesiones;
    }

    public void setSesiones(ArrayList<SesionEnfoque> sesiones) {
        this.sesiones = sesiones;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public void anadirSesion(SesionEnfoque sesion) {
        if (sesion != null) {
            this.sesiones.add(sesion);
        }
    }
}

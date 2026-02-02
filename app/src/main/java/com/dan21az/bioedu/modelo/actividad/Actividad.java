package com.dan21az.bioedu.modelo.actividad;

import java.io.Serializable;
import java.util.UUID;

/**
 * Representa una actividad genérica en el sistema.
 * Se utiliza UUID para identificar cada actividad de forma única.
 */
public class Actividad implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String categoria;
    private String fechaVencimiento;
    private String prioridad;
    private String tipo;
    private String estado;
    private String descripcion;
    private int progreso;
    private float tiempoEstimado;

    public Actividad(String nombre, String categoria, String fechaVencimiento, String prioridad, String tipo, float tiempoEstimado, int progreso, String id, String estado, String descripcion) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fechaVencimiento = fechaVencimiento;
        this.prioridad = prioridad;
        this.tipo = tipo;
        this.tiempoEstimado = tiempoEstimado;
        this.progreso = progreso;
        // Si el id es nulo o vacío, generamos uno nuevo. Esto es útil para la creación de nuevas actividades.
        this.id = (id == null || id.isEmpty()) ? UUID.randomUUID().toString() : id;
        this.estado = (estado == null || estado.isEmpty()) ? "No iniciado" : estado;
        this.descripcion = descripcion;
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public float getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(float tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    // --- Lógica ---

    /**
     * Actualiza el progreso y ajusta el estado automáticamente.
     */
    public void actualizarAvance(int nuevoProgreso) {
        if (nuevoProgreso >= 0 && nuevoProgreso <= 100) {
            this.progreso = nuevoProgreso;
            if (this.progreso == 100) {
                this.estado = "Completo";
            } else if (this.progreso == 0) {
                this.estado = "No iniciado";
            } else {
                this.estado = "En curso";
            }
        }
    }
}

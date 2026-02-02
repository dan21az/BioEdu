package com.dan21az.bioedu.modelo.hidratacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DiaHidratacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Calendar fecha;
    private List<RegistroHidratacion> registros;

    // Constructor
    public DiaHidratacion(Calendar fecha) {
        this.fecha = fecha;
        this.registros = new ArrayList<>();
    }

    // Getter de fecha
    public Calendar getFecha() {
        return fecha;
    }

    // Setter de fecha
    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    // Devuelve la lista de registros
    public List<RegistroHidratacion> getRegistros() {
        return registros;
    }

    // Agrega un registro de agua al día
    public void agregarRegistro(RegistroHidratacion registro) {
        registros.add(registro);
    }

    // Calcula el total de agua consumida en el día
    public int getTotalConsumido() {
        int total = 0;
        for (RegistroHidratacion r : registros) {
            total += r.getCantidadAgua();
        }
        return total;
    }

    // Indica si el día tiene registros
    public boolean tieneRegistros() {
        return !registros.isEmpty();
    }

    @Override
    public String toString() {
        return "Día: " + fecha + " | Total: " + getTotalConsumido() + " ml";
    }
}

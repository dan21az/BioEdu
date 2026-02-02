package com.dan21az.bioedu.modelo.hidratacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Modelo principal de Hidratación.
 * Maneja la meta diaria y la lista de días de hidratación.
 */
public class Hidratacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private int metaDiaria;
    private List<DiaHidratacion> dias;

    public Hidratacion() {
        metaDiaria = 2500;
        dias = new ArrayList<>();
    }

    /* =======================
       REGISTRO DE INGESTA
       ======================= */
    public void registrarIngesta(int cantidadMl, String horaManual, Calendar fechaSeleccionada) {

        DiaHidratacion dia = obtenerODiaCrear(fechaSeleccionada);

        Calendar c = (Calendar) fechaSeleccionada.clone();
        if (horaManual != null && horaManual.contains(":")) {
            String[] partes = horaManual.split(":");
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partes[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(partes[1]));
        }

        long timestamp = c.getTimeInMillis();
        dia.agregarRegistro(new RegistroHidratacion(timestamp, cantidadMl));
    }

    /* =======================
       META DIARIA
       ======================= */
    public void establecerMeta(int nuevaMeta) {
        metaDiaria = nuevaMeta;
    }

    public int getMetaDiaria() {
        return metaDiaria;
    }

    /* =======================
       CONSULTAS POR FECHA
       ======================= */
    public int getTotalPorDia(Calendar fecha) {
        DiaHidratacion dia = buscarDia(fecha);
        return (dia != null) ? dia.getTotalConsumido() : 0;
    }

    public List<RegistroHidratacion> getRegistrosPorDia(Calendar fecha) {
        DiaHidratacion dia = buscarDia(fecha);
        return (dia != null) ? dia.getRegistros() : new ArrayList<>();
    }

    /* =======================
       GESTIÓN DE DÍAS
       ======================= */
    private DiaHidratacion buscarDia(Calendar fecha) {
        for (DiaHidratacion d : dias) {
            if (mismoDia(d.getFecha(), fecha)) {
                return d;
            }
        }
        return null;
    }

    private DiaHidratacion obtenerODiaCrear(Calendar fecha) {
        DiaHidratacion dia = buscarDia(fecha);
        if (dia == null) {
            dia = new DiaHidratacion((Calendar) fecha.clone());
            dias.add(dia);
        }
        return dia;
    }

    private boolean mismoDia(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    /* =======================
       DATOS INICIALES (RÚBRICA)
       ======================= */
    public void cargarDatosIniciales() {

        Calendar dia19 = Calendar.getInstance();
        dia19.set(2026, Calendar.JANUARY, 19, 0, 0);

        DiaHidratacion d19 = new DiaHidratacion(dia19);

        Calendar h1 = (Calendar) dia19.clone();
        h1.set(Calendar.HOUR_OF_DAY, 10);
        h1.set(Calendar.MINUTE, 30);

        Calendar h2 = (Calendar) dia19.clone();
        h2.set(Calendar.HOUR_OF_DAY, 14);
        h2.set(Calendar.MINUTE, 0);

        d19.agregarRegistro(new RegistroHidratacion(h1.getTimeInMillis(), 250));
        d19.agregarRegistro(new RegistroHidratacion(h2.getTimeInMillis(), 300));

        dias.add(d19);
    }

    public List<DiaHidratacion> getDias() {
        return dias;
    }
}

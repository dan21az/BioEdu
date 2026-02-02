package com.dan21az.bioedu.datos;

import android.content.Context;
import android.util.Log;

import com.dan21az.bioedu.modelo.actividad.Actividad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ActividadesDatos implements Serializable {

    private static final ActividadesDatos INSTANCIA = new ActividadesDatos();
    private ArrayList<Actividad> listaActividades;
    private static final String NOMBRE_ARCHIVO = "actividades.ser";
    private File directorioArchivos;

    private ActividadesDatos() {
        listaActividades = new ArrayList<>();
    }

    public static ActividadesDatos getInstancia() {
        return INSTANCIA;
    }

    public List<Actividad> getListaActividades() {
        return new ArrayList<>(listaActividades);
    }

    public void inicializar(Context context) {
        if (context != null) {
            this.directorioArchivos = context.getFilesDir();
            cargarDatos();
        }
    }

    @SuppressWarnings("unchecked")
    public void cargarDatos() {
        if (directorioArchivos == null) return;
        
        File f = new File(directorioArchivos, NOMBRE_ARCHIVO);
        if (f.exists()) {
            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f))) {
                listaActividades = (ArrayList<Actividad>) is.readObject();
            } catch (Exception e) {
                Log.e("ActividadesDatos", "Error al cargar actividades serializadas", e);
            }
        }
    }

    public boolean guardarActividades() {
        if (directorioArchivos == null) return false;

        File f = new File(directorioArchivos, NOMBRE_ARCHIVO);
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f))) {
            os.writeObject(listaActividades);
            return true;
        } catch (Exception e) {
            Log.e("ActividadesDatos", "Error al guardar actividades", e);
            return false;
        }
    }

    public void agregarActividad(Actividad nuevaActividad) {
        if (nuevaActividad != null) {
            listaActividades.add(nuevaActividad);
            guardarActividades();
        }
    }

    public boolean actualizarProgreso(String idActividad, int nuevoAvance) {
        Actividad actividad = buscarActividadPorId(idActividad);
        if (actividad != null) {
            actividad.actualizarAvance(nuevoAvance);
            return guardarActividades();
        }
        return false;
    }

    public boolean eliminarActividad(String idActividad) {
        boolean eliminada = listaActividades.removeIf(a -> a.getId().equals(idActividad));
        if (eliminada) {
            guardarActividades();
        }
        return eliminada;
    }

    public Actividad buscarActividadPorId(String id) {
        if (id == null) return null;
        for (Actividad actividad : listaActividades) {
            if (id.equals(actividad.getId())) {
                return actividad;
            }
        }
        return null;
    }

    public List<Actividad> filtrarPorTipo(String filtro) {
        if (filtro == null || "Todos".equalsIgnoreCase(filtro)) {
            return new ArrayList<>(listaActividades);
        }

        List<Actividad> listaFiltrada = new ArrayList<>();
        String filtroUpper = filtro.toUpperCase();

        for (Actividad actividad : listaActividades) {
            if (actividad.getTipo() != null && actividad.getTipo().toUpperCase().equals(filtroUpper)) {
                listaFiltrada.add(actividad);
            }
        }
        return listaFiltrada;
    }

    public List<Actividad> filtrarNoVencidas(List<Actividad> lista) {
        if (lista == null) return new ArrayList<>();
        List<Actividad> resultado = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date hoy = new Date();

        for (Actividad actividad : lista) {
            try {
                Date vencimiento = sdf.parse(actividad.getFechaVencimiento());
                if (vencimiento != null && !vencimiento.before(hoy)) {
                    resultado.add(actividad);
                }
            } catch (Exception e) {
                // Si la fecha es inválida, se incluye por seguridad o se maneja el error
                resultado.add(actividad);
            }
        }
        return resultado;
    }

    public List<Actividad> ordenarLista(List<Actividad> lista, String criterio) {
        if (lista == null) return new ArrayList<>();
        List<Actividad> copia = new ArrayList<>(lista);

        switch (criterio) {
            case "Nombre A-Z":
                copia.sort(Comparator.comparing(a -> a.getNombre() != null ? a.getNombre() : "", String.CASE_INSENSITIVE_ORDER));
                break;
            case "Fecha (desc)":
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                copia.sort((a1, a2) -> {
                    try {
                        Date d1 = sdf.parse(a1.getFechaVencimiento());
                        Date d2 = sdf.parse(a2.getFechaVencimiento());
                        if (d1 == null || d2 == null) return 0;
                        return d2.compareTo(d1);
                    } catch (Exception e) {
                        return 0;
                    }
                });
                break;
            case "Avance (desc)":
                copia.sort((a1, a2) -> Integer.compare(a2.getProgreso(), a1.getProgreso()));
                break;
        }
        return copia;
    }

    public boolean actualizarActividad(Actividad actividadActualizada) {
        if (actividadActualizada == null) return false;
        for (int i = 0; i < listaActividades.size(); i++) {
            if (listaActividades.get(i).getId().equals(actividadActualizada.getId())) {
                listaActividades.set(i, actividadActualizada);
                return guardarActividades();
            }
        }
        return false;
    }

    public void completarTodasLasActividades() {
        for (Actividad actividad : listaActividades) {
            actividad.setProgreso(100);
            actividad.setEstado("Completo");
        }
        guardarActividades();
    }

    public int eliminarActividadesCompletadas() {
        int sizeBefore = listaActividades.size();
        listaActividades.removeIf(actividad -> actividad.getProgreso() >= 100);
        int removedCount = sizeBefore - listaActividades.size();
        if (removedCount > 0) {
            guardarActividades();
        }
        return removedCount;
    }

    public void eliminarTodasLasActividades() {
        listaActividades.clear();
        guardarActividades();
    }
}

package com.dan21az.bioedu.datos;

import android.content.Context;
import com.dan21az.bioedu.modelo.sostenibilidad.RegistroSostenible;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SostenibilidadDatos {

    private static final String FILE_NAME = "sostenibilidad_v2.ser";
    private static SostenibilidadDatos instance;

    // El "Modelo" real es esta lista de registros
    private ArrayList<RegistroSostenible> registros;

    public static SostenibilidadDatos getInstance(Context context) {
        if (instance == null) instance = new SostenibilidadDatos(context);
        return instance;
    }

    private SostenibilidadDatos(Context context) {
        registros = leerArchivo(context);
        if (registros == null) {
            registros = new ArrayList<>();
            // Opcional: podrías meter datos de prueba aquí si es la primera vez
            // datosPrueba(); Pronto
        }
    }

    // --- LÓGICA DE NEGOCIO  ---

    public ArrayList<String> cargarAcciones(String fechaIso) {
        for (RegistroSostenible r : registros) {
            if (r.getFechaIso().equals(fechaIso)) {
                return new ArrayList<>(r.getAcciones());
            }
        }
        return new ArrayList<>(); // Retorna lista vacía si no hay registro
    }

    public void guardarAcciones(String fechaIso, ArrayList<String> acciones, Context context) {
        RegistroSostenible encontrado = null;
        for (RegistroSostenible r : registros) {
            if (r.getFechaIso().equals(fechaIso)) {
                encontrado = r;
                break;
            }
        }

        if (encontrado == null) {
            encontrado = new RegistroSostenible(fechaIso);
            registros.add(encontrado);
        }

        encontrado.setAcciones(acciones);
        guardarArchivo(context); // Persistencia inmediata
    }

    public List<String> obtenerTodasLasFechasRegistradas() {
        List<String> fechas = new ArrayList<>();
        if (registros != null) {
            for (RegistroSostenible r : registros) {
                fechas.add(r.getFechaIso());
            }
        }
        return fechas;
    }

    // --- LÓGICA DE PERSISTENCIA (.SER) ---

    private void guardarArchivo(Context context) {
        try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE))) {
            oos.writeObject(registros);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<RegistroSostenible> leerArchivo(Context context) {
        try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(FILE_NAME))) {
            return (ArrayList<RegistroSostenible>) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}


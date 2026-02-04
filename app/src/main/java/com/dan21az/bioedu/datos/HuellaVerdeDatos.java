package com.dan21az.bioedu.datos;

import android.content.Context;
import com.dan21az.bioedu.modelo.huellaverde.RegistroSostenible;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class HuellaVerdeDatos {

    private static final String FILE_NAME = "sostenibilidad_v3.ser"; // Nueva versión para evitar conflictos
    private static HuellaVerdeDatos instance;

    private HashMap<String, RegistroSostenible> registrosMap;

    public static synchronized HuellaVerdeDatos getInstance(Context context) {
        if (instance == null) {
            instance = new HuellaVerdeDatos(context.getApplicationContext());
        }
        return instance;
    }

    private HuellaVerdeDatos(Context context) {
        registrosMap = leerArchivo(context);
        if (registrosMap == null) {
            registrosMap = new HashMap<>();
        }
    }

    public HashMap<String, RegistroSostenible> getRegistrosMap() {
        return registrosMap;
    }

    // --- LÓGICA DE NEGOCIO ---

    // Lectura segura mediante una copia
    // Se usa cuando vamos a recorrer (iterar) el mapa en segundo plano.
    public synchronized HashMap<String, RegistroSostenible> getRegistrosMapCopy() {
        // Retornamos una nueva instancia con los mismos datos actuales
        return new HashMap<>(registrosMap);
    }

    // 3. CARGAR ACCIONES
    public synchronized ArrayList<String> cargarAcciones(String fecha) {
        RegistroSostenible r = registrosMap.get(fecha);
        return (r != null) ? new ArrayList<>(r.getAcciones()) : new ArrayList<>();
    }

    // 4. GUARDAR ACCIONES
    public synchronized void guardarAcciones(String fecha, ArrayList<String> acciones, Context context) {
        RegistroSostenible registro = registrosMap.get(fecha);
        if (registro == null) {
            registro = new RegistroSostenible(fecha);
            registrosMap.put(fecha, registro);
        }
        registro.setAcciones(acciones);
        guardarArchivo(context);
    }

    // --- PERSISTENCIA OPTIMIZADA ---

    private synchronized void guardarArchivo(Context context) {
        try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE))) {
            oos.writeObject(registrosMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, RegistroSostenible> leerArchivo(Context context) {
        try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(FILE_NAME))) {
            return (HashMap<String, RegistroSostenible>) ois.readObject();
        } catch (Exception e) {
            // Si el archivo no existe o hay error, devolvemos un mapa vacío
            return new HashMap<>();
        }
    }
}


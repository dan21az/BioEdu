package com.dan21az.bioedu.datos;

import android.content.Context;
import android.content.SharedPreferences;

public class MenuDatos {
    private static final String PREFS_NAME = "BioEduPrefs";
    private static final String KEY_PUNTOS_SEMANA = "puntos_semanales";

    // Para que el Menú Principal lea los puntos sin cargar nada más
    public static int obtenerPuntosCache(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_PUNTOS_SEMANA, 0);
    }

    // Para que la Controladora guarde el resultado final
    public static void guardarPuntosCache(Context context, int puntos) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_PUNTOS_SEMANA, puntos)
                .apply(); // .apply() es asíncrono, no traba la UI
    }
}

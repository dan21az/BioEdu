package com.dan21az.bioedu.vista.sostenibilidad;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.dan21az.bioedu.controlador.SostenibilidadControladora;
import com.dan21az.bioedu.datos.SostenibilidadDatos;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;

public class RegistroBioindicadoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No usamos setContentView para que sea ultra rápida e invisible
        mostrarDialogo();
    }

    private void mostrarDialogo() {
        SostenibilidadControladora ctrl = SostenibilidadControladora.getInstance();
        String hoy = ctrl.obtenerFechaActual();
        ArrayList<String> opciones = ctrl.getAccionesDisponibles();
        String[] items = opciones.toArray(new String[0]);

        // Cargar estado previo
        ArrayList<String> guardadas = SostenibilidadDatos.getInstance(this).cargarAcciones(hoy);
        boolean[] checkedItems = new boolean[items.length];
        ArrayList<String> seleccionActual = new ArrayList<>(guardadas);

        for (int i = 0; i < items.length; i++) {
            if (guardadas.contains(items[i])) checkedItems[i] = true;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Bioindicadores de Hoy")
                .setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) seleccionActual.add(items[which]);
                    else seleccionActual.remove(items[which]);
                })
                .setPositiveButton("Guardar", (dialog, which) -> {
                    SostenibilidadDatos.getInstance(this).guardarAcciones(hoy, seleccionActual, this);
                    terminar();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> terminar())
                .setOnCancelListener(dialog -> terminar())
                .show();
    }

    private void terminar() {
        finish();
        // Evita animaciones bruscas al cerrar
        overridePendingTransition(0, 0);
    }
}
package com.dan21az.bioedu.vista.huellaverde;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.dan21az.bioedu.controlador.HuellaVerdeControladora;
import com.dan21az.bioedu.datos.HuellaVerdeDatos;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;

public class RegistroAccionesDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostrarDialogo();
    }

    private void mostrarDialogo() {
        HuellaVerdeControladora ctrl = HuellaVerdeControladora.getInstance();
        String hoy = ctrl.obtenerFechaActual();
        ArrayList<String> opciones = ctrl.getAccionesDisponibles();
        String[] items = opciones.toArray(new String[0]);

        // Cargar estado previo
        ArrayList<String> guardadas = HuellaVerdeDatos.getInstance(this).cargarAcciones(hoy);
        boolean[] checkedItems = new boolean[items.length];
        ArrayList<String> seleccionActual = new ArrayList<>(guardadas);

        for (int i = 0; i < items.length; i++) {
            if (guardadas.contains(items[i])) checkedItems[i] = true;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("EcoAcciones de Hoy")
                .setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) seleccionActual.add(items[which]);
                    else seleccionActual.remove(items[which]);
                })
                .setPositiveButton("Guardar", (dialog, which) -> {
                    ctrl.guardarAccionesActuales(this, hoy, seleccionActual);

                    terminar();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> terminar())
                .setOnCancelListener(dialog -> terminar())
                .show();
    }

    private void terminar() {
        finish();
        overridePendingTransition(0, 0);
    }
}
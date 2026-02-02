package com.dan21az.bioedu.vista.actividad;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.ActividadesDatos;
import com.dan21az.bioedu.modelo.actividad.Actividad;

public class RegistrarAvance extends AppCompatActivity {

    public static final String EXTRA_ID_ACTIVIDAD = "com.dan21az.bioedu.ID_ACTIVIDAD";
    private EditText etIdActividad, etNombreActividad, etAvanceActual;
    private TextInputLayout tilNuevoAvance;
    private Button btnGuardar, btnCancelar;

    // Datos de la actividad
    private String idActividad;
    private Actividad actividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_registrar_avance);

        ActividadesDatos.getInstancia().inicializar(this);
        inicializarVistas();

        // Obtener el ID de la actividad desde el Intent
        Intent intent = getIntent();
        idActividad = intent.getStringExtra(EXTRA_ID_ACTIVIDAD);
        
        if (idActividad == null) {
            Toast.makeText(this, "Error: ID de actividad no proporcionado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        actividad = ActividadesDatos.getInstancia().buscarActividadPorId(idActividad);
        
        if (actividad == null) {
            Toast.makeText(this, "Error: Actividad no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarDatosActividad();

        // Configurar botones
        btnGuardar.setOnClickListener(v -> intentarGuardarAvance());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void inicializarVistas() {
        etIdActividad = findViewById(R.id.et_id_actividad);
        etNombreActividad = findViewById(R.id.et_nombre_actividad);
        etAvanceActual = findViewById(R.id.et_avance_actual);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnCancelar = findViewById(R.id.btn_cancelar);
        tilNuevoAvance = findViewById(R.id.et_nuevo_avance);
        
        // Deshabilitar edición de campos informativos
        etIdActividad.setFocusable(false);
        etNombreActividad.setFocusable(false);
        etAvanceActual.setFocusable(false);
    }

    private void cargarDatosActividad() {
        etIdActividad.setText(actividad.getId());
        etNombreActividad.setText(actividad.getNombre());
        etAvanceActual.setText(String.format(java.util.Locale.getDefault(), "%d%%", actividad.getProgreso()));
    }

    private void intentarGuardarAvance() {
        if (tilNuevoAvance.getEditText() == null) return;
        
        String nuevoAvanceStr = tilNuevoAvance.getEditText().getText().toString().trim();

        tilNuevoAvance.setError(null);

        if (TextUtils.isEmpty(nuevoAvanceStr)) {
            tilNuevoAvance.setError("Ingrese un valor para el nuevo avance.");
            return;
        }

        int nuevoAvance;
        try {
            nuevoAvance = Integer.parseInt(nuevoAvanceStr);
        } catch (NumberFormatException e) {
            tilNuevoAvance.setError("El valor debe ser un número entero válido.");
            return;
        }

        if (nuevoAvance < 0 || nuevoAvance > 100) {
            tilNuevoAvance.setError("El avance debe estar entre 0 y 100.");
            return;
        }

        if (nuevoAvance <= actividad.getProgreso()) {
            String mensajeError = String.format(java.util.Locale.getDefault(), 
                "El nuevo avance (%d%%) debe ser mayor al actual (%d%%).", 
                nuevoAvance, actividad.getProgreso());
            tilNuevoAvance.setError(mensajeError);
            return;
        }

        confirmarYGuardarAvance(nuevoAvance);
    }

    private void confirmarYGuardarAvance(int nuevoAvance) {
        String mensaje = String.format(java.util.Locale.getDefault(), 
            "¿Está seguro de registrar el avance al %d%%?", nuevoAvance);
            
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmar Avance")
                .setMessage(mensaje)
                .setPositiveButton("Sí", (dialog, which) -> {
                    boolean exito = ActividadesDatos.getInstancia().actualizarProgreso(idActividad, nuevoAvance);
                    if (exito) {
                        Toast.makeText(this, "Avance registrado con éxito.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error al actualizar la actividad.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}

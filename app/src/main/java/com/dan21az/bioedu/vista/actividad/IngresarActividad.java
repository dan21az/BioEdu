package com.dan21az.bioedu.vista.actividad;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.ActividadesDatos;
import com.dan21az.bioedu.modelo.actividad.Academica;
import com.dan21az.bioedu.modelo.actividad.Actividad;
import com.dan21az.bioedu.modelo.actividad.Personal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class IngresarActividad extends AppCompatActivity {
    private Spinner spinnerCategoria, spinnerPrioridad, spinnerTipo;
    private TextInputLayout tilNombre, tilDescripcion, tilTiempoEstimado, tilFechaHora, tilAsignatura, tilLugar;
    private TextInputEditText etNombre, etDescripcion, etTiempoEstimado, etAsignatura, etLugar;
    private AutoCompleteTextView etFechaHora;
    private Button btnGuardar, btnCancelar;
    private TextView tvTipo, tvAsignatura, tvLugar;
    private final Calendar selectedDateTimeCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_ingresar_actividad);

        // Inicializar repositorio
        ActividadesDatos.getInstancia().inicializar(this);

        inicializarVistas();
        configurarSpinners();

        // Configurar Selector de Fecha/Hora
        etFechaHora.setOnClickListener(v -> selectorFecha());

        // Configurar Botones
        btnGuardar.setOnClickListener(v -> guardarActividad());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void inicializarVistas() {
        btnGuardar = findViewById(R.id.btn_guardar);
        btnCancelar = findViewById(R.id.btn_cancelar);
        tilNombre = findViewById(R.id.til_nombre);
        tilDescripcion = findViewById(R.id.til_descripcion);
        tilFechaHora = findViewById(R.id.til_fecha_hora);
        tilTiempoEstimado = findViewById(R.id.til_tiempo_estimado);
        tilAsignatura = findViewById(R.id.til_asignatura);
        tilLugar = findViewById(R.id.til_lugar);

        spinnerCategoria = findViewById(R.id.spinner_categoria);
        spinnerPrioridad = findViewById(R.id.spinner_prioridad);
        spinnerTipo = findViewById(R.id.spinner_tipo);

        etNombre = (TextInputEditText) tilNombre.getEditText();
        etDescripcion = (TextInputEditText) tilDescripcion.getEditText();
        etTiempoEstimado = (TextInputEditText) tilTiempoEstimado.getEditText();
        etFechaHora = (AutoCompleteTextView) tilFechaHora.getEditText();
        etAsignatura = (TextInputEditText) tilAsignatura.getEditText();
        etLugar = (TextInputEditText) tilLugar.getEditText();

        tvTipo = findViewById(R.id.tv_tipo);
        tvAsignatura = findViewById(R.id.tv_asignatura);
        tvLugar = findViewById(R.id.tv_lugar);
    }

    private void configurarSpinners() {
        String[] categoriasPrincipales = {"Personal", "Académica"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoriasPrincipales);
        spinnerCategoria.setAdapter(catAdapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSeleccionada = (String) parent.getItemAtPosition(position);
                actualizarInterfazPorCategoria(categoriaSeleccionada);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        String[] prioridades = {"Alta", "Media", "Baja"};
        ArrayAdapter<String> prioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, prioridades);
        spinnerPrioridad.setAdapter(prioAdapter);
    }

    private void actualizarInterfazPorCategoria(String categoria) {
        String[] tiposDetallados;
        if ("Académica".equals(categoria)) {
            tvTipo.setVisibility(View.VISIBLE);
            spinnerTipo.setVisibility(View.VISIBLE);
            tvAsignatura.setVisibility(View.VISIBLE);
            tilAsignatura.setVisibility(View.VISIBLE);
            tiposDetallados = new String[] {"Tareas", "Examen", "Proyecto"};
            tvLugar.setVisibility(View.GONE);
            tilLugar.setVisibility(View.GONE);
        } else {
            tvAsignatura.setVisibility(View.GONE);
            tilAsignatura.setVisibility(View.GONE);
            tvLugar.setVisibility(View.VISIBLE);
            tilLugar.setVisibility(View.VISIBLE);
            tvTipo.setVisibility(View.VISIBLE);
            spinnerTipo.setVisibility(View.VISIBLE);
            tiposDetallados = new String[] {"Citas", "Ejercicio", "Hobbies"};
        }

        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposDetallados);
        spinnerTipo.setAdapter(tipoAdapter);
    }

    private void selectorFecha() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar Fecha")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // MaterialDatePicker returns selection in UTC at midnight.
            // We use UTC Calendar to extract correct year, month and day to avoid time zone shifts.
            Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            utcCalendar.setTimeInMillis(selection);
            
            selectedDateTimeCalendar.set(Calendar.YEAR, utcCalendar.get(Calendar.YEAR));
            selectedDateTimeCalendar.set(Calendar.MONTH, utcCalendar.get(Calendar.MONTH));
            selectedDateTimeCalendar.set(Calendar.DAY_OF_MONTH, utcCalendar.get(Calendar.DAY_OF_MONTH));

            selectorHora();
        });
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
    }

    private void selectorHora() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(selectedDateTimeCalendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(selectedDateTimeCalendar.get(Calendar.MINUTE))
                .setTitleText("Seleccionar Hora")
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            selectedDateTimeCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            selectedDateTimeCalendar.set(Calendar.MINUTE, timePicker.getMinute());
            actualizarFechaHora(selectedDateTimeCalendar);
        });
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER_TAG");
    }

    private void actualizarFechaHora(Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        etFechaHora.setText(sdf.format(c.getTime()));
    }

    private void guardarActividad() {
        tilNombre.setError(null);
        tilDescripcion.setError(null);
        tilFechaHora.setError(null);
        tilTiempoEstimado.setError(null);
        tilAsignatura.setError(null);
        tilLugar.setError(null);

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String prioridad = spinnerPrioridad.getSelectedItem().toString();
        String categoriaPrincipal = spinnerCategoria.getSelectedItem().toString();
        String tipoDetallado = spinnerTipo.getSelectedItem().toString();
        String fechaHoraVencimiento = etFechaHora.getText().toString().trim();
        String tiempoEstimadoStr = etTiempoEstimado.getText().toString().trim();
        
        boolean hayError = false;

        if (nombre.isEmpty()) { tilNombre.setError("El nombre es obligatorio."); hayError = true; }
        if (descripcion.isEmpty()) { tilDescripcion.setError("La descripción es obligatoria"); hayError = true; }
        if (fechaHoraVencimiento.isEmpty()) { tilFechaHora.setError("La fecha es obligatoria"); hayError = true; }
        if (tiempoEstimadoStr.isEmpty()) { tilTiempoEstimado.setError("El tiempo estimado es obligatorio."); hayError = true; }

        String asignatura = "", lugar = "";
        if ("Académica".equals(categoriaPrincipal)) {
            asignatura = etAsignatura.getText().toString().trim();
            if (asignatura.isEmpty()) { tilAsignatura.setError("La Asignatura es obligatoria."); hayError = true; }
        } else {
            lugar = etLugar.getText().toString().trim();
            if (lugar.isEmpty()) { tilLugar.setError("El lugar es obligatorio"); hayError = true; }
        }

        if (hayError) return;

        try {
            float tiempoEstimadoHoras = Float.parseFloat(tiempoEstimadoStr) / 60;
            String nuevoId = UUID.randomUUID().toString();
            Actividad nuevaActividad;

            if ("Académica".equals(categoriaPrincipal)) {
                nuevaActividad = new Academica(nombre, "ACADEMICA", fechaHoraVencimiento, prioridad, tipoDetallado, 
                                            tiempoEstimadoHoras, 0, nuevoId, "No iniciado", descripcion, asignatura);
            } else {
                nuevaActividad = new Personal(nombre, "PERSONAL", fechaHoraVencimiento, prioridad, tipoDetallado, 
                                            tiempoEstimadoHoras, 0, nuevoId, "No iniciado", descripcion, lugar);
            }

            ActividadesDatos.getInstancia().agregarActividad(nuevaActividad);
            Toast.makeText(this, "Actividad guardada con éxito", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            tilTiempoEstimado.setError("Ingrese un número válido.");
        }
    }
}

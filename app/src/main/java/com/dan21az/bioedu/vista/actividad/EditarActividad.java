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

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.ActividadesDatos;
import com.dan21az.bioedu.modelo.actividad.Academica;
import com.dan21az.bioedu.modelo.actividad.Actividad;
import com.dan21az.bioedu.modelo.actividad.Personal;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Actividad para editar los detalles de una actividad existente.
 * Se han aplicado correcciones en la lógica de selección de Spinners, validación de datos
 * y corrección del desfase horario en el MaterialDatePicker.
 */
public class EditarActividad extends AppCompatActivity {

    private Spinner spinnerCategoria, spinnerPrioridad, spinnerTipo;
    private TextInputLayout tilNombre, tilDescripcion, tilTiempoEstimado, tilFechaHora, tilAsignatura, tilLugar;
    private TextInputEditText etNombre, etDescripcion, etTiempoEstimado, etAsignatura, etLugar;
    private AutoCompleteTextView etFechaHora;
    private Button btnGuardar, btnCancelar;
    private TextView tvTitulo, tvTipo, tvAsignatura, tvLugar;

    private String actividadId;
    private Actividad actividadAEditar;
    private final Calendar selectedDateTimeCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_ingresar_actividad);

        inicializarVistas();
        configurarSpinners();

        // Obtener ID de la actividad a editar
        actividadId = getIntent().getStringExtra(DetalleActividad.EXTRA_ACTIVIDAD_ID);
        ActividadesDatos.getInstancia().inicializar(this);
        actividadAEditar = ActividadesDatos.getInstancia().buscarActividadPorId(actividadId);

        if (actividadAEditar != null) {
            cargarDatosEnInterfaz();
        } else {
            Toast.makeText(this, "Error: Actividad no encontrada", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar listeners
        etFechaHora.setOnClickListener(v -> selectorFecha());
        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void inicializarVistas() {
        tvTitulo = findViewById(R.id.tv_titulo);
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
        String[] categorias = {"Personal", "Académica"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(catAdapter);

        String[] prioridades = {"Alta", "Media", "Baja"};
        ArrayAdapter<String> prioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, prioridades);
        spinnerPrioridad.setAdapter(prioAdapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarInterfazPorCategoria(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarDatosEnInterfaz() {
        tvTitulo.setText("Editar Actividad");
        btnGuardar.setText("Actualizar");

        etNombre.setText(actividadAEditar.getNombre());
        etDescripcion.setText(actividadAEditar.getDescripcion());
        etFechaHora.setText(actividadAEditar.getFechaVencimiento());

        int minutos = Math.round(actividadAEditar.getTiempoEstimado() * 60);
        etTiempoEstimado.setText(String.valueOf(minutos));

        seleccionarEnSpinner(spinnerPrioridad, actividadAEditar.getPrioridad());

        if (actividadAEditar instanceof Academica) {
            seleccionarEnSpinner(spinnerCategoria, "Académica");
            actualizarInterfazPorCategoria("Académica");
            etAsignatura.setText(((Academica) actividadAEditar).getAsignatura());
            seleccionarEnSpinner(spinnerTipo, actividadAEditar.getTipo());
        } else if (actividadAEditar instanceof Personal) {
            seleccionarEnSpinner(spinnerCategoria, "Personal");
            actualizarInterfazPorCategoria("Personal");
            etLugar.setText(((Personal) actividadAEditar).getLugar());
            seleccionarEnSpinner(spinnerTipo, actividadAEditar.getTipo());
        }

        spinnerCategoria.setEnabled(false);
    }

    private void guardarCambios() {
        tilNombre.setError(null);
        tilTiempoEstimado.setError(null);
        tilAsignatura.setError(null);
        tilLugar.setError(null);

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String tiempoStr = etTiempoEstimado.getText().toString().trim();
        String fechaHora = etFechaHora.getText().toString().trim();
        String prioridad = spinnerPrioridad.getSelectedItem().toString();
        String tipo = spinnerTipo.getSelectedItem().toString();

        boolean hayError = false;

        if (nombre.isEmpty()) {
            tilNombre.setError("El nombre es obligatorio");
            hayError = true;
        }
        if (tiempoStr.isEmpty()) {
            tilTiempoEstimado.setError("El tiempo es obligatorio");
            hayError = true;
        }

        String asignatura = "", lugar = "";
        if (actividadAEditar instanceof Academica) {
            asignatura = etAsignatura.getText().toString().trim();
            if (asignatura.isEmpty()) {
                tilAsignatura.setError("La asignatura es obligatoria");
                hayError = true;
            }
        } else {
            lugar = etLugar.getText().toString().trim();
            if (lugar.isEmpty()) {
                tilLugar.setError("El lugar es obligatorio");
                hayError = true;
            }
        }

        if (hayError) return;

        try {
            float tiempoHoras = Float.parseFloat(tiempoStr) / 60;
            Actividad actualizada;

            if (actividadAEditar instanceof Academica) {
                actualizada = new Academica(
                        nombre, "ACADEMICA", fechaHora,
                        prioridad, tipo, tiempoHoras, 
                        actividadAEditar.getProgreso(), actividadId,
                        actividadAEditar.getEstado(), descripcion, asignatura
                );
                ((Academica) actualizada).setSesiones(((Academica) actividadAEditar).getSesiones());
            } else {
                actualizada = new Personal(
                        nombre, "PERSONAL", fechaHora,
                        prioridad, tipo, tiempoHoras, 
                        actividadAEditar.getProgreso(), actividadId,
                        actividadAEditar.getEstado(), descripcion, lugar
                );
            }

            if (ActividadesDatos.getInstancia().actualizarActividad(actualizada)) {
                Toast.makeText(this, "Actividad actualizada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            tilTiempoEstimado.setError("Ingrese un valor numérico válido");
        }
    }

    private void seleccionarEnSpinner(Spinner spinner, String valor) {
        if (spinner == null || valor == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            Object item = spinner.getItemAtPosition(i);
            if (item != null && item.toString().equalsIgnoreCase(valor)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void actualizarInterfazPorCategoria(String categoria) {
        String[] tipos;
        if ("Académica".equals(categoria)) {
            tvAsignatura.setVisibility(View.VISIBLE);
            tilAsignatura.setVisibility(View.VISIBLE);
            tvLugar.setVisibility(View.GONE);
            tilLugar.setVisibility(View.GONE);
            tipos = new String[]{"Tareas", "Examen", "Proyecto"};
        } else {
            tvAsignatura.setVisibility(View.GONE);
            tilAsignatura.setVisibility(View.GONE);
            tvLugar.setVisibility(View.VISIBLE);
            tilLugar.setVisibility(View.VISIBLE);
            tipos = new String[]{"Citas", "Ejercicio", "Hobbies"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos);
        spinnerTipo.setAdapter(adapter);
        tvTipo.setVisibility(View.VISIBLE);
        spinnerTipo.setVisibility(View.VISIBLE);
    }

    private void selectorFecha() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar Fecha")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // MaterialDatePicker devuelve el tiempo en UTC a medianoche. 
            // Para evitar el desfase de zona horaria al pasar a la hora local, extraemos los campos en UTC.
            Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            utcCalendar.setTimeInMillis(selection);
            
            selectedDateTimeCalendar.set(Calendar.YEAR, utcCalendar.get(Calendar.YEAR));
            selectedDateTimeCalendar.set(Calendar.MONTH, utcCalendar.get(Calendar.MONTH));
            selectedDateTimeCalendar.set(Calendar.DAY_OF_MONTH, utcCalendar.get(Calendar.DAY_OF_MONTH));

            selectorHora();
        });
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            etFechaHora.setText(sdf.format(selectedDateTimeCalendar.getTime()));
        });
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }
}

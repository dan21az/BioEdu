package com.dan21az.bioedu.vista.hidratacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.HidratacionDatos;
import com.dan21az.bioedu.modelo.hidratacion.RegistroHidratacion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class HidratacionMenu extends AppCompatActivity {

    private HidratacionDatos controladora;
    private Calendar fechaSeleccionada;
    // Referencias de la UI
    private TextView tvMetaValue, tvPercentage, tvTotalValue;
    private CircularProgressIndicator circularProgress;
    private EditText etDate;
    private RecyclerView rvRegistros;
    private RegistroAdapter adaptador;
    private MaterialButton btnSetGoal, btnRegister;
    private TextView tvMetaConseguida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);

        fechaSeleccionada = Calendar.getInstance();
        //  Cargamos el layout del dashboard
        setContentView(R.layout.activity_hidratacion_control);

        //  Inicializamos la controladora
        controladora = HidratacionDatos.getInstance(this);

        //  Vinculamos los componentes del XML con Java
        vincularVistas();
        //  Configuramos el selector de fecha
        actualizarCampoFecha();
        //  Configuramos el RecyclerView (Lista de tomas)
        rvRegistros.setLayoutManager(new LinearLayoutManager(this));

        // Configuramos los eventos de los botones
        configurarEventos();

        //  Mostramos los datos iniciales
        actualizarVista();
    }

    private void vincularVistas() {
        tvMetaValue = findViewById(R.id.tvMetaValue);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        circularProgress = findViewById(R.id.circularProgress);
        etDate = findViewById(R.id.etDate);
        rvRegistros = findViewById(R.id.rvRegistros);
        btnSetGoal = findViewById(R.id.btnSetGoal);
        btnRegister = findViewById(R.id.btnRegister);
        tvMetaConseguida = findViewById(R.id.tvMetaConseguida);
    }


    private void configurarEventos() {
        // Selector de fecha Material
        etDate.setOnClickListener(v -> {
            // 1. Configurar restricciones (Ejemplo: Solo permitir fechas pasadas y hoy)
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            constraintsBuilder.setValidator(DateValidatorPointBackward.now());

            // 2. Crear el selector
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha de registro")
                    .setSelection(fechaSeleccionada.getTimeInMillis()) // Abrir en la fecha actual
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build();

            // 3. Manejar el resultado
            datePicker.addOnPositiveButtonClickListener(selection -> {
                // Actualizar nuestro objeto Calendar global
                fechaSeleccionada.setTimeInMillis(selection);

                // Actualizar UI
                actualizarCampoFecha();
                actualizarVista();
            });

            datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });

        // Abrir pantalla de Meta
        btnSetGoal.setOnClickListener(v -> {
            Intent intent = new Intent(this, MetaActivity.class);
            startActivity(intent);
        });

        // Abrir pantalla de Registro
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroTomaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Al regresar de Meta o Registro, refrescamos los mililitros y el progreso
        actualizarVista();
    }

    private void actualizarVista() {
        int meta = controladora.getMetaDiaria();
        int total = controladora.getTotalPorDia(fechaSeleccionada);

        tvMetaValue.setText(meta + " ml");
        tvTotalValue.setText(total + " ml");

        int porcentaje = (meta > 0) ? (total * 100 / meta) : 0;
        porcentaje = Math.min(porcentaje, 100);

        circularProgress.setProgress(porcentaje);
        tvPercentage.setText(porcentaje + "%");

        if (porcentaje == 100) {
            tvMetaConseguida.setVisibility(View.VISIBLE);
        } else {
            tvMetaConseguida.setVisibility(View.GONE);
        }

        List<RegistroHidratacion> lista =
                controladora.getRegistrosPorDia(fechaSeleccionada);

        adaptador = new RegistroAdapter(lista);
        rvRegistros.setAdapter(adaptador);
    }
    private void actualizarCampoFecha() {
        String fecha = String.format(
                Locale.getDefault(),
                "%02d/%02d/%d",
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH),
                fechaSeleccionada.get(Calendar.MONTH) + 1,
                fechaSeleccionada.get(Calendar.YEAR)
        );
        etDate.setText(fecha);
    }

}
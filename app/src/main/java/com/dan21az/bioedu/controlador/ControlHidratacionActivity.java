package com.dan21az.bioedu.controlador;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.HidratacionDatos;
import com.dan21az.bioedu.modelo.hidratacion.RegistroHidratacion;
import com.dan21az.bioedu.vista.hidratacion.MetaActivity;
import com.dan21az.bioedu.vista.hidratacion.RegistroAdapter;
import com.dan21az.bioedu.vista.hidratacion.RegistroTomaActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;



public class ControlHidratacionActivity extends AppCompatActivity {

    private HidratacionDatos controladora;

    // Componentes de la interfaz
    private TextView tvMetaValue, tvPercentage, tvTotalValue;
    private CircularProgressIndicator circularProgress;
    private EditText etDate;
    private RecyclerView rvRegistros;
    private RegistroAdapter adaptador;
    private MaterialButton btnSetGoal, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Inicializar la controladora
        controladora = HidratacionDatos.getInstance(this);

        // 3. Vincular las vistas (IDs del XML)
        vincularVistas();

        // 4. Configurar el Selector de Fecha
        configurarDatePicker();

        // 5. Configurar el RecyclerView para los registros
        rvRegistros.setLayoutManager(new LinearLayoutManager(this));

        // Botón Establecer Meta
        btnSetGoal.setOnClickListener(v -> {
            Intent intent = new Intent(this, MetaActivity.class);
            startActivity(intent);
        });

        // Botón Registrar Toma
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroTomaActivity.class);
            startActivity(intent);
        });

        actualizarVista();
    }

    private void vincularVistas() {
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvMetaValue = findViewById(R.id.tvMetaValue);
        tvPercentage = findViewById(R.id.tvPercentage);
        circularProgress = findViewById(R.id.circularProgress);
        etDate = findViewById(R.id.etDate);
        rvRegistros = findViewById(R.id.rvRegistros);
        btnSetGoal = findViewById(R.id.btnSetGoal);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void configurarDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String fecha = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month + 1), year);
                etDate.setText(fecha);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarVista(); // Refresca los datos cuando vuelves de otras pantallas
    }

    private void actualizarVista() {
        int meta = controladora.getMetaDiaria();
        int total = controladora.getTotalHoy();

        // Actualizar textos de la meta y total
        tvMetaValue.setText(meta + " ml");
        if (tvTotalValue != null) {
            tvTotalValue.setText(total + " ml");
        }

        // Calcular progreso porcentual
        int porcentaje = (meta > 0) ? (total * 100 / meta) : 0;
        porcentaje = Math.min(porcentaje, 100); // No pasar del 100% visualmente

        circularProgress.setProgress(porcentaje);
        if (tvPercentage != null) {
            tvPercentage.setText(porcentaje + "%");
        }

        // Refrescar la lista del RecyclerView con los registros actuales
        List<RegistroHidratacion> lista = controladora.getRegistrosDeHoy();
        adaptador = new RegistroAdapter(lista);
        rvRegistros.setAdapter(adaptador);
    }
}
package com.dan21az.bioedu.vista.actividad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.ActividadesDatos;
import com.dan21az.bioedu.modelo.actividad.Academica;
import com.dan21az.bioedu.modelo.actividad.Actividad;
import com.dan21az.bioedu.modelo.actividad.Personal;
import com.dan21az.bioedu.modelo.sesionenfoque.SesionEnfoque;
import com.dan21az.bioedu.vista.sesionEnfoque.HistorialSesionAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetalleActividad extends AppCompatActivity {

    public static final String EXTRA_ACTIVIDAD_ID = "actividad_id";

    private TextView tvTitulo, tvNombre, tvTipo, tvAsignatura, tvPrioridad, tvEstado,
            tvFechaLimite, tvTiempoEstimado, tvAvanceActual, tvDescripcion, tvLugar;
    private CardView cardHistorialTiempo;
    private HistorialSesionAdapter historialAdapter;
    private String actividadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_detalle_actividad);

        inicializarVistas();

        // Obtener el ID de la actividad
        actividadId = getIntent().getStringExtra(EXTRA_ACTIVIDAD_ID);
        
        if (actividadId == null) {
            Toast.makeText(this, "Error: ID de actividad no proporcionado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ActividadesDatos.getInstancia().inicializar(this);
        cargarDatosActividad();

        // Configurar botones
        Button btnVolver = findViewById(R.id.btn_volver_listado);
        btnVolver.setOnClickListener(v -> finish());

        Button btnEditar = findViewById(R.id.btn_editar_actividad);
        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleActividad.this, EditarActividad.class);
            intent.putExtra(EXTRA_ACTIVIDAD_ID, actividadId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosActividad();
    }

    private void cargarDatosActividad() {
        Actividad actividad = ActividadesDatos.getInstancia().buscarActividadPorId(actividadId);
        if (actividad != null) {
            mostrarDetalles(actividad);
        } else {
            Toast.makeText(this, "La actividad ya no existe", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void inicializarVistas() {
        tvTitulo = findViewById(R.id.tv_detalle_titulo);
        tvNombre = findViewById(R.id.detalle_tv_nombre);
        tvTipo = findViewById(R.id.detalle_tv_tipo);
        tvAsignatura = findViewById(R.id.detalle_tv_asignatura);
        tvPrioridad = findViewById(R.id.detalle_tv_prioridad);
        tvEstado = findViewById(R.id.detalle_tv_estado);
        tvFechaLimite = findViewById(R.id.detalle_tv_fecha_limite);
        tvTiempoEstimado = findViewById(R.id.detalle_tv_tiempo_estimado);
        tvAvanceActual = findViewById(R.id.detalle_tv_avance_actual);
        tvDescripcion = findViewById(R.id.detalle_tv_descripcion);
        tvLugar = findViewById(R.id.detalle_tv_lugar);

        cardHistorialTiempo = findViewById(R.id.card_historial_tiempo);
        RecyclerView recyclerViewHistorial = findViewById(R.id.recycler_view_historial_tiempo);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));
        historialAdapter = new HistorialSesionAdapter(new ArrayList<>());
        recyclerViewHistorial.setAdapter(historialAdapter);
    }

    private void mostrarDetalles(@NonNull Actividad actividad) {
        tvTitulo.setText(String.format("Detalles de la actividad"));

        tvNombre.setText(HtmlCompat.fromHtml("<b>Nombre:</b> " + actividad.getNombre(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        tvTipo.setText(HtmlCompat.fromHtml("<b>Tipo:</b> " + actividad.getTipo(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        tvPrioridad.setText(HtmlCompat.fromHtml("<b>Prioridad:</b> " + actividad.getPrioridad(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        tvEstado.setText(HtmlCompat.fromHtml("<b>Estado:</b> " + actividad.getEstado(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        tvFechaLimite.setText(HtmlCompat.fromHtml("<b>Fecha Límite:</b> " + actividad.getFechaVencimiento(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        tvAvanceActual.setText(HtmlCompat.fromHtml("<b>Avance Actual:</b> " + actividad.getProgreso() + "%", HtmlCompat.FROM_HTML_MODE_COMPACT));
        tvDescripcion.setText(HtmlCompat.fromHtml("<b>Descripción:</b> " + actividad.getDescripcion(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        tvTiempoEstimado.setText(HtmlCompat.fromHtml("<b>Tiempo Estimado Total:</b> " + getDuracionFormateada(actividad.getTiempoEstimado()), HtmlCompat.FROM_HTML_MODE_COMPACT));

        tvAsignatura.setVisibility(View.GONE);
        tvLugar.setVisibility(View.GONE);
        cardHistorialTiempo.setVisibility(View.GONE);

        if (actividad instanceof Academica) {
            Academica academica = (Academica) actividad;
            tvAsignatura.setVisibility(View.VISIBLE);
            tvAsignatura.setText(HtmlCompat.fromHtml("<b>Asignatura:</b> " + academica.getAsignatura(), HtmlCompat.FROM_HTML_MODE_COMPACT));

            List<SesionEnfoque> historial = academica.getSesiones();
            if (historial != null && !historial.isEmpty()) {
                cardHistorialTiempo.setVisibility(View.VISIBLE);
                historialAdapter.setSesiones(historial);
            }
        } else if (actividad instanceof Personal) {
            Personal personal = (Personal) actividad;
            tvLugar.setVisibility(View.VISIBLE);
            tvLugar.setText(HtmlCompat.fromHtml("<b>Lugar:</b> " + personal.getLugar(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        }
    }

    private String getDuracionFormateada(float duracionEnHoras) {
        int minutosTotales = Math.round(duracionEnHoras * 60);

        if (minutosTotales < 60) {
            return minutosTotales + " minutos";
        } else {
            int horas = minutosTotales / 60;
            int minutosRestantes = minutosTotales % 60;

            if (minutosRestantes == 0) {
                return horas + (horas == 1 ? " hora" : " horas");
            } else {
                return String.format(Locale.getDefault(), "%d %s y %d minutos", 
                    horas, (horas == 1 ? "hora" : "horas"), minutosRestantes);
            }
        }
    }
}

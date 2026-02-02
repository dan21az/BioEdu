package com.dan21az.bioedu.vista.sesionEnfoque;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.Slider;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.modelo.actividad.Actividad;
import com.dan21az.bioedu.controlador.EnfoqueService;

import java.util.Locale;

public class SesionEnfoqueActivity extends AppCompatActivity {

    public static final String EXTRA_ACTIVIDAD = "extra_actividad";
    public static final String EXTRA_MODO = "extra_modo";

    private TextView tvTemporizador, tvActividadNombre, tvTitulo, tvLabelCiclos;
    private Button btnIniciar, btnPausar, btnReiniciar, btnOp1, btnOp2, btnOp3, btnOp4;
    private MaterialButtonToggleGroup grupoDuracion;
    private Slider sliderCiclos;
    private View layoutCiclos;

    private Actividad actividadAsociada;
    private String modoActual;
    private long duracionSeleccionadaMs;
    private EnfoqueReceiver receiver;
    private ActivityResultLauncher<String> peticionPermiso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion_enfoque);
        EdgeToEdge.enable(this);


        solicitarPermisoNotificacion();

        // Inicialización de datos
        actividadAsociada = (Actividad) getIntent().getSerializableExtra(EXTRA_ACTIVIDAD);
        modoActual = getIntent().getStringExtra(EXTRA_MODO);
        if (modoActual == null) modoActual = "POMODORO";
        if (actividadAsociada == null) {
            Toast.makeText(this, "Error: No se encontró la actividad", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inicializarVistas();
        configurarInterfazPorModo();
        configurarListeners();

        // Crear y registrar el receiver del servicio
        receiver = new EnfoqueReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(EnfoqueService.ACCION_TIEMPO_UPDATE);
        filter.addAction(EnfoqueService.ACCION_SESION_COMPLETA);
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    private void inicializarVistas() {

        tvTemporizador = findViewById(R.id.tv_temporizador);
        tvTitulo = findViewById(R.id.tv_titulo_sesion);
        tvActividadNombre = findViewById(R.id.tv_actividad_nombre);
        tvLabelCiclos = findViewById(R.id.tv_label_ciclos);
        btnIniciar = findViewById(R.id.btn_iniciar);
        btnPausar = findViewById(R.id.btn_pausar);
        btnReiniciar = findViewById(R.id.btn_reiniciar);
        btnOp1 = findViewById(R.id.btn_opcion_1);
        btnOp2 = findViewById(R.id.btn_opcion_2);
        btnOp3 = findViewById(R.id.btn_opcion_3);
        btnOp4 = findViewById(R.id.btn_opcion_4);
        grupoDuracion = findViewById(R.id.toggle_group_duracion);
        sliderCiclos = findViewById(R.id.slider_ciclos);
        layoutCiclos = findViewById(R.id.layout_config_ciclos);
    }

    private void configurarInterfazPorModo() {

        tvActividadNombre.setText("Actividad: " + actividadAsociada.getNombre());

        if ("DEEPWORK".equals(modoActual)) {
            tvTitulo.setText("Deep Work");
            layoutCiclos.setVisibility(View.GONE);
            btnOp1.setText("45 min");
            btnOp2.setText("60 min");
            btnOp3.setText("90 min");
            duracionSeleccionadaMs = 45 * 60 * 1000L;
        } else {
            tvTitulo.setText("Técnica Pomodoro");
            layoutCiclos.setVisibility(View.VISIBLE);
            btnOp1.setText("25 min");
            btnOp2.setText("15 min");
            btnOp3.setText("5 min");
            duracionSeleccionadaMs = 25 * 60 * 1000L;
        }

        btnOp4.setText("10 seg");
        grupoDuracion.check(R.id.btn_opcion_1);
        actualizarTextoReloj(duracionSeleccionadaMs);

    }

    private void configurarListeners() {

        // Botones de duración
        btnOp1.setOnClickListener(v -> setDuracion(modoActual.equals("DEEPWORK") ? 45 * 60 : 25 * 60));
        btnOp2.setOnClickListener(v -> setDuracion(modoActual.equals("DEEPWORK") ? 60 * 60 : 15 * 60));
        btnOp3.setOnClickListener(v -> setDuracion(modoActual.equals("DEEPWORK") ? 90 * 60 : 5 * 60));
        btnOp4.setOnClickListener(v -> setDuracion(10)); // 10 segundos para test

        // Slider de ciclos
        sliderCiclos.addOnChangeListener((slider, value, fromUser) ->
                tvLabelCiclos.setText("Ciclos: " + (int) value+ "\nDescanso de 5 minutos"));

        // Iniciar o Reanudar
        btnIniciar.setOnClickListener(v -> {
            verificarPermisosYComenzar();
            enviarComando(EnfoqueService.ACCION_INICIAR);
        });

        // Pausar
        btnPausar.setOnClickListener(v -> {
            enviarComando(EnfoqueService.ACCION_PAUSA);
        });

        // Detener / Reiniciar
        btnReiniciar.setOnClickListener(v -> {
            detenerYSesionLimpia();
        });
    }

    private void solicitarPermisoNotificacion() {
        peticionPermiso = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    // ELIMINAR ESTA LÍNEA: enviarComando(EnfoqueService.ACCION_INICIAR);
                    // El servicio solo debe iniciar cuando el usuario presione el botón físico.

                    if (isGranted) {
                        Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Sin permiso no verás el cronómetro fuera de la app", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verificarPermisosYComenzar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                enviarComando(EnfoqueService.ACCION_INICIAR);
            } else {
                peticionPermiso.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Versiones anteriores a Android 13 no necesitan permiso dinámico
            enviarComando(EnfoqueService.ACCION_INICIAR);
        }
    }

    // Limpiar la interfaz
    private void detenerYSesionLimpia() {
        enviarComando(EnfoqueService.ACCION_PARAR);

        // Resetear valores visuales localmente
        configurarInterfazPorModo(); // Restablece tiempo según Pomodoro o DeepWork
        actualizarBotones("DETENIDO");
    }

    private void enviarComando(String accion) {
        Intent intent = new Intent(this, EnfoqueService.class);
        intent.setAction(accion);

        // Si inicia una nueva sesion
        if (accion.equals(EnfoqueService.ACCION_INICIAR)) {
            intent.putExtra("extra_actividad", actividadAsociada);
            intent.putExtra("DURACION", duracionSeleccionadaMs);
            intent.putExtra(EnfoqueService.EXTRA_MODO, modoActual);
            intent.putExtra("CICLOS", (int) sliderCiclos.getValue());
        }

        // Manejo de servicio en las versiones de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void setDuracion(long segundos) {
        duracionSeleccionadaMs = segundos * 1000L;
        actualizarTextoReloj(duracionSeleccionadaMs);
    }

    private void actualizarTextoReloj(long ms) {
        int min = (int) (ms / 1000) / 60;
        int seg = (int) (ms / 1000) % 60;
        tvTemporizador.setText(String.format(Locale.getDefault(), "%02d:%02d", min, seg));
    }

    private void actualizarBotones(String estado) {
        if (estado == null) return;

        if ("CORRIENDO".equals(estado)) {
            btnIniciar.setEnabled(false);
            btnIniciar.setText("Iniciar");
            btnPausar.setEnabled(true);
            btnReiniciar.setEnabled(true);

            habilitarConfiguracion(false);
        }
        else if ("PAUSADO".equals(estado)) {
            btnIniciar.setEnabled(true);
            btnIniciar.setText("Reanudar");
            btnPausar.setEnabled(false);
            btnReiniciar.setEnabled(true);

            habilitarConfiguracion(false);
        }
        else if ("DETENIDO".equals(estado)) {
            btnIniciar.setEnabled(true);
            btnIniciar.setText("Iniciar");
            btnPausar.setEnabled(false);
            btnReiniciar.setEnabled(false);

            // Resetear el reloj al tiempo configurado originalmente
            actualizarTextoReloj(duracionSeleccionadaMs);

            habilitarConfiguracion(true);
            configurarInterfazPorModo();
        }
    }

    private void habilitarConfiguracion(boolean habilitar) {

        grupoDuracion.setEnabled(habilitar);
        btnOp1.setEnabled(habilitar);
        btnOp2.setEnabled(habilitar);
        btnOp3.setEnabled(habilitar);
        btnOp4.setEnabled(habilitar);
        sliderCiclos.setEnabled(habilitar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, EnfoqueService.class);
        intent.setAction(EnfoqueService.ACCION_SOLICITAR_ESTADO);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
    }

    private class EnfoqueReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String accion = intent.getAction();

            if (EnfoqueService.ACCION_TIEMPO_UPDATE.equals(accion)) {
                manejarActualizacionTiempo(intent);
            }
            else if (EnfoqueService.ACCION_SESION_COMPLETA.equals(accion)) {
                manejarSesionFinalizada();
            }
        }

        private void manejarActualizacionTiempo(Intent intent) {

            String idServicio = intent.getStringExtra("ACTIVIDAD_ID");
            String nombreServicio = intent.getStringExtra("ACTIVIDAD_NOMBRE");
            String modoServicio = intent.getStringExtra("MODO");
            String estado = intent.getStringExtra("ESTADO");
            long ms = intent.getLongExtra(EnfoqueService.EXTRA_TIEMPOMS, 0);

            boolean estaOcupado = !"DETENIDO".equals(estado);

            // El conflicto ocurre si el servicio está ocupado Y (es otra ID o es otro modo)
            boolean hayConflicto = estaOcupado && (
                    (idServicio != null && !String.valueOf(actividadAsociada.getId()).equals(idServicio)) ||
                            (modoServicio != null && !modoActual.equals(modoServicio))
            );

            runOnUiThread(() -> {
                if (hayConflicto) {
                    mostrarInterfazBloqueada(nombreServicio);
                } else {
                    mostrarInterfazActiva(intent, estado, ms);
                }
            });
        }

        private void mostrarInterfazBloqueada(String nombreActividad) {
            tvTemporizador.setText("--:--");
            tvTitulo.setText("Sesión en curso");
            tvActividadNombre.setText("Ocupado con: " + nombreActividad);

            // Bloqueo de controles
            habilitarConfiguracion(false);
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(false);
            btnReiniciar.setEnabled(true);
            btnReiniciar.setText("Detener\nSesión");
        }

        private void mostrarInterfazActiva(Intent intent, String estado, long ms) {
            boolean sesionEnCurso = "CORRIENDO".equals(estado) || "PAUSADO".equals(estado);
            actualizarBotones(estado);
            btnReiniciar.setText("Reiniciar");

            if (sesionEnCurso) {
                actualizarTextoReloj(ms);
                String fase = intent.getStringExtra("FASE");
                if ("DESCANSO".equals(fase)) {
                    tvTitulo.setText("Sesión de Pomodoro\n¡Descanso!");
                } else {
                    String modo = "DEEPWORK".equals(modoActual) ? "Deep Work" : "Pomodoro";
                    tvTitulo.setText("Sesión de " + modo + "\nEnfoque");
                }
            } else {
                // Estado DETENIDO: Títulos estáticos de configuración
                tvTitulo.setText(modoActual.equals("DEEPWORK") ? "Técnica Deep Work" : "Técnica Pomodoro");
            }
            tvActividadNombre.setText("Actividad: " + actividadAsociada.getNombre());
        }

        private void manejarSesionFinalizada() {
            runOnUiThread(() -> {
                Toast.makeText(SesionEnfoqueActivity.this, "¡Sesión finalizada!", Toast.LENGTH_SHORT).show();
                configurarInterfazPorModo();
                actualizarBotones("DETENIDO");
            });
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Actualizamos el intent de la activity

        if (intent.hasExtra(EXTRA_ACTIVIDAD)) {
            actividadAsociada = (Actividad) intent.getSerializableExtra(EXTRA_ACTIVIDAD);
            if (actividadAsociada != null) {
                tvActividadNombre.setText("Actividad: " + actividadAsociada.getNombre());
                // Forzamos un chequeo de estado inmediato
                enviarComando(EnfoqueService.ACCION_SOLICITAR_ESTADO);
            }
        }
    }

}

package com.dan21az.bioedu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

import com.dan21az.bioedu.datos.ActividadesDatos;


public class AjustesActivity extends AppCompatActivity {

    // Para usar las preferencias (Guarda pequeños datos)
    public static final String PREF_NAME = "AppPrefs";
    public static final String KEY_VIBRACION_ACTIVADA = "vibracion_activada";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_ajustes);

        // Inicializar Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_ajustes);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return windowInsets;
        });
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicializar Botones
        Button btnCompletarTodas = findViewById(R.id.btn_completar_todas);
        Button btnEliminarCompletadas = findViewById(R.id.btn_eliminar_completadas);
        Button btnEliminarTodas = findViewById(R.id.btn_eliminar_todas);
        Button btnAcercaDe = findViewById(R.id.btn_acerca_de);

        // Opcion de vibracion
        // Inicializar switch
        MaterialSwitch switchVibracion = findViewById(R.id.switch_vibracion);
        // Obtener preferencias
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isVibrationEnabled = prefs.getBoolean(KEY_VIBRACION_ACTIVADA, true);
        switchVibracion.setChecked(isVibrationEnabled);

        // Guardar el nuevo estado
        switchVibracion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_VIBRACION_ACTIVADA, isChecked).apply(); //Editar la preferencia
            String msg = isChecked ? "Vibración activada." : "Vibración desactivada.";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });


        btnCompletarTodas.setOnClickListener(v -> {
            mostrarDialogoConfirmacion(
                    this, "Confirmar Avance Total",
                    "¿Estás seguro de que deseas marcar todas las actividades como completadas (100% de avance)? Esta acción se puede revertir manualmente.",
                    () -> {
                        ActividadesDatos.getInstancia().completarTodasLasActividades();
                        Toast.makeText(this, "Todas las actividades han sido marcadas como completadas (100%).", Toast.LENGTH_LONG).show();
                    }
            );
        });

        btnEliminarCompletadas.setOnClickListener(v -> {
            mostrarDialogoConfirmacion(
                    this, "Eliminar Completadas",
                    "¿Estás seguro de que deseas eliminar permanentemente todas las actividades que tienen 100% de avance?. Es irreversible",
                    () -> {
                        int eliminadas = ActividadesDatos.getInstancia().eliminarActividadesCompletadas();
                        Toast.makeText(this, eliminadas + " actividades completadas eliminadas.", Toast.LENGTH_LONG).show();
                    }
            );
        });

        btnEliminarTodas.setOnClickListener(v -> {
            mostrarDialogoConfirmacion(
                    this, "Eliminar Todo",
                    "¡Advertencia! Esta acción eliminará todas las actividades y sus datos asociados. Es irreversible. ¿Continuar?",
                    () -> {
                        ActividadesDatos.getInstancia().eliminarTodasLasActividades();
                        Toast.makeText(this, "Todas las actividades han sido eliminadas.", Toast.LENGTH_LONG).show();
                    }
            );
        });

        btnAcercaDe.setOnClickListener(v -> {
            mostrarDialogoAcercaDe(this);
        });
    }

    // Mostrar Dialogos
    private void mostrarDialogoConfirmacion(Context context, String title, String message, Runnable onConfirm) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title).setMessage(message)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    onConfirm.run();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void mostrarDialogoAcercaDe(Context context) {
        // Definimos el enlace de GitHub aquí (¡Asegúrate de reemplazar con tu URL real!)
        String githubUrl = "https://github.com/dan21az/POO-P01-G06";

        // Crear el mensaje usando HTML para los enlaces
        StringBuilder message = new StringBuilder();
        message.append("Versión: 1.0<br>");
        message.append("Desarrollado como proyecto de gestión de enfoque y productividad para la asignatura de POO ESPOL PAO II 2025.<br><br>");
        message.append("<b>🚀 Desarrollado por:</b><br>");
        message.append("- Aguilar Vélez Henry Ariel<br>");
        message.append("- Anzules García Daniel Isaías<br>");
        message.append("- García Onofre Klever Javier<br>");
        message.append("- Murillo Castro Allan Marcelo<br><br>");

        // --- Sección de enlaces ---

        // Enlace de Atribución de Iconografía
        String flaticonUrl = "https://www.flaticon.es/autores/ra-ic0n21/detailed-outline?author_id=3187&type=standard";
        message.append("<b>🖼️ Atribución de Iconografía:</b><br>");
        message.append("Icono principal cortesía de <a href=\"")
                .append(flaticonUrl)
                .append("\">RA_IC0N21</a> (Flaticon).<br><br>");

        // Nuevo Enlace al Código Fuente de GitHub
        message.append("<b>🔗 Código Fuente:</b><br>");
        message.append("Consulta el código fuente completo en <a href=\"")
                .append(githubUrl)
                .append("\">GitHub</a>.<br><br>");

        message.append("© 2026");

        // Convertir el HTML a texto compatible con Android
        Spanned spannedMessage = HtmlCompat.fromHtml(
                message.toString(),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        );

        // Construir y mostrar el diálogo
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("Acerca de la App")
                .setMessage(spannedMessage)
                .setPositiveButton("Cerrar", (dialogInterface, which) -> dialogInterface.dismiss())
                .show();

        // Obtener el TextView y habilitar el manejo de enlaces
        TextView messageTextView = dialog.findViewById(android.R.id.message);

        if (messageTextView != null) {
            // Habilitar que las etiquetas <a> de HTML sean clicables
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}

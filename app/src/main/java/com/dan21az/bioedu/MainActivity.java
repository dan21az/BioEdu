package com.dan21az.bioedu;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.dan21az.bioedu.datos.MenuDatos;
import com.dan21az.bioedu.vista.hidratacion.HidratacionMenu;
import com.dan21az.bioedu.vista.huellaverde.MainHuellaVerde;
import com.dan21az.bioedu.vista.actividad.ListaActividades;
import com.dan21az.bioedu.vista.juegomemoria.JuegoMemoria;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView tvResumenPuntos;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        Typeface.DEFAULT.getStyle();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_main);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        tvResumenPuntos = findViewById(R.id.tvResumenSosteniblidad);
        MaterialCardView cardActividades = findViewById(R.id.cardActividades);
        MaterialCardView cardHuellaVerde = findViewById(R.id.cardHuellaVerde);
        MaterialCardView cardHidratacion = findViewById(R.id.cardHidratacion);
        MaterialCardView cardJuego = findViewById(R.id.cardJuego);
        ImageButton ajustes = findViewById(R.id.btnSettings);

        // Listeners: Abrir la actividad de detalle

        cardActividades.setOnClickListener(v -> {
            v.setEnabled(false);
            Intent intent = new Intent(MainActivity.this, ListaActividades.class);
            startActivity(intent);

            // Rehabilitamos después de un segundo
            v.postDelayed(() -> v.setEnabled(true), 1000);
        });

        cardHuellaVerde.setOnClickListener(v -> {
            v.setEnabled(false);
            Intent intent = new Intent(MainActivity.this, MainHuellaVerde.class);
            startActivity(intent);

            // Rehabilitamos después de un segundo
            v.postDelayed(() -> v.setEnabled(true), 1000);
        });

        cardHidratacion.setOnClickListener(v -> {
            v.setEnabled(false);
            Intent intent = new Intent(MainActivity.this, HidratacionMenu.class);
            startActivity(intent);

            // Rehabilitamos después de un segundo
            v.postDelayed(() -> v.setEnabled(true), 1000);
        });
        cardJuego.setOnClickListener(v -> {
            v.setEnabled(false);
            Intent intent = new Intent(MainActivity.this, JuegoMemoria.class);
            startActivity(intent);

            // Rehabilitamos después de un segundo
            v.postDelayed(() -> v.setEnabled(true), 1000);
        });

        ajustes.setOnClickListener(v -> {
            v.setEnabled(false);
            Intent intent = new Intent(MainActivity.this, AjustesActivity.class);
            startActivity(intent);

            // Rehabilitamos después de un segundo
            v.postDelayed(() -> v.setEnabled(true), 1000);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().post(() -> {

            // 2. Cargamos los datos en un hilo secundario para no bloquear el Main Thread
            executorService.execute(() -> {
                int puntos = MenuDatos.obtenerPuntosCache(this);

                // 3. Solo volvemos al hilo principal para tocar la UI
                runOnUiThread(() -> {
                    tvResumenPuntos.setText(puntos + " / 28 pts");
                });
            });
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        actualizarResumenDashboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Apaga el executor para liberar recursos
        executorService.shutdown();
    }

    private void actualizarResumenDashboard() {

        int puntos = MenuDatos.obtenerPuntosCache(this);

        tvResumenPuntos.setText(puntos + " / 28 pts");
    }

}

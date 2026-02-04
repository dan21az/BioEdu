package com.dan21az.bioedu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.dan21az.bioedu.datos.MenuDatos;
import com.dan21az.bioedu.vista.hidratacion.HidratacionMenu;
import com.dan21az.bioedu.vista.huellaverde.MainHuellaVerde;

import com.dan21az.bioedu.vista.actividad.ListaActividades;
import com.dan21az.bioedu.vista.juegomemoria.JuegoMemoria;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private TextView tvResumenPuntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_main);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        tvResumenPuntos = findViewById(R.id.tvResumenSosteniblidad);
        MaterialCardView cardActividades = findViewById(R.id.cardActividades);
        MaterialCardView cardSostenibilidad = findViewById(R.id.cardSostenibilidad);
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

        cardSostenibilidad.setOnClickListener(v -> {
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


        actualizarResumenDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();

        actualizarResumenDashboard();
    }

    private void actualizarResumenDashboard() {

        int puntos = MenuDatos.obtenerPuntosCache(this);

        tvResumenPuntos.setText(puntos + " / 28 pts");
    }

}

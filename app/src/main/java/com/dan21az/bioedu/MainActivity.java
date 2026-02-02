package com.dan21az.bioedu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dan21az.bioedu.vista.hidratacion.HidratacionMenu;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

import com.dan21az.bioedu.vista.juegomemoria.JuegoMemoria;
import com.dan21az.bioedu.vista.actividad.ListaActividades;
import com.dan21az.bioedu.vista.sostenibilidad.MainSostenibilidadActivity;


public class MainActivity extends AppCompatActivity {

    private MaterialCardView cardActividades, cardHidratacion, cardSostenibilidad, cardMemoria;
    private MaterialButton btnAjustes, btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_main);

        // Handle system bar insets for the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return windowInsets;
        });

        // Inicializar tarjetas
        cardActividades = findViewById(R.id.card_actividades);
        cardHidratacion = findViewById(R.id.card_hidratacion);
        cardSostenibilidad = findViewById(R.id.card_sostenibilidad);
        cardMemoria = findViewById(R.id.card_memoria);

        // Inicializar botones
        btnAjustes = findViewById(R.id.btn_ajustes);
        btnSalir = findViewById(R.id.btn_salir);

        // Acciones de las tarjetas
        cardActividades.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaActividades.class);
            startActivity(intent);
        });

        cardHidratacion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HidratacionMenu.class);
            startActivity(intent);
        });

        cardSostenibilidad.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainSostenibilidadActivity.class);
            startActivity(intent);
        });


        cardMemoria.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JuegoMemoria.class);
            startActivity(intent);
        });

        // Acciones de los Botones
        btnAjustes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AjustesActivity.class);
            startActivity(intent);
        });

        btnSalir.setOnClickListener(v -> {
            finish(); // Cierra la app
        });
    }

    // Mensaje para funciones en desarrollo
    private void mostrarFuncionEnDesarrollo() {
        Toast.makeText(this, "Función en desarrollo...", Toast.LENGTH_SHORT).show();
    }
}

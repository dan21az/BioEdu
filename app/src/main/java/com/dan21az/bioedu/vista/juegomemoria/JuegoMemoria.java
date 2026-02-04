package com.dan21az.bioedu.vista.juegomemoria;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.modelo.juegomemoria.Carta;
import com.dan21az.bioedu.modelo.juegomemoria.Juego;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class JuegoMemoria extends AppCompatActivity {

    private Juego juego;
    private RecyclerView recyclerView;
    private CartaAdapter adapter;
    private int primeraSeleccion = -1;
    private TextView txtIntentos;
    private LinearLayout pantallaInicio;
    private Button btnIniciar;
    private Button btnSalir;
    private boolean bloqueado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_juego_memoria);

        // Vincular vistas
        pantallaInicio = findViewById(R.id.pantallaInicio);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnSalir = findViewById(R.id.btnSalir);
        recyclerView = findViewById(R.id.recyclerViewJuego);
        txtIntentos = findViewById(R.id.txtIntentos);

        btnIniciar.setOnClickListener(v -> {
            pantallaInicio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            txtIntentos.setVisibility(View.VISIBLE);
            btnSalir.setVisibility(View.VISIBLE);
            iniciarJuego();
        });

        btnSalir.setOnClickListener(v -> finish());
    }

    private void iniciarJuego() {
        // 1. Reiniciar estado del juego
        juego = new Juego();
        primeraSeleccion = -1;
        bloqueado = true; // Bloqueo inicial para la vista previa

        // 2. Configurar el LayoutManager (4 columnas)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        // 4. Inicializar el adaptador una sola vez
        adapter = new CartaAdapter(juego, this::manejarSeleccion);
        recyclerView.setAdapter(adapter);

        actualizarTextoEstado();

        // 5. Ejecutar la vista previa con animaciones
        recyclerView.post(() -> {
            // Descubrimos todas las cartas en el modelo
            for (int i = 0; i < 16; i++) {
                juego.getTablero().getCarta(i).descubrir();
            }

            // Usamos notifyItemRangeChanged para que el animator sepa que debe animar las 16 cartas
            adapter.notifyItemRangeChanged(0, 16);

            // Ocultarlas después de 1.5 segundos
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                for (int i = 0; i < 16; i++) {
                    juego.getTablero().getCarta(i).ocultar();
                }
                adapter.notifyItemRangeChanged(0, 16);
                bloqueado = false;
            }, 1500);
        });
    }

    private void manejarSeleccion(int posicion) {
        if (bloqueado) return;
        if (posicion == primeraSeleccion) return;

        Carta carta = juego.getTablero().getCarta(posicion);
        if (carta.estaEmparejada() || carta.estaDescubierta()) return;

        // Revelar carta seleccionada
        carta.descubrir();
        adapter.notifyItemChanged(posicion);

        if (primeraSeleccion == -1) {
            // Es la primera carta del turno
            primeraSeleccion = posicion;
        } else {
            // Es la segunda carta del turno
            bloqueado = true;
            int segundaSeleccion = posicion;

            Carta primeraCarta = juego.getTablero().getCarta(primeraSeleccion);
            Carta segundaCarta = carta;

            // Procesar en el modelo (lógica de intentos y match)
            juego.seleccionarCartas(primeraSeleccion, segundaSeleccion);
            actualizarTextoEstado();

            if (primeraCarta.getImagenId() != segundaCarta.getImagenId()) {
                // NO coinciden: Esperar 1 segundo y ocultar
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    primeraCarta.ocultar();
                    segundaCarta.ocultar();
                    adapter.notifyItemChanged(primeraSeleccion);
                    adapter.notifyItemChanged(segundaSeleccion);
                    bloqueado = false;
                    primeraSeleccion = -1;
                }, 1000);
            } else {
                // COINCIDEN: Verificar si el juego terminó
                bloqueado = false;
                primeraSeleccion = -1;

                if (contarParesEncontrados() == 8) {
                    mostrarDialogoVictoria();
                }
            }
        }
    }

    private int contarParesEncontrados() {
        int cartasEmparejadas = 0;
        for (int i = 0; i < 16; i++) {
            if (juego.getTablero().getCarta(i).estaEmparejada()) {
                cartasEmparejadas++;
            }
        }
        return cartasEmparejadas / 2;
    }

    private void actualizarTextoEstado() {
        int pares = contarParesEncontrados();
        txtIntentos.setText("Intentos: " + juego.getIntentos() + " | Pares: " + pares + "/8");

        if (juego.juegoTerminado()) {
            btnSalir.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDialogoVictoria() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("🎉 ¡Felicidades!")
                .setMessage("Lograste encontrar todos los pares en " + juego.getIntentos() + " intentos.")
                .setCancelable(false)
                .setPositiveButton("Aceptar y salir", (dialog, which) -> finish())
                .show();
    }
}
package com.dan21az.bioedu.modelo.juegomemoria;


import com.dan21az.bioedu.R;

import java.util.ArrayList;
import java.util.Collections;

public class Tablero {

    private Carta[] cartas;

    public Tablero() {

        int[] imagenes = {
                R.drawable.ahorrar_energia_24px,
                R.drawable.casa_24px,
                R.drawable.agua_24px,
                R.drawable.arbol_24px,
                R.drawable.energia_24px,
                R.drawable.tacho_24px,
                R.drawable.reciclaje_24px,
                R.drawable.panel_24px
        };

        ArrayList<Integer> pares = new ArrayList<>();
        for (int img : imagenes) {
            pares.add(img);
            pares.add(img);
        }

        Collections.shuffle(pares);

        cartas = new Carta[16];
        for (int i = 0; i < 16; i++) {
            cartas[i] = new Carta(pares.get(i));
        }
    }

    public Carta getCarta(int indice) {
        return cartas[indice];
    }
}


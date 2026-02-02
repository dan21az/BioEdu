package com.dan21az.bioedu.vista.juegomemoria;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.modelo.juegomemoria.Carta;
import com.dan21az.bioedu.modelo.juegomemoria.Juego;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;

public class CartaAdapter extends RecyclerView.Adapter<CartaAdapter.ViewHolder> {
    private Juego juego;
    private OnCartaClickListener listener;

    public interface OnCartaClickListener {
        void onCartaClick(int posicion);
    }

    public CartaAdapter(Juego juego, OnCartaClickListener listener) {
        this.juego = juego;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MaterialButton btn = new MaterialButton(parent.getContext());

        // Configuración de dimensiones cuadradas
        int anchoColumna = parent.getWidth() / 4;
        // Si el ancho aún no está disponible, usamos una estimación basada en el ancho de pantalla
        if (anchoColumna == 0) {
            anchoColumna = parent.getContext().getResources().getDisplayMetrics().widthPixels / 4;
        }

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                anchoColumna
        );
        params.setMargins(8, 8, 8, 8);
        btn.setLayoutParams(params);

        // Estilos base de tu código original
        btn.setInsetTop(0);
        btn.setInsetBottom(0);
        btn.setPadding(0, 0, 0, 0);
        btn.setCornerRadius(20);
        btn.setStrokeWidth(0);
        btn.setTextSize(32);
        btn.setGravity(Gravity.CENTER);
        btn.setMinWidth(0);
        btn.setMinHeight(0);

        return new ViewHolder(btn);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Carta carta = juego.getTablero().getCarta(position);
        MaterialButton btn = (MaterialButton) holder.itemView;
        Context context = btn.getContext();

        if (carta.estaDescubierta() || carta.estaEmparejada()) {
            btn.setText("");
            btn.setIconResource(carta.getImagenId());

            // Estilos de carta REVELADA
            btn.setIconSize(120);
            btn.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            btn.setIconPadding(0);

            int colorSec = MaterialColors.getColor(context, com.google.android.material.R.attr.colorSecondary, Color.BLACK);
            int colorOnSec = MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnSecondary, Color.WHITE);

            btn.setBackgroundColor(colorSec);
            btn.setIconTint(ColorStateList.valueOf(colorOnSec));
        } else {
            // Estilos de carta OCULTA
            btn.setText("?");
            btn.setIcon(null);

            int colorPrimContainer = MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimaryContainer, Color.WHITE);
            int colorOnPrimContainer = MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnPrimaryContainer, Color.BLACK);

            btn.setBackgroundColor(colorPrimContainer);
            btn.setTextColor(colorOnPrimContainer);
        }

        btn.setOnClickListener(v -> listener.onCartaClick(position));
    }

    @Override
    public int getItemCount() { return 16; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(MaterialButton v) { super(v); }
    }
}
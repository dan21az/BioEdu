package com.dan21az.bioedu.vista.sesionEnfoque;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.modelo.sesionenfoque.SesionEnfoque;

import java.util.List;

public class HistorialSesionAdapter extends RecyclerView.Adapter<HistorialSesionAdapter.SesionViewHolder> {

    private List<SesionEnfoque> sesiones;

    public HistorialSesionAdapter(List<SesionEnfoque> sesiones) {
        this.sesiones = sesiones;
    }

    public void setSesiones(List<SesionEnfoque> nuevasSesiones) {
        this.sesiones = nuevasSesiones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sesion_historial, parent, false);
        return new SesionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        SesionEnfoque sesion = sesiones.get(position);

        long duracionMs = sesion.getDuracionEnfoqueMs();
        String duracionDisplay;

        // Calculamos segundos totales
        long segundosTotales = duracionMs / 1000;

        if (segundosTotales < 60) {
            // Para pruebas de 10 seg o duraciones muy cortas
            duracionDisplay = segundosTotales + " seg";
        } else {
            // Para sesiones normales, convertimos a minutos
            // Usar Math para evitar decimales
            long minutos = Math.round((double) segundosTotales / 60);
            duracionDisplay = minutos + " min";
        }

        holder.tvFechaSesion.setText(sesion.getFecha());
        holder.tvTecnicaAplicada.setText(sesion.getTipoTecnica());
        holder.tvDuracionMin.setText(duracionDisplay);
    }

    @Override
    public int getItemCount() {
        return sesiones != null ? sesiones.size() : 0;
    }

    static class SesionViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaSesion;
        TextView tvTecnicaAplicada;
        TextView tvDuracionMin;

        public SesionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaSesion = itemView.findViewById(R.id.tv_fecha_sesion);
            tvTecnicaAplicada = itemView.findViewById(R.id.tv_tecnica_aplicada);
            tvDuracionMin = itemView.findViewById(R.id.tv_duracion_min);
        }
    }
}
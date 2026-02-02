package com.dan21az.bioedu.vista.hidratacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.modelo.hidratacion.RegistroHidratacion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.ViewHolder> {
    private List<RegistroHidratacion> listaRegistros;

    public RegistroAdapter(List<RegistroHidratacion> listaRegistros) {
        this.listaRegistros = listaRegistros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hidracion_registro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroHidratacion registro = listaRegistros.get(position);
        Date fecha = new Date(registro.getFechaHora());

        // Convertir timestamp a hora AM/PM
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String horaFormateada = sdf.format(fecha);

        String cantidad = String.format("%4d ml", registro.getCantidadAgua());
        String texto = cantidad + " - " + horaFormateada;
        holder.tvRegistro.setText(texto);
    }



    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRegistro;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRegistro = itemView.findViewById(R.id.tvItemRegistro);
        }
    }
}
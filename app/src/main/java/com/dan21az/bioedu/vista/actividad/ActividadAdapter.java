package com.dan21az.bioedu.vista.actividad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.ActividadesDatos;
import com.dan21az.bioedu.modelo.actividad.Academica;
import com.dan21az.bioedu.modelo.actividad.Actividad;
import com.dan21az.bioedu.modelo.actividad.Personal;
import com.dan21az.bioedu.vista.sesionEnfoque.SesionEnfoqueActivity;

import java.util.ArrayList;
import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<Actividad> listaActividades;

    public ActividadAdapter(List<Actividad> listaActividades) {
        this.listaActividades = listaActividades != null ? listaActividades : new ArrayList<>();
    }

    public void setLista(List<Actividad> nuevaLista) {
        this.listaActividades = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        Actividad actividad = listaActividades.get(position);

        String nombreHtml = "<b>Nombre:</b> " + actividad.getNombre();
        holder.tvIdNombre.setText(HtmlCompat.fromHtml(nombreHtml, HtmlCompat.FROM_HTML_MODE_COMPACT));
        
        String fechaHtml = "<b>Vence:</b> " + actividad.getFechaVencimiento();
        holder.tvFechaVencimiento.setText(HtmlCompat.fromHtml(fechaHtml, HtmlCompat.FROM_HTML_MODE_COMPACT));
        
        String prioridadHtml = "<b>Prioridad:</b> " + actividad.getPrioridad();
        holder.tvPrioridad.setText(HtmlCompat.fromHtml(prioridadHtml, HtmlCompat.FROM_HTML_MODE_COMPACT));
        
        String avanceHtml = "<b>Avance:</b> " + actividad.getProgreso() + "%";
        holder.tvAvance.setText(HtmlCompat.fromHtml(avanceHtml, HtmlCompat.FROM_HTML_MODE_COMPACT));
        
        String tipoHtml = "<b>Tipo:</b> " + actividad.getTipo();
        holder.tvTipo.setText(HtmlCompat.fromHtml(tipoHtml, HtmlCompat.FROM_HTML_MODE_COMPACT));

        if (actividad instanceof Personal) {
            Personal personal = (Personal) actividad;
            String lugarHtml = "<b>Lugar:</b> " + personal.getLugar();
            holder.tvLugar.setText(HtmlCompat.fromHtml(lugarHtml, HtmlCompat.FROM_HTML_MODE_COMPACT));
            holder.tvLugar.setVisibility(View.VISIBLE);
            holder.btnPomodoro.setVisibility(View.GONE);
            holder.btnDeepWork.setVisibility(View.GONE);
        } else if (actividad instanceof Academica) {
            holder.tvLugar.setVisibility(View.GONE);
            holder.btnPomodoro.setVisibility(View.VISIBLE);
            holder.btnDeepWork.setVisibility(View.VISIBLE);
            
            holder.btnPomodoro.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, SesionEnfoqueActivity.class);
                intent.putExtra(SesionEnfoqueActivity.EXTRA_ACTIVIDAD, actividad);
                intent.putExtra(SesionEnfoqueActivity.EXTRA_MODO, "POMODORO");
                context.startActivity(intent);
            });
            
            holder.btnDeepWork.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, SesionEnfoqueActivity.class);
                intent.putExtra(SesionEnfoqueActivity.EXTRA_ACTIVIDAD, actividad);
                intent.putExtra(SesionEnfoqueActivity.EXTRA_MODO, "DEEPWORK");
                context.startActivity(intent);
            });
        }

        holder.btnVerDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleActividad.class);
            intent.putExtra(DetalleActividad.EXTRA_ACTIVIDAD_ID, actividad.getId());
            v.getContext().startActivity(intent);
        });

        if (actividad.getProgreso() < 100) {
            holder.btnRegistrarAvance.setVisibility(View.VISIBLE);
            holder.btnRegistrarAvance.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RegistrarAvance.class);
                intent.putExtra(RegistrarAvance.EXTRA_ID_ACTIVIDAD, actividad.getId());
                v.getContext().startActivity(intent);
            });
        } else {
            holder.btnRegistrarAvance.setVisibility(View.GONE);
        }

        holder.btnEliminar.setOnClickListener(v -> {
            mostrarDialogoConfirmacion(v.getContext(), actividad.getId(), holder.getBindingAdapterPosition());
        });
    }

    private void mostrarDialogoConfirmacion(Context context, String idActividad, int position) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar esta actividad?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (ActividadesDatos.getInstancia().eliminarActividad(idActividad)) {
                        listaActividades.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Actividad eliminada.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al eliminar la actividad.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return listaActividades.size();
    }

    public static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdNombre, tvFechaVencimiento, tvPrioridad, tvAvance, tvTipo, tvLugar;
        Button btnVerDetalles, btnRegistrarAvance, btnEliminar, btnPomodoro, btnDeepWork;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdNombre = itemView.findViewById(R.id.tv_id_nombre);
            tvFechaVencimiento = itemView.findViewById(R.id.tv_fecha_vencimiento);
            tvPrioridad = itemView.findViewById(R.id.tv_prioridad);
            tvAvance = itemView.findViewById(R.id.tv_avance);
            tvTipo = itemView.findViewById(R.id.tv_tipo);
            tvLugar = itemView.findViewById(R.id.tv_lugar);
            btnVerDetalles = itemView.findViewById(R.id.btn_ver_detalles);
            btnRegistrarAvance = itemView.findViewById(R.id.btn_registrar_avance);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
            btnPomodoro = itemView.findViewById(R.id.btn_iniciar_pomodoro);
            btnDeepWork = itemView.findViewById(R.id.btn_iniciar_deep_work);
        }
    }
}

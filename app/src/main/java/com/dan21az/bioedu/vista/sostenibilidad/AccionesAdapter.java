package com.dan21az.bioedu.vista.sostenibilidad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.dan21az.bioedu.R;

//Adapter encargado de mostrar la lista de acciones de sostenibilidad
public class AccionesAdapter extends RecyclerView.Adapter<AccionesAdapter.VH> {

    private final ArrayList<String> acciones;     // Lista de acciones a mostrar
    private final boolean[] checked;              // Estado de selección de cada acción

    //Inicializa el adapter con la lista de acciones y prepara el arreglo que controla cuáles están marcadas
    public AccionesAdapter(ArrayList<String> acciones) {
        this.acciones = (acciones == null) ? new ArrayList<>() : acciones;
        this.checked = new boolean[this.acciones.size()];
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {      // Construye la vista de cada elemento de la lista
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sostenibilidad_accion_checkbox, parent, false);
        return new VH(v);
    }
    @Override
    //Asigna el texto y el estado del CheckBox y actualiza el estado cuando el usuario marca o desmarca
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String texto = acciones.get(position);

        holder.cb.setOnCheckedChangeListener(null);
        holder.cb.setText((position + 1) + ". " + texto);
        holder.cb.setChecked(checked[position]);

        holder.cb.setOnCheckedChangeListener((buttonView, isChecked) -> checked[position] = isChecked);
    }

    public int getItemCount() { //retorna la cantidad de acciones
        return acciones.size();
    }

    public ArrayList<String> getAccionesSeleccionadas() { //Obtiene la lista de acciones que fueron seleccionadas
        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < acciones.size(); i++) {
            if (checked[i]) out.add(acciones.get(i));
        }
        return out;
    }

    //Marca como seleccionadas las acciones que ya existían previamente y actuliza la vista
    public void setSeleccionadas(ArrayList<String> seleccionadas) {
        for (int i = 0; i < checked.length; i++) checked[i] = false;
        if (seleccionadas != null) {
            for (int i = 0; i < acciones.size(); i++) {
                checked[i] = seleccionadas.contains(acciones.get(i));
            }
        }
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {     //ViewHolder que contiene el Checkbox de cada accion
        CheckBox cb;

        VH(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.cbAccion);
        }
    }
}


package com.dan21az.bioedu.vista.huellaverde;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.controlador.HuellaVerdeControladora;
import com.dan21az.bioedu.datos.HuellaVerdeDatos;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class RegistroAccionesSheet extends BottomSheetDialogFragment {

    private HuellaVerdeControladora controladora;
    private InternalAccionesAdapter adapter;
    private Runnable onDismissListener;

    public void setOnDismissListener(Runnable listener) {
        this.onDismissListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.huellaverde_sheet_registro_acciones, container, false);

        controladora = HuellaVerdeControladora.getInstance();
        RecyclerView rv = v.findViewById(R.id.rvAccionesRegistro);
        MaterialButton btnGuardar = v.findViewById(R.id.btnGuardarRegistro);

        adapter = new InternalAccionesAdapter(controladora.getAccionesDisponibles());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        String hoyTecnico = controladora.obtenerFechaActual();

        if (getContext() != null) {
            ArrayList<String> guardadasHoy = HuellaVerdeDatos.getInstance(getContext()).cargarAcciones(hoyTecnico);
            if (!guardadasHoy.isEmpty()) {
                adapter.setSeleccionadas(guardadasHoy);
            }
        }

        btnGuardar.setOnClickListener(view -> {
            ArrayList<String> seleccionadas = adapter.getAccionesSeleccionadas();
            if (getContext() != null) {
                controladora.guardarAccionesActuales(getContext(), hoyTecnico, seleccionadas);

                // Importante: Ejecutar el refresco antes de cerrar
                if (onDismissListener != null) {
                    onDismissListener.run();
                }
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View bottomSheet = (View) view.getParent();
        bottomSheet.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.md_theme_surfaceContainerLow)
        ));
    }

    // --- ADAPTER INTERNO (Elimina la necesidad de un archivo aparte) ---
    private static class InternalAccionesAdapter extends RecyclerView.Adapter<InternalAccionesAdapter.VH> {
        private final ArrayList<String> acciones;
        private final boolean[] checked;

        InternalAccionesAdapter(ArrayList<String> acciones) {
            this.acciones = acciones != null ? acciones : new ArrayList<>();
            this.checked = new boolean[this.acciones.size()];
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.huellaverde_item_accion_checkbox, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.cb.setOnCheckedChangeListener(null);
            holder.cb.setText((position + 1) + ". " + acciones.get(position));
            holder.cb.setChecked(checked[position]);
            holder.cb.setOnCheckedChangeListener((btn, isChecked) -> checked[position] = isChecked);
        }

        @Override
        public int getItemCount() { return acciones.size(); }

        ArrayList<String> getAccionesSeleccionadas() {
            ArrayList<String> out = new ArrayList<>();
            for (int i = 0; i < acciones.size(); i++) {
                if (checked[i]) out.add(acciones.get(i));
            }
            return out;
        }

        void setSeleccionadas(ArrayList<String> seleccionadas) {
            for (int i = 0; i < checked.length; i++) checked[i] = false;
            for (int i = 0; i < acciones.size(); i++) {
                if (seleccionadas.contains(acciones.get(i))) checked[i] = true;
            }
            notifyDataSetChanged();
        }

        static class VH extends RecyclerView.ViewHolder {
            CheckBox cb;
            VH(View itemView) { super(itemView); cb = itemView.findViewById(R.id.cbAccion); }
        }
    }
}

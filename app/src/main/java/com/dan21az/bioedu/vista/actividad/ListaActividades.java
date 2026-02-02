package com.dan21az.bioedu.vista.actividad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.ActividadesDatos;
import com.dan21az.bioedu.modelo.actividad.Actividad;

import java.util.ArrayList;
import java.util.List;

public class ListaActividades extends AppCompatActivity {

    private ActividadAdapter adapter;
    private Spinner spinnerFiltroTipo;
    private final String[] tiposFiltro = new String[] {"Todos", "Tareas", "Examen", "Proyecto", "Citas", "Ejercicio", "Hobbies"};
    private String filtroActual = "Todos";

    private Spinner spinnerOrdenar;
    private final String[] criteriosOrden = {"Nombre A-Z", "Fecha (desc)", "Avance (desc)"};
    private String ordenActual = "Nombre A-Z";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_lista_actividades);

        // Inicializar repositorio
        ActividadesDatos.getInstancia().inicializar(this);

        // Inicializar vistas
        RecyclerView recyclerView = findViewById(R.id.recycler_view_actividades);
        spinnerFiltroTipo = findViewById(R.id.spinner_filtro_tipo);
        spinnerOrdenar = findViewById(R.id.spinner_ordenar);
        Button btnAgregarActividad = findViewById(R.id.btn_agregar_actividad);

        btnAgregarActividad.setOnClickListener(v -> {
            Intent intent = new Intent(this, IngresarActividad.class);
            startActivity(intent);
        });

        // Configurar el RecyclerView
        adapter = new ActividadAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        configurarSpinnerFiltro();
        configurarSpinnerOrden();
    }

    private void configurarSpinnerFiltro() {
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposFiltro);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroTipo.setAdapter(adapterSpinner);

        spinnerFiltroTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroActual = tiposFiltro[position];
                aplicarFiltros();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configurarSpinnerOrden() {
        ArrayAdapter<String> adapterOrden = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, criteriosOrden);
        adapterOrden.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrdenar.setAdapter(adapterOrden);

        spinnerOrdenar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ordenActual = criteriosOrden[position];
                aplicarFiltros();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void aplicarFiltros() {
        List<Actividad> lista = ActividadesDatos.getInstancia().filtrarPorTipo(filtroActual);
        lista = ActividadesDatos.getInstancia().filtrarNoVencidas(lista);
        lista = ActividadesDatos.getInstancia().ordenarLista(lista, ordenActual);
        adapter.setLista(lista);
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicarFiltros();
    }
}

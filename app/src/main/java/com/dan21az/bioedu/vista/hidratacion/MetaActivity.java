package com.dan21az.bioedu.vista.hidratacion;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.HidratacionDatos;
import com.google.android.material.button.MaterialButton;


public class MetaActivity extends AppCompatActivity {

    private HidratacionDatos controladora;
    private EditText etNewMeta;
    private TextView tvCurrentMeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidratacion_meta);

        controladora = HidratacionDatos.getInstance(this);

        etNewMeta = findViewById(R.id.etNewMeta);
        tvCurrentMeta = findViewById(R.id.tvCurrentMeta);
        MaterialButton btnSave = findViewById(R.id.btnSaveMeta);
        MaterialButton btnCancel = findViewById(R.id.btnCancelMeta);

        // Mostrar meta actual
        tvCurrentMeta.setText(controladora.getMetaDiaria() + " ml");

        // Acción Guardar
        btnSave.setOnClickListener(v -> {
            String input = etNewMeta.getText().toString();
            if (!input.isEmpty()) {
                int nuevaMeta = Integer.parseInt(input);
                controladora.setMetaDiaria(nuevaMeta);

                Toast.makeText(this, "Meta actualizada", Toast.LENGTH_SHORT).show();
                finish(); // Esto cierra esta pantalla y vuelve a la anterior
            } else {
                etNewMeta.setError("Ingrese un valor");
            }
        });

        // Acción Cancelar
        btnCancel.setOnClickListener(v -> finish());
    }
}
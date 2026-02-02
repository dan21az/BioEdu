package com.dan21az.bioedu;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dan21az.bioedu.vista.sostenibilidad.SostenibilidadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private ExtendedFloatingActionButton fabRegistrar;
    private Fragment activo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        fabRegistrar = findViewById(R.id.fab_registrar);

        if (savedInstanceState == null) {
            getWindow().getDecorView().post(() -> {
                // Usamos el nuevo método optimizado
                cambiarFragmento(new SostenibilidadFragment(), "HUELLA");
            });
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_huella) {
                cambiarFragmento(new SostenibilidadFragment(), "HUELLA");
                return true;
            } else if (id == R.id.nav_hidratacion) {
                // cambiarFragmento(new HidratacionFragment(), "HIDRATACION");
                return true;
            } else if (id == R.id.nav_agenda) {
                // cambiarFragmento(new AgendaFragment(), "AGENDA");
                return true;
            } else if (id == R.id.nav_ajustes) {
                // Ajustes no es un fragmento, es otra Activity
                startActivity(new Intent(this, AjustesActivity.class));
                return false;
            }
            return false;
        });

        bottomNav.setOnItemReselectedListener(item -> {
            // Opcional: scroll to top si el usuario vuelve a presionar
        });
    }

    // MÉTODO OPTIMIZADO: Lazy Loading + Show/Hide
    private void cambiarFragmento(Fragment fragmentoNuevo, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Animación suave para que no sea brusco el cambio
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        Fragment temp = getSupportFragmentManager().findFragmentByTag(tag);

        if (activo != null) {
            transaction.hide(activo);
        }

        if (temp == null) {
            transaction.add(R.id.nav_host_fragment, fragmentoNuevo, tag);
            activo = fragmentoNuevo;
        } else {
            transaction.show(temp);
            activo = temp;
        }

        transaction.commit();
    }

    public void configurarFAB(String texto, int iconoRes, int colorFondo, int colorContenido, View.OnClickListener accion) {
        // Si mandamos texto nulo, escondemos el FAB (útil para secciones sin acción)
        if (texto == null) {
            fabRegistrar.hide();
            return;
        }

        fabRegistrar.setText(texto);
        fabRegistrar.setIconResource(iconoRes);
        fabRegistrar.setBackgroundTintList(ColorStateList.valueOf(colorFondo));
        fabRegistrar.setIconTint(ColorStateList.valueOf(colorContenido));
        fabRegistrar.setTextColor(ColorStateList.valueOf(colorContenido));
        fabRegistrar.setOnClickListener(accion);

        fabRegistrar.show();
        if (!fabRegistrar.isShown()) {
            fabRegistrar.show();
        }
    }

    public void actualizarEstiloBarra(int colorFondo, int colorPildora, int colorIconoActivo, int colorIconoInactivo) {
        bottomNav.setBackgroundColor(colorFondo);
        bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(colorPildora));

        int[][] estados = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] colores = new int[]{colorIconoActivo, colorIconoInactivo};
        ColorStateList listaColores = new ColorStateList(estados, colores);

        bottomNav.setItemIconTintList(listaColores);
        bottomNav.setItemTextColor(listaColores);
    }
}

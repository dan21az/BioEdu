package com.dan21az.bioedu.vista.sostenibilidad;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.dan21az.bioedu.MainActivity;
import com.dan21az.bioedu.R;
import com.dan21az.bioedu.controlador.SostenibilidadControladora;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SostenibilidadFragment extends Fragment {

    private SostenibilidadControladora controladora;
    private LinearLayout containerAcciones;
    private TextView tvDiasConAccion, tvDiasCompletos, tvRangoFechas, tvLogroGeneral, tvMensajeInspiracional, tvAccionesSemanal;
    private LinearProgressIndicator progressGeneral;
    private ImageView ivInspiracional;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Aplicamos el tema específico para esta sección
        ContextThemeWrapper contextConTema = new ContextThemeWrapper(getActivity(), R.style.Verde_Theme_MyApplication);
        LayoutInflater localInflater = inflater.cloneInContext(contextConTema);
        return localInflater.inflate(R.layout.activity_sostenibilidad_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controladora = SostenibilidadControladora.getInstance();

        // Enlace de vistas
        containerAcciones = view.findViewById(R.id.containerAcciones);
        tvDiasConAccion = view.findViewById(R.id.tvDiasConAccion);
        tvDiasCompletos = view.findViewById(R.id.tvDiasCompletos);
        tvRangoFechas = view.findViewById(R.id.tvRangoFechas);
        tvLogroGeneral = view.findViewById(R.id.tvLogroGeneral);
        progressGeneral = view.findViewById(R.id.progressGeneral);
        ivInspiracional = view.findViewById(R.id.ivInspiracional);
        tvMensajeInspiracional = view.findViewById(R.id.tvMensajeInspiracional);
        tvAccionesSemanal = view.findViewById(R.id.tvAccionesSemanal);

        // Listeners básicos
        ivInspiracional.setOnClickListener(v -> actualizarCardInspiracional());
        view.findViewById(R.id.btn_share_sostenibilidad).setOnClickListener(v -> prepararYCompartirRacha());

        // CONFIGURACIÓN INICIAL
        configurarInterfazGlobal();

        // OPTIMIZACIÓN: Cargamos los datos pesados después de dibujar la UI
        view.post(() -> {
            if (isAdded()) {
                cargarResumen();
            }
        });
    }

    /**
     * CRUCIAL: Este método detecta cuándo el usuario vuelve a esta pestaña
     * tras haber estado en otra (Agenda, Hidratación, etc.)
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) { // Si vuelve a ser visible
            configurarInterfazGlobal();
            cargarResumen(); // Refrescamos por si hubo cambios en la base de datos
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fallback para la primera carga o cuando la actividad se restaura
        configurarInterfazGlobal();
        cargarResumen();
    }

    private void configurarInterfazGlobal() {
        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();

            // Estilo de la barra inferior
            main.actualizarEstiloBarra(
                    ContextCompat.getColor(requireContext(), R.color.md_theme_surfaceContainer),
                    ContextCompat.getColor(requireContext(), R.color.md_theme_primaryContainer),
                    ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimaryContainer),
                    ContextCompat.getColor(requireContext(), R.color.md_theme_onSurfaceVariant)
            );

            // Estilo del FAB
            main.configurarFAB(
                    "Registrar",
                    R.drawable.eco_24px,
                    ContextCompat.getColor(requireContext(), R.color.md_theme_primary),
                    ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimary),
                    v -> {
                        RegistroAccionesSheet sheet = new RegistroAccionesSheet();
                        // Al cerrar el sheet, refrescamos los datos del fragmento
                        sheet.setOnDismissListener(this::cargarResumen);
                        sheet.show(getChildFragmentManager(), "RegistroAccionesSheet");
                    }
            );
        }
    }

    private void cargarResumen() {
        if (!isAdded() || getContext() == null) return;

        // Lógica de fechas
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String inicio = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        String fin = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.getTime());
        tvRangoFechas.setText("(" + inicio + " - " + fin + ")");

        // Obtención de datos del controlador
        SostenibilidadControladora.ResumenGeneral resumen = controladora.obtenerResumenCompleto(requireContext());

        progressGeneral.setMax(resumen.maxPuntos);
        progressGeneral.setProgressCompat(resumen.puntosTotales, true);
        tvLogroGeneral.setText(resumen.mensajeGlobal);
        tvAccionesSemanal.setText(resumen.puntosTotales + "/" + resumen.maxPuntos + " pts");

        actualizarCardInspiracional();

        // Inflado dinámico de la lista de acciones
        containerAcciones.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (SostenibilidadControladora.AccionResumen item : resumen.acciones) {
            View v = inflater.inflate(R.layout.item_sosteniblidad_frecuencia_accion, containerAcciones, false);

            ((TextView) v.findViewById(R.id.tvAccion)).setText(item.titulo);
            ((TextView) v.findViewById(R.id.tvVeces)).setText(item.actual + "/7");

            LinearProgressIndicator pg = v.findViewById(R.id.progressAccion);
            pg.setIndicatorColor(item.color);
            pg.setProgressCompat(item.actual, true);

            ImageView iv = v.findViewById(R.id.ivAccionIcon);
            iv.setImageResource(item.iconoResId);
            iv.setColorFilter(MaterialColors.getColor(v, com.google.android.material.R.attr.colorOnSurfaceVariant));

            containerAcciones.addView(v);
        }

        actualizarSeccionAnalisis();
    }

    // --- Resto de métodos de apoyo (Analisis, Compartir, etc.) ---

    private void actualizarCardInspiracional() {
        if (getContext() == null) return;
        SostenibilidadControladora.EcoTip tip = controladora.obtenerEcoTipAleatorio();
        tvMensajeInspiracional.setText(tip.mensaje);
        ivInspiracional.setImageResource(tip.iconoResId);
        ivInspiracional.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in));
    }

    private void actualizarSeccionAnalisis() {
        SostenibilidadControladora.ResumenHistorico global = controladora.calcularEstadisticasGlobales(requireContext());
        tvDiasConAccion.setText(String.valueOf(global.totalDiasConAccion));
        tvDiasCompletos.setText(String.valueOf(global.totalDiasPerfectos));
    }

    private void prepararYCompartirRacha() {
        SostenibilidadControladora.ResumenHistorico global = controladora.calcularEstadisticasGlobales(requireContext());
        View shareView = getLayoutInflater().inflate(R.layout.image_share_racha, null);
        ((TextView) shareView.findViewById(R.id.tvShareDias)).setText(String.valueOf(global.totalDiasConAccion));

        shareView.measure(
                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY)
        );
        shareView.layout(0, 0, shareView.getMeasuredWidth(), shareView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(shareView.getMeasuredWidth(), shareView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        shareView.draw(new Canvas(bitmap));
        compartirBitmap(bitmap);
    }

    private void compartirBitmap(Bitmap bitmap) {
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/racha_share.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", new File(cachePath, "racha_share.png"));

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "¡He completado " + tvDiasConAccion.getText() + " días sostenibles en BioEdu! 🌱");
            startActivity(Intent.createChooser(shareIntent, "Compartir mi logro"));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
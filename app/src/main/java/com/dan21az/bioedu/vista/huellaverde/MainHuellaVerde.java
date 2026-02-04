package com.dan21az.bioedu.vista.huellaverde;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.dan21az.bioedu.R;
import com.dan21az.bioedu.controlador.HuellaVerdeControladora;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainHuellaVerde extends AppCompatActivity {

    private HuellaVerdeControladora controladora;
    private final java.util.concurrent.ExecutorService executorService =
            java.util.concurrent.Executors.newSingleThreadExecutor();
    private LinearLayout containerAcciones;
    private TextView tvDiasConAccion, tvDiasCompletos, tvRangoFechas;
    private TextView tvLogroGeneral,tvMensajeInspiracional,tvAccionesSemanal;
    private LinearProgressIndicator progressGeneral;
    private ImageView ivInspiracional;
    private int colorBioIcon;

    // Ciclo de vida del Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huellaverde_activity_main);

        controladora = HuellaVerdeControladora.getInstance();

        iniciarVistas();
        configurarSistemaEdgeToEdge();
        animacionesLayouts();
        configurarListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarResumenAsincrono();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Apaga el hilo para evitar fugas de memoria (Memory Leaks)
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }


    // --- VISTAS ---
    private void iniciarVistas() {
        containerAcciones = findViewById(R.id.containerAcciones);
        tvDiasConAccion = findViewById(R.id.tvDiasConAccion);
        tvDiasCompletos = findViewById(R.id.tvDiasCompletos);
        tvRangoFechas = findViewById(R.id.tvRangoFechas);
        tvLogroGeneral = findViewById(R.id.tvLogroGeneral);
        progressGeneral = findViewById(R.id.progressGeneral);
        ivInspiracional = findViewById(R.id.ivInspiracional);
        tvMensajeInspiracional = findViewById(R.id.tvMensajeInspiracional);
        tvAccionesSemanal = findViewById(R.id.tvAccionesSemanal);

        // Estados iniciales
        containerAcciones.setAlpha(0f);
        tvLogroGeneral.setAlpha(0f);
        tvAccionesSemanal.setAlpha(0f);
        colorBioIcon = MaterialColors.getColor(this,
                com.google.android.material.R.attr.colorOnSurfaceVariant,
                android.graphics.Color.GRAY);


    }

    private void configurarSistemaEdgeToEdge() {





    }


    // --- ACCIONES ---
    private void configurarListeners() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarHuella);
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.ivInspiracional).setOnClickListener(v -> cambiarTipManual());

        findViewById(R.id.btn_share_sostenibilidad).setOnClickListener(v -> compartirRacha());

        ExtendedFloatingActionButton fab = findViewById(R.id.fabRegistrarAccion);
        fab.setOnClickListener(v -> {

            RegistroAccionesSheet sheet = new RegistroAccionesSheet();
            sheet.setOnDismissListener(() -> {
                cargarResumenAsincrono();
            });
            sheet.show(getSupportFragmentManager(), "RegistroAccionesSheet");
        });
    }


    // --- CARGA DE DATOS ---

    //Obtener los datos del controlador en un hilo a parte
    private void cargarResumenAsincrono() {
        executorService.execute(() -> {
            try {
                final HuellaVerdeControladora.ResumenGeneral resumen =
                        controladora.obtenerResumenCompleto(getApplicationContext());

                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed() && resumen != null) {
                        cargarResumen(resumen);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Cargar la interfaz con la información del resumen del controlador
    private void cargarResumen(HuellaVerdeControladora.ResumenGeneral resumen) {

        //Mostrar la semana acutal
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String inicio = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        String fin = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.getTime());
        tvRangoFechas.setText("(" + inicio + " - " + fin + ")");

        // Actualizar textos de Datos Semanales
        progressGeneral.setMax(resumen.maxPuntos);
        progressGeneral.setProgressCompat(resumen.puntosTotales, true);
        tvLogroGeneral.setText(resumen.mensajeGlobal);
        tvAccionesSemanal.setText(resumen.puntosTotales + "/" + resumen.maxPuntos + " pts");

        // Actualizar textos de Racha Histórica
        tvDiasConAccion.setText(String.valueOf(resumen.historico.totalDiasConAccion));
        tvDiasCompletos.setText(String.valueOf(resumen.historico.totalDiasPerfectos));

        // Actualizar los Bioindicadores
        containerAcciones = findViewById(R.id.containerAcciones);
        bioIndicadores(resumen.acciones);

        // Actualizar texto de Carta Inspiracional
        tvMensajeInspiracional.setText(resumen.tipDelDia.mensaje);
        ivInspiracional.setImageResource(resumen.tipDelDia.iconoResId);

        //Animaciones
        float targetAlpha = 1f;
        containerAcciones.animate().alpha(targetAlpha).setDuration(400).start();
        tvLogroGeneral.animate().alpha(targetAlpha).setDuration(400).start();
        tvAccionesSemanal.animate().alpha(targetAlpha).setDuration(400).start();
        tvDiasCompletos.animate().alpha(targetAlpha).setDuration(400).start();
        tvDiasConAccion.animate().alpha(targetAlpha).setDuration(400).start();

    }

    //Generar los BioIndicadores
    private void bioIndicadores(List<HuellaVerdeControladora.AccionResumen> acciones) {
        if (acciones == null) return;

        // Si ya hay vistas, solo actualiza valores sin recrear la interfaz
        if (containerAcciones.getChildCount() > 0) {
            for (int i = 0; i < acciones.size(); i++) {
                if (i < containerAcciones.getChildCount()) {
                    actualizarVistaIndicador(containerAcciones.getChildAt(i), acciones.get(i));
                }
            }

        } else { //Caso Contrario inflo los indicadores inflando el layout
        for (HuellaVerdeControladora.AccionResumen item : acciones) {
            View v = getLayoutInflater().inflate(R.layout.huellaverde_item_bioindicador, containerAcciones, false);
            actualizarVistaIndicador(v, item);
            v.setAlpha(0f);
            v.setTranslationY(30f);
            containerAcciones.addView(v);
        }
        animacionBioIndicadores();}
    }

    //Actualizar la interfaz de Bioindicadores
    private void actualizarVistaIndicador(View v, HuellaVerdeControladora.AccionResumen item) {

        ((TextView) v.findViewById(R.id.tvAccion)).setText(item.titulo);
        ((TextView) v.findViewById(R.id.tvVeces)).setText(item.actual + "/7");

        LinearProgressIndicator pg = v.findViewById(R.id.progressAccion);
        pg.setIndicatorColor(item.color);
        pg.setProgressCompat(item.actual, true);

        ImageView ivIcono = v.findViewById(R.id.ivAccionIcon);
        ivIcono.setImageResource(item.iconoResId);
        ivIcono.setColorFilter(colorBioIcon);
    }


    // --- ANIMACIONES ---

    // Animaciones de las CardViews al generar todos los datos
    private void animacionesLayouts() {
        // Transición para el contenedor de acciones
        LayoutTransition lt = new LayoutTransition();
        lt.enableTransitionType(LayoutTransition.CHANGING);
        lt.setDuration(400);
        containerAcciones.setLayoutTransition(lt);

        // Transición para el contenido principal
        ViewGroup mainContent = findViewById(R.id.mainContentLayout);
        if (mainContent != null) {
            LayoutTransition mainLt = new LayoutTransition();
            mainLt.enableTransitionType(LayoutTransition.CHANGING);
            mainLt.setDuration(LayoutTransition.CHANGING, 300);
            mainContent.setLayoutTransition(mainLt);
        }
    }

    //Animación de entrada de los bioindicadres
    private void animacionBioIndicadores() {
        containerAcciones.setLayoutTransition(null);
        containerAcciones.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                containerAcciones.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < containerAcciones.getChildCount(); i++) {
                    containerAcciones.getChildAt(i).animate()
                            .alpha(1f).translationY(0f)
                            .setDuration(400).setStartDelay(i * 60L)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                }
                return true;
            }
        });
    }


    // --- COMPARTIR Y OTRAS ACCIONES ---

    // Cambiar tip inspiracional
    private void cambiarTipManual() {
        // Pedimos un tip nuevo
        HuellaVerdeControladora.EcoTip nuevoTip = controladora.obtenerEcoTipAleatorio();

        // Animación y cambio
        ivInspiracional.clearAnimation();
        tvMensajeInspiracional.clearAnimation();

        tvMensajeInspiracional.setText(nuevoTip.mensaje);
        ivInspiracional.setImageResource(nuevoTip.iconoResId);

        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        ivInspiracional.startAnimation(fadeIn);
        tvMensajeInspiracional.startAnimation(fadeIn);
    }

    // Compartir la racha
    private void compartirRacha() {
        // 1. Inflar el layout de la tarjeta de compartir (esta fuera de la vista)

        View shareView = getLayoutInflater().inflate(R.layout.image_share_racha, null);
        TextView tvDias = shareView.findViewById(R.id.tvShareDias);
        tvDias.setText(tvDiasConAccion.getText());
        TextView tvDiasContexto = shareView.findViewById(R.id.shareDiaContexto);
        tvDiasContexto.setText("Dias cuidando el planeta");
        TextView tvracha = shareView.findViewById(R.id.shareTvRacha);
        tvracha.setText("Racha Ecológica");
        ImageView shareIcon = shareView.findViewById(R.id.shareIcon);
        shareIcon.setImageResource(R.drawable.eco_24px);
        MaterialCardView card = shareView.findViewById(R.id.shareCardView);
        card.setCardBackgroundColor(getColor(android.R.color.holo_green_dark));

        // 2. Darle tamaño
        shareView.measure(
                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY)
        );
        shareView.layout(0, 0, shareView.getMeasuredWidth(), shareView.getMeasuredHeight());

        // 3. Convertir a Bitmap
        Bitmap bitmap = Bitmap.createBitmap(shareView.getMeasuredWidth(),
                shareView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        shareView.draw(canvas);

        // 4. Compartir
        compartirBitmap(bitmap);
    }
    private void compartirBitmap(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/racha_share.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imageFile = new File(cachePath, "racha_share.png");
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "¡He completado " + tvDiasConAccion.getText() + " días de acciones sostenibles en BioEdu! 🌱");
            startActivity(Intent.createChooser(shareIntent, "Compartir mi logro"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

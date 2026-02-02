package com.dan21az.bioedu.vista.sostenibilidad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

public class MainSostenibilidadActivity extends AppCompatActivity {

    private SostenibilidadControladora controladora;
    private LinearLayout containerAcciones;
    private TextView tvDiasConAccion, tvDiasCompletos, tvRangoFechas, tvLogroGeneral,tvMensajeInspiracional,tvAccionesSemanal;
    private LinearProgressIndicator progressGeneral;
    private ImageView ivInspiracional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sostenibilidad_main);

        controladora = SostenibilidadControladora.getInstance();

        // Referencias de la UI
        containerAcciones = findViewById(R.id.containerAcciones);
        tvDiasConAccion = findViewById(R.id.tvDiasConAccion);
        tvDiasCompletos = findViewById(R.id.tvDiasCompletos);
        tvRangoFechas = findViewById(R.id.tvRangoFechas);
        tvLogroGeneral = findViewById(R.id.tvLogroGeneral);
        progressGeneral = findViewById(R.id.progressGeneral);
        ivInspiracional = findViewById(R.id.ivInspiracional);
        tvMensajeInspiracional = findViewById(R.id.tvMensajeInspiracional);
        tvAccionesSemanal= findViewById(R.id.tvAccionesSemanal);

        findViewById(R.id.ivInspiracional).setOnClickListener(v ->
                actualizarCardInspiracional());

        // Configuración de botones

        findViewById(R.id.btn_share_sostenibilidad).setOnClickListener(v ->
                prepararYCompartirRacha());

        cargarResumen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarResumen(); // Refrescar datos al volver de registrar
    }

    private void cargarResumen() {
        // 1. Rango de fechas (Optimizado para mostrar el inicio de semana real)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String inicio = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        String fin = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.getTime());
        tvRangoFechas.setText("(" + inicio + " - " + fin + ")");

        // 2. Obtener datos
        SostenibilidadControladora.ResumenGeneral resumen = controladora.obtenerResumenCompleto(this);
        SostenibilidadControladora.ResumenSemanal stats = controladora.calcularEstadisticas(this);

        // 3. Barra General e Impacto
        progressGeneral.setMax(resumen.maxPuntos);
        progressGeneral.setProgressCompat(resumen.puntosTotales, true);
        // Cambiamos el texto para que diga los puntos
        tvLogroGeneral.setText(resumen.mensajeGlobal);
        tvAccionesSemanal.setText(resumen.puntosTotales + "/" + resumen.maxPuntos + " pts");

        // 4. Card Inspiracional (Nueva lógica)
        actualizarCardInspiracional();

        // 5. Bioindicadores (Iconos horizontales)
        containerAcciones.removeAllViews();
        for (SostenibilidadControladora.AccionResumen item : resumen.acciones) {
            View v = getLayoutInflater().inflate(R.layout.item_sosteniblidad_frecuencia_accion, containerAcciones, false);

            ((TextView) v.findViewById(R.id.tvAccion)).setText(item.titulo);
            ((TextView) v.findViewById(R.id.tvVeces)).setText(item.actual + "/7");

            LinearProgressIndicator pg = v.findViewById(R.id.progressAccion);
            pg.setIndicatorColor(item.color);
            pg.setProgressCompat(item.actual, true);

            ImageView iv = v.findViewById(R.id.ivAccionIcon);
            iv.setImageResource(item.iconoResId);
            int colorIcon = MaterialColors.getColor(v, com.google.android.material.R.attr.colorOnSurfaceVariant);
            iv.setColorFilter(colorIcon);

            containerAcciones.addView(v);
        }

        actualizarSeccionAnalisis();
    }

    private void actualizarCardInspiracional() {
        SostenibilidadControladora.EcoTip tip = controladora.obtenerEcoTipAleatorio();
        tvMensajeInspiracional.setText(tip.mensaje);
        ivInspiracional.setImageResource(tip.iconoResId);
        ivInspiracional.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        tvMensajeInspiracional.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
    }

    private void actualizarSeccionAnalisis() {
        SostenibilidadControladora.ResumenHistorico global = controladora.calcularEstadisticasGlobales(this);

        // Actualizamos la Card de Racha con los totales históricos
        tvDiasConAccion.setText(String.valueOf(global.totalDiasConAccion));
        tvDiasCompletos.setText(String.valueOf(global.totalDiasPerfectos));
    }

    private void prepararYCompartirRacha() {
        // 1. Inflar el layout de la tarjeta de compartir (esta fuera de la vista)
        SostenibilidadControladora.ResumenHistorico global = controladora.calcularEstadisticasGlobales(this);
        View shareView = getLayoutInflater().inflate(R.layout.image_share_racha, null);
        TextView tvDias = shareView.findViewById(R.id.tvShareDias);
        tvDias.setText(String.valueOf(global.totalDiasConAccion));

        // 2. Darle tamaño manualmente (porque no está pegada a la pantalla)
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

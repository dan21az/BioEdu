package com.dan21az.bioedu.controlador;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import androidx.core.app.NotificationCompat;

import com.dan21az.bioedu.AjustesActivity;
import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.ActividadesDatos;
import com.dan21az.bioedu.modelo.actividad.Academica;
import com.dan21az.bioedu.modelo.actividad.Actividad;
import com.dan21az.bioedu.modelo.sesionenfoque.DeepWork;
import com.dan21az.bioedu.modelo.sesionenfoque.Pomodoro;
import com.dan21az.bioedu.modelo.sesionenfoque.SesionEnfoque;
import com.dan21az.bioedu.vista.sesionEnfoque.SesionEnfoqueActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EnfoqueService extends Service {

    public static final String ACCION_INICIAR = "com.dan21az.bioedu.INICIAR";
    public static final String ACCION_PAUSA = "com.dan21az.bioedu.PAUSA";
    public static final String ACCION_PARAR = "com.dan21az.bioedu.PARAR";
    public static final String ACCION_TIEMPO_UPDATE = "com.dan21az.bioedu.TIEMPO_UPDATE";
    public static final String ACCION_SESION_COMPLETA = "com.dan21az.bioedu.SESION_COMPLETA";
    public static final String ACCION_SOLICITAR_ESTADO = "com.dan21az.bioedu.SOLICITAR_ESTADO";
    public static final String EXTRA_MODO = "extra_modo";
    public static final String EXTRA_TIEMPOMS = "EXTRA_TIEMPOMS";
    private static final String CHANNEL_POMODORO = "canal_pomodoro";
    private static final String CHANNEL_DEEPWORK = "canal_deepwork";
    private static final String CHANNEL_GROUP_ID = "grupo_sesiones";
    private static final int NOTIFICATION_ID = 505;

    public enum Estado { CORRIENDO, PAUSADO, DETENIDO }
    public enum Fase { ENFOQUE, DESCANSO }

    private Estado estadoActual = Estado.DETENIDO;
    private Fase faseActual = Fase.ENFOQUE;
    private String modoActual = "POMODORO";

    private CountDownTimer temporizador;
    private long tiempoRestanteMs;
    private SesionEnfoque sesionActual;
    private Actividad actividadUsada;
    private int ciclosTotal = 1;


    @Override
    public void onCreate() {
        super.onCreate();
        crearCanalNotificacion();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) return START_STICKY;

        String action = intent.getAction();

        // Solo lanzar el foreground si vamos a iniciar una sesión nueva
        if (action.equals(ACCION_INICIAR)) {
            if (estadoActual == Estado.DETENIDO) {
                modoActual = intent.getStringExtra(EXTRA_MODO);
                actividadUsada = (Actividad) intent.getSerializableExtra("extra_actividad");
            }
            lanzarForeground();
        } else if (estadoActual != Estado.DETENIDO) {
            // Si el servicio ya está activo, mantenemos el foreground
            lanzarForeground();
        }

        switch (action) {
            case ACCION_INICIAR:
                if (estadoActual == Estado.DETENIDO) configurarSesion(intent);
                else if (estadoActual == Estado.PAUSADO) iniciarTemp(tiempoRestanteMs);
                break;
            case ACCION_PAUSA:
                pausaTemp();
                break;
            case ACCION_PARAR:
                detenerTemp();
                stopSelf();
                break;
            case ACCION_SOLICITAR_ESTADO:
                sendUpdateBroadcast();
                // IMPORTANTE: Si solo piden estado y estamos DETENIDOS,
                // no llamamos a lanzarForeground y dejamos que el servicio muera.
                if (estadoActual == Estado.DETENIDO) stopSelf();
                break;
        }
        return START_STICKY;
    }

    private void configurarSesion(Intent intent) {
        // Aseguramos que el modo sea capturado correctamente (usa la constante)
        modoActual = intent.getStringExtra(EXTRA_MODO);
        if (modoActual == null) modoActual = "POMODORO";

        actividadUsada = (Actividad) intent.getSerializableExtra("extra_actividad");
        long duracionMs = intent.getLongExtra("DURACION", 25 * 60 * 1000L);
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        // LOGS para depuración (puedes verlos en Logcat)
        android.util.Log.d("ENFOQUE_SERVICE", "Iniciando modo: " + modoActual + " Duración: " + duracionMs);

        if ("DEEPWORK".equals(modoActual)) {
            ciclosTotal = 1;

            sesionActual = new DeepWork(duracionMs, fecha, actividadUsada);
        } else {
            ciclosTotal = intent.getIntExtra("CICLOS", 4);

            sesionActual = new Pomodoro(duracionMs, fecha, actividadUsada, ciclosTotal, 5);
        }

        faseActual = Fase.ENFOQUE;
        iniciarTemp(duracionMs);
    }

    // ---- Temporizador de Tiempo y sus Metodos //

    private void iniciarTemp(long duration) {
        if (temporizador != null) temporizador.cancel();

        temporizador = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millis) {
                tiempoRestanteMs = millis;
                sendUpdateBroadcast();
                actualizarNotificacion();
            }

            @Override
            public void onFinish() {
                gestionarFinDeFase();
            }
        }.start();

        estadoActual = Estado.CORRIENDO;
        lanzarForeground();
    }

    private void pausaTemp() {
        if (temporizador != null) {
            temporizador.cancel();
            estadoActual = Estado.PAUSADO;
            sendUpdateBroadcast();
            actualizarNotificacion();
        }
    }

    private void detenerTemp() {
        if (temporizador != null) temporizador.cancel();
        estadoActual = Estado.DETENIDO;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        sendUpdateBroadcast();
    }

    private void gestionarFinDeFase() {
        vibrarDispositivo();

        if ("DEEPWORK".equals(modoActual)) {
            finalizarYGuardar();
            return;
        }

        if (faseActual == Fase.ENFOQUE) {
            if (((Pomodoro)sesionActual).getCicloActual() >= ciclosTotal) {
                finalizarYGuardar();
            } else {
                faseActual = Fase.DESCANSO;

                long tiempoDescanso;
                //Comparar directamente con los milisegundos del modelo
                if (sesionActual.getDuracionEnfoqueMs() == 10000L) {
                    tiempoDescanso = 5 * 1000L; // 5 segundos
                } else {
                    tiempoDescanso = 5 * 60 * 1000L; // 5 minutos
                }
                iniciarTemp(tiempoDescanso);
            }
        } else {
            faseActual = Fase.ENFOQUE;
            ((Pomodoro)sesionActual).avanzarCiclo();
            long duracionMs = sesionActual.getDuracionEnfoqueMs();
            iniciarTemp(duracionMs);
        }
    }

    private void finalizarYGuardar() {

        if (actividadUsada instanceof Academica) {
            if (sesionActual instanceof Pomodoro) {
                Pomodoro p = (Pomodoro) sesionActual;
                long tiempoTotalReal = p.getDuracionEnfoqueMs() * p.getCicloActual();
                p.setDuracionEnfoqueMs(tiempoTotalReal);
            }
            ((Academica) actividadUsada).anadirSesion(sesionActual);
            //Actualizar la actividad con sus sesiones
            ActividadesDatos.getInstancia().actualizarActividad(actividadUsada);
        }

        // Notificar a la Activity para que limpie la interfaz
        Intent intentCompleta = new Intent(ACCION_SESION_COMPLETA);
        intentCompleta.setPackage(getPackageName());
        sendBroadcast(intentCompleta);

        // Notificación de finalizado
        mostrarNotificacionFinal();

        // Detener servicio
        detenerTemp();
        stopSelf();
    }

    // --- Notificaciones ---

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            // Crear el grupo de notificaciones
            NotificationChannelGroup group = new NotificationChannelGroup(CHANNEL_GROUP_ID, "Sesiones de Enfoque");
            manager.createNotificationChannelGroup(group);

            // Canal para Pomodoro
            NotificationChannel chanPomo = new NotificationChannel(
                    CHANNEL_POMODORO, "Pomodoro", NotificationManager.IMPORTANCE_HIGH);
            chanPomo.setGroup(CHANNEL_GROUP_ID);
            chanPomo.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Canal para DeepWork
            NotificationChannel chanDeep = new NotificationChannel(
                    CHANNEL_DEEPWORK, "Deep Work", NotificationManager.IMPORTANCE_HIGH);
            chanDeep.setGroup(CHANNEL_GROUP_ID);
            chanDeep.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            manager.createNotificationChannel(chanPomo);
            manager.createNotificationChannel(chanDeep);
        }
    }


    private Notification construirNotificacion() {
        String canalActivo = "DEEPWORK".equals(modoActual) ? CHANNEL_DEEPWORK : CHANNEL_POMODORO;

        String titulo = ("DEEPWORK".equals(modoActual) ? "DeepWork" : "Pomodoro")
                + ": " + (actividadUsada != null ? actividadUsada.getNombre() : "");
        String contenido = String.format(Locale.getDefault(), "%s - %02d:%02d",
                faseActual == Fase.DESCANSO ? "Descanso" : "Enfoque",
                (tiempoRestanteMs / 1000) / 60, (tiempoRestanteMs / 1000) % 60);

        Intent intent = new Intent(this, SesionEnfoqueActivity.class);
        intent.putExtra(SesionEnfoqueActivity.EXTRA_ACTIVIDAD, actividadUsada);
        intent.putExtra(SesionEnfoqueActivity.EXTRA_MODO, modoActual);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= 26) { // Versiones mayores a Android 8
            Bundle extras = new Bundle();

            Notification.Builder builder = new Notification.Builder(this, canalActivo)
                    .setSmallIcon(R.drawable.timer_24px)
                    .setContentTitle(titulo)
                    .setContentText(contenido)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setGroup(CHANNEL_GROUP_ID)
                    .setContentIntent(pi)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);

            if (Build.VERSION.SDK_INT >= 36) { // Soporte para Android 16

                // Solicitar promoción a Live Update vía extras
                extras.putBoolean("android.requestPromotedOngoing", true);

                // Configurar el chip de estado
                String textoChip = String.format(Locale.getDefault(), "%02d:%02d",
                        (tiempoRestanteMs / 1000) / 60, (tiempoRestanteMs / 1000) % 60);

                builder.setShortCriticalText(textoChip).setExtras(extras);

            }
            // Acciones
            if (estadoActual == Estado.CORRIENDO) {
                builder.addAction(new Notification.Action.Builder(
                        Icon.createWithResource(this, R.drawable.timer_24px), "Pausar", crearPendingIntent(ACCION_PAUSA)).build());
            } else {
                builder.addAction(new Notification.Action.Builder(
                        Icon.createWithResource(this, R.drawable.timer_24px), "Reanudar", crearPendingIntent(ACCION_INICIAR)).build());
            }
            builder.addAction(new Notification.Action.Builder(
                    Icon.createWithResource(this, R.drawable.timer_24px), "Detener", crearPendingIntent(ACCION_PARAR)).build());

            return builder.build();
        } else { // Versiones anteriores a Android 8

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalActivo)
                    .setSmallIcon(R.drawable.timer_24px)
                    .setContentTitle(titulo)
                    .setContentText(contenido)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setGroup(CHANNEL_GROUP_ID)
                    .setContentIntent(pi)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            if (estadoActual == Estado.CORRIENDO) {
                builder.addAction(0, "Pausar", crearPendingIntent(ACCION_PAUSA));
            } else {
                builder.addAction(0, "Reanudar", crearPendingIntent(ACCION_INICIAR));
            }
            builder.addAction(0, "Detener", crearPendingIntent(ACCION_PARAR));

            return builder.build();
        }
    }

    //Acción del botón de la notificación
    private PendingIntent crearPendingIntent(String accion) {
        Intent i = new Intent(this, EnfoqueService.class).setAction(accion);
        return PendingIntent.getService(this, accion.hashCode(), i, PendingIntent.FLAG_IMMUTABLE);
    }

    //Actualiza el tiempo en la notificación
    private void actualizarNotificacion() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, construirNotificacion());
    }

    // Crear el servicion de Fondo
    private void lanzarForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, construirNotificacion(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, construirNotificacion());
        }
    }

    //Al finalziar, muestra la sesion completa
    private void mostrarNotificacionFinal() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Usar el canal según el modo que se acaba de terminar
        String canalActivo = "DEEPWORK".equals(modoActual) ? CHANNEL_DEEPWORK : CHANNEL_POMODORO;
        String nombreActividad = (actividadUsada != null) ? actividadUsada.getNombre() : "";

        Notification finalNotif = new NotificationCompat.Builder(this, canalActivo)
                .setSmallIcon(R.drawable.timer_24px)
                .setContentTitle("¡Sesión Finalizada!")
                .setContentText("Has completado tu enfoque en: " + nombreActividad)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Se borra al tocarla
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .build();

        // Usar un ID diferente al del temporizador
        manager.notify(506, finalNotif);
    }

    // --- Comunicación y Otros ---

    //Enviar la actualización del tiempo y datos al activity
    private void sendUpdateBroadcast() {
        Intent intent = new Intent(ACCION_TIEMPO_UPDATE);
        intent.setPackage(getPackageName());

        intent.putExtra(EXTRA_TIEMPOMS, tiempoRestanteMs);
        intent.putExtra("ESTADO", estadoActual.name());
        intent.putExtra("FASE", faseActual.name());
        intent.putExtra("MODO", modoActual);

        // Enviamos el ID y Nombre de la actividad que posee el servicio
        if (actividadUsada != null) {
            intent.putExtra("ACTIVIDAD_ID", String.valueOf(actividadUsada.getId()));
            intent.putExtra("ACTIVIDAD_NOMBRE", actividadUsada.getNombre());
        }

        if (sesionActual instanceof Pomodoro) {
            intent.putExtra("CICLO", ((Pomodoro)sesionActual).getCicloActual());
            intent.putExtra("CICLOSTOTAL", ciclosTotal);
        }
        sendBroadcast(intent);
    }

    // Vibrar el dispositivo
    private void vibrarDispositivo() {

        // Acceder a las preferencias de los ajustes (Vibrar o no)
        SharedPreferences prefs = getSharedPreferences(AjustesActivity.PREF_NAME, MODE_PRIVATE);
        if (!prefs.getBoolean(AjustesActivity.KEY_VIBRACION_ACTIVADA, true)) return;

        //Crear el vibrador segun la version de android
        Vibrator v;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //Para versiones nuevas
            v = ((VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE)).getDefaultVibrator();
        } else { //Para versiones antiguas
            v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        //Usar el vibrador según la versión de android
        if (v != null && v.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(new long[]{0, 500, 200, 500}, -1));
            } else {
                v.vibrate(new long[]{0, 500, 200, 500}, -1);
            }
        }
    }

    @Override public IBinder onBind(Intent intent) { return null; }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // Si el usuario cierra la app, detenemos el cronómetro y quitamos la notificación
        detenerTemp();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
}

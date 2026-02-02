package com.dan21az.bioedu.controlador;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.SostenibilidadDatos;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SostenibilidadControladora {

    private static SostenibilidadControladora instance;
    public static final int TOTAL_DIAS_SEMANA = 7;
    public static final int MAX_PUNTOS_POSIBLES = 28;

    public static final String A1_TRANSPORTE = "Usé transporte público, bicicleta o caminé.";
    public static final String A2_IMPRESIONES = "No realicé impresiones.";
    public static final String A3_ENVASES = "No utilicé envases descartables (usé mi termo/taza).";
    public static final String A4_RECICLAJE = "Separé y reciclé materiales (vidrio, plástico, papel).";

    public static SostenibilidadControladora getInstance() {
        if (instance == null) instance = new SostenibilidadControladora();
        return instance;
    }

    private SostenibilidadControladora() {}

    // --- NUEVOS MÉTODOS PARA EL DISEÑO DE CARDS ---

    public static class EcoTip {
        public final int iconoResId;
        public final String mensaje;

        public EcoTip(int iconoResId, String mensaje) {
            this.iconoResId = iconoResId;
            this.mensaje = mensaje;
        }
    }

    public EcoTip obtenerEcoTipAleatorio() {
        EcoTip[] tips = {
                new EcoTip(R.drawable.noun_sprout_5694994, "Un solo árbol puede absorber hasta 150kg de CO2 al año."),
                /*Cambiar*/new EcoTip(R.drawable.noun_book_5694993, "Caminar o usar bici reduce tu huella de carbono un 67%."),
                new EcoTip(R.drawable.noun_beach_4906920, "Evitar plásticos salva la vida de miles de especies marinas."),
                new EcoTip(R.drawable.noun_tree_5377571, "Ahorrar papel protege los bosques y la biodiversidad."),
                new EcoTip(R.drawable.noun_renewable_energy_5377587, "Reciclar aluminio ahorra el 95% de energía de fabricación."),
                new EcoTip(R.drawable.noun_renewable_energy_5377593, "Apagar luces innecesarias ayuda a reducir el consumo global."), // Asegúrate de tener estos drawables
                new EcoTip(R.drawable.noun_recycle_bag_5377597, "Una bolsa de tela reemplaza a 1000 de plástico en su vida útil."),
                new EcoTip(R.drawable.noun_save_water_5377595, "Cerrar el grifo al cepillarte ahorra hasta 12 litros por minuto."),
                new EcoTip(R.drawable.noun_wind_energy_5377585, "La energía del sol es limpia, infinita y gratuita."),
                new EcoTip(R.drawable.noun_tulip_5625817,"Compostar reduce tus residuos domésticos hasta en un 40%.")
        };
        return tips[(int) (Math.random() * tips.length)];
    }

    // --- MÉTODOS EXISTENTES ---

    public String obtenerFechaActual() {
        return obtenerFechaDiasAtras(0);
    }

    public String obtenerFechaDiasAtras(int diasAtras) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -diasAtras);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
    }

    public ArrayList<String> getAccionesDisponibles() {
        ArrayList<String> acciones = new ArrayList<>();
        acciones.add(A1_TRANSPORTE);
        acciones.add(A2_IMPRESIONES);
        acciones.add(A3_ENVASES);
        acciones.add(A4_RECICLAJE);
        return acciones;
    }

    public ResumenGeneral obtenerResumenCompleto(Context ctx) {
        List<AccionResumen> acciones = generarBioindicadores(ctx);
        int puntosTotales = 0;
        for (AccionResumen a : acciones) puntosTotales += a.actual;

        String mensajeGlobal = determinarMensajeMotivador(puntosTotales);
        return new ResumenGeneral(acciones, puntosTotales, mensajeGlobal);
    }

    private List<AccionResumen> generarBioindicadores(Context ctx) {
        ResumenSemanal stats = calcularEstadisticas(ctx);
        List<AccionResumen> lista = new ArrayList<>();

        int verde = ContextCompat.getColor(ctx, R.color.verdePrimary);
        int naranja = ContextCompat.getColor(ctx, R.color.naranjaPrimary);
        int rojo = ContextCompat.getColor(ctx, R.color.rojoPrimary);

        lista.add(new AccionResumen("Movilidad", stats.vTransporte, TOTAL_DIAS_SEMANA,
                obtenerColorSegunProgreso(stats.vTransporte, verde, naranja, rojo), R.drawable.subway_walk_24px));

        lista.add(new AccionResumen("Papel", stats.vImpresiones, TOTAL_DIAS_SEMANA,
                obtenerColorSegunProgreso(stats.vImpresiones, verde, naranja, rojo), R.drawable.print_24px));

        lista.add(new AccionResumen("Plásticos", stats.vEnvases, TOTAL_DIAS_SEMANA,
                obtenerColorSegunProgreso(stats.vEnvases, verde, naranja, rojo), R.drawable.water_bottle_large_24px));

        lista.add(new AccionResumen("Reciclaje", stats.vReciclaje, TOTAL_DIAS_SEMANA,
                obtenerColorSegunProgreso(stats.vReciclaje, verde, naranja, rojo), R.drawable.reciclaje_24px));

        return lista;
    }

    private String determinarMensajeMotivador(int puntos) {
        if (puntos >= 25) return "¡Eres un Héroe de la Naturaleza! 🌟";
        if (puntos >= 14) return "¡Buen trabajo semanal! 🌱";
        if (puntos >= 1) return "Cada acción cuenta, ¡sigue así! ✨";
        return "¡Comienza tu racha ecológica hoy! 🌍";
    }

    private int obtenerColorSegunProgreso(int valor, int v, int n, int r) {
        if (valor >= 6) return v;
        if (valor >= 3) return n;
        return r;
    }

    public ResumenSemanal calcularEstadisticas(Context ctx) {
        SostenibilidadDatos datos = SostenibilidadDatos.getInstance(ctx);
        ResumenSemanal res = new ResumenSemanal();
        Calendar cal = Calendar.getInstance();
        int primerDiaSemana = cal.getFirstDayOfWeek();
        int diaHoy = cal.get(Calendar.DAY_OF_WEEK);
        int diasTranscurridosEstaSemana = (diaHoy - primerDiaSemana + 7) % 7;

        for (int i = 0; i <= diasTranscurridosEstaSemana; i++) {
            String fecha = obtenerFechaDiasAtras(i);
            ArrayList<String> acc = datos.cargarAcciones(fecha);
            if (!acc.isEmpty()) res.diasConAlMenosUna++;
            if (acc.size() == 4) res.diasCompletos++;
            for (String a : acc) {
                String s = a.toLowerCase();
                if (s.contains("transporte") || s.contains("bic") || s.contains("camin")) res.vTransporte++;
                else if (s.contains("impresion")) res.vImpresiones++;
                else if (s.contains("envase") || s.contains("termo") || s.contains("taza")) res.vEnvases++;
                else if (s.contains("recicl")) res.vReciclaje++;
            }
        }
        return res;
    }

    public ResumenHistorico calcularEstadisticasGlobales(Context ctx) {
        SostenibilidadDatos datos = SostenibilidadDatos.getInstance(ctx);
        ResumenHistorico historico = new ResumenHistorico();

        // Obtenemos las fechas directamente de la lista cargada en memoria
        List<String> todasLasFechas = datos.obtenerTodasLasFechasRegistradas();

        for (String fecha : todasLasFechas) {
            ArrayList<String> acciones = datos.cargarAcciones(fecha);

            if (!acciones.isEmpty()) {
                historico.totalDiasConAccion++;
            }

            if (acciones.size() == 4) {
                historico.totalDiasPerfectos++;
            }
        }

        return historico;
    }

    // --- CLASES DE SOPORTE ---

    public static class ResumenGeneral {
        public final List<AccionResumen> acciones;
        public final int puntosTotales;
        public final String mensajeGlobal;
        public final int maxPuntos = MAX_PUNTOS_POSIBLES;

        public ResumenGeneral(List<AccionResumen> acciones, int puntosTotales, String mensajeGlobal) {
            this.acciones = acciones;
            this.puntosTotales = puntosTotales;
            this.mensajeGlobal = mensajeGlobal;
        }
    }

    public static class AccionResumen {
        public final String titulo;
        public final int actual, total, color, iconoResId;

        public AccionResumen(String titulo, int actual, int total, int color, int iconoResId) {
            this.titulo = titulo;
            this.actual = actual;
            this.total = total;
            this.color = color;
            this.iconoResId = iconoResId;
        }
    }

    public static class ResumenSemanal {
        public int vTransporte = 0, vImpresiones = 0, vEnvases = 0, vReciclaje = 0;
        public int diasConAlMenosUna = 0, diasCompletos = 0;
    }

    public static class ResumenHistorico {
        public int totalDiasConAccion = 0;
        public int totalDiasPerfectos = 0;
    }
}



package com.dan21az.bioedu.controlador;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.dan21az.bioedu.R;
import com.dan21az.bioedu.datos.MenuDatos;
import com.dan21az.bioedu.datos.HuellaVerdeDatos;
import com.dan21az.bioedu.modelo.huellaverde.RegistroSostenible;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HuellaVerdeControladora {

    private static HuellaVerdeControladora instance;
    public static final int TOTAL_DIAS_SEMANA = 7;
    public static final int MAX_PUNTOS_POSIBLES = 28;
    public static final String A1_TRANSPORTE = "Usé transporte público, bicicleta o caminé.";
    public static final String A2_IMPRESIONES = "No realicé impresiones.";
    public static final String A3_ENVASES = "No utilicé envases descartables (usé mi termo/taza).";
    public static final String A4_RECICLAJE = "Separé y reciclé materiales (vidrio, plástico, papel).";

    public static HuellaVerdeControladora getInstance() {
        if (instance == null) instance = new HuellaVerdeControladora();
        return instance;
    }

    private HuellaVerdeControladora() {}

    private static final EcoTip[] LISTA_TIPS = {
            new EcoTip(R.drawable.noun_sprout_5694994, "Un solo árbol puede absorber hasta 150kg de CO2 al año."),
            new EcoTip(R.drawable.noun_book_5694993, "Caminar o usar bici reduce tu huella de carbono un 67%."),
            new EcoTip(R.drawable.noun_beach_4906920, "Evitar plásticos salva la vida de miles de especies marinas."),
            new EcoTip(R.drawable.noun_tree_5377571, "Ahorrar papel protege los bosques y la biodiversidad."),
            new EcoTip(R.drawable.noun_renewable_energy_5377587, "Reciclar aluminio ahorra el 95% de energía de fabricación."),
            new EcoTip(R.drawable.noun_renewable_energy_5377593, "Apagar luces innecesarias ayuda a reducir el consumo global."),
            new EcoTip(R.drawable.noun_recycle_bag_5377597, "Una bolsa de tela reemplaza a 1000 de plástico en su vida útil."),
            new EcoTip(R.drawable.noun_save_water_5377595, "Cerrar el grifo al cepillarte ahorra hasta 12 litros por minuto."),
            new EcoTip(R.drawable.noun_wind_energy_5377585, "La energía del sol es limpia, infinita y gratuita."),
            new EcoTip(R.drawable.noun_tulip_5625817,"Compostar reduce tus residuos domésticos hasta en un 40%.")
    };

    // --- MÉTODOS EXISTENTES ---
    public EcoTip obtenerEcoTipAleatorio() {
        return LISTA_TIPS[(int) (Math.random() * LISTA_TIPS.length)];
    }

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
        HuellaVerdeDatos datos = HuellaVerdeDatos.getInstance(ctx);
        HashMap<String, RegistroSostenible> mapa = datos.getRegistrosMapCopy();

        // 1. Calcular Histórico (Recorrido directo de valores)
        ResumenHistorico historico = new ResumenHistorico();
        for (RegistroSostenible r : mapa.values()) {
            int cant = r.getAcciones().size();
            if (cant > 0) historico.totalDiasConAccion++;
            if (cant == 4) historico.totalDiasPerfectos++;
        }

        // 2. Calcular Semana (Búsqueda directa de 7 llaves)
        ResumenSemanal stats = new ResumenSemanal();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String fecha = sdf.format(cal.getTime());
            RegistroSostenible reg = mapa.get(fecha);
            if (reg != null) {
                List<String> acc = reg.getAcciones();
                if (!acc.isEmpty()) stats.diasConAlMenosUna++;
                if (acc.size() == 4) stats.diasCompletos++;
                for (String a : acc) {
                    String s = a.toLowerCase();
                    if (s.contains("transporte") || s.contains("bic") || s.contains("camin")) stats.vTransporte++;
                    else if (s.contains("impresion")) stats.vImpresiones++;
                    else if (s.contains("envase") || s.contains("termo") || s.contains("taza")) stats.vEnvases++;
                    else if (s.contains("recicl")) stats.vReciclaje++;
                }
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        // 3. Generar Bioindicadores (UI)
        List<AccionResumen> listaAcciones = generarBioindicadoresDesdeStats(ctx, stats);

        int puntosTotales = stats.vTransporte + stats.vImpresiones + stats.vEnvases + stats.vReciclaje;

        int dia = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        EcoTip tipInicial = LISTA_TIPS[dia % LISTA_TIPS.length];

        return new ResumenGeneral(listaAcciones, puntosTotales,
                determinarMensajeMotivador(puntosTotales),
                historico, tipInicial);

    }

    private List<AccionResumen> generarBioindicadoresDesdeStats(Context ctx, ResumenSemanal stats) {
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

    public void guardarAccionesActuales(Context ctx, String fecha, ArrayList<String> acciones) {
        // Guardar solo los datos
        HuellaVerdeDatos.getInstance(ctx).guardarAcciones(fecha, acciones, ctx);
        ResumenGeneral resumen = obtenerResumenCompleto(ctx);

        // Aseguras que el caché esté actualizado
        MenuDatos.guardarPuntosCache(ctx, resumen.getPuntosTotales());

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

    // --- CLASES DE SOPORTE ---

    public static class ResumenGeneral {
        public final List<AccionResumen> acciones;

        public final int puntosTotales;
        public final String mensajeGlobal;
        public final ResumenHistorico historico;
        public final EcoTip tipDelDia;
        public final int maxPuntos = MAX_PUNTOS_POSIBLES;

        public ResumenGeneral(List<AccionResumen> acciones, int puntosTotales,
                              String mensajeGlobal, ResumenHistorico historico, EcoTip tip) {
            this.acciones = acciones;
            this.puntosTotales = puntosTotales;
            this.mensajeGlobal = mensajeGlobal;
            this.historico = historico;
            this.tipDelDia = tip;
        }

        public int getPuntosTotales() {
            return puntosTotales;
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

    public static class EcoTip {
        public final int iconoResId;
        public final String mensaje;

        public EcoTip(int iconoResId, String mensaje) {
            this.iconoResId = iconoResId;
            this.mensaje = mensaje;
        }
    }
}


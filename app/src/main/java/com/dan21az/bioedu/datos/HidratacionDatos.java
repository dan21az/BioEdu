package com.dan21az.bioedu.datos;

import android.content.Context;

import com.dan21az.bioedu.modelo.hidratacion.Hidratacion;
import com.dan21az.bioedu.modelo.hidratacion.RegistroHidratacion;
import com.dan21az.bioedu.modelo.hidratacion.DiaHidratacion;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.List;

public class HidratacionDatos {

    private static HidratacionDatos instancia;
    private static final String ARCHIVO = "hidratacion.ser";

    private Hidratacion modelo;
    private Context context;

    private HidratacionDatos(Context context) {
        this.context = context.getApplicationContext();
        cargarModelo();
    }

    // Singleton con Context
    public static HidratacionDatos getInstance(Context context) {
        if (instancia == null) {
            instancia = new HidratacionDatos(context);
        }
        return instancia;
    }
    private void cargarModelo() {
        try {
            FileInputStream fis = context.openFileInput(ARCHIVO);
            ObjectInputStream ois = new ObjectInputStream(fis);
            modelo = (Hidratacion) ois.readObject();
            ois.close();
        } catch (Exception e) {
            // Primera ejecución: crear modelo y datos iniciales
            modelo = new Hidratacion();
            modelo.cargarDatosIniciales();
            guardarModelo();
        }
    }

    public void guardarModelo() {
        try {
            FileOutputStream fos = context.openFileOutput(ARCHIVO, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(modelo);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =======================
       MÉTODOS QUE USA ANDROID
       ======================= */

    public void registrarToma(int cantidad, String hora, Calendar fecha) {
        modelo.registrarIngesta(cantidad, hora, fecha);
        guardarModelo();
    }

    public void establecerMeta(int meta) {
        modelo.establecerMeta(meta);
        guardarModelo();
    }

    public int getMetaDiaria() {
        return modelo.getMetaDiaria();
    }

    public int getTotalPorDia(Calendar fecha) {
        return modelo.getTotalPorDia(fecha);
    }

    public List<RegistroHidratacion> getRegistrosPorDia(Calendar fecha) {
        return modelo.getRegistrosPorDia(fecha);
    }

    public List<DiaHidratacion> getDias() {
        return modelo.getDias();
    }

    public int getTotalHoy() {
        Calendar hoy = Calendar.getInstance();
        return modelo.getTotalPorDia(hoy);
    }

    public List<RegistroHidratacion> getRegistrosDeHoy() {
        Calendar hoy = Calendar.getInstance();
        return modelo.getRegistrosPorDia(hoy);
    }

    public void setMetaDiaria(int nuevaMeta) {
        modelo.establecerMeta(nuevaMeta);
        guardarModelo();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xjplay.model.progress;

import java.awt.Color;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import jlog.model.Log;

/**
 *
 * @author prez
 */
public class WorkerStringProgress extends SwingWorker<Void, String> {

    private final JLabel lblDuracion;
    private final String maxDuracionCancion;
    private final int totalSegundos;

    private int iniMin;
    private int iniSeg;
    private boolean pausado;

    public WorkerStringProgress(JLabel lblDuracion, String max) {
        this.lblDuracion = lblDuracion;
        maxDuracionCancion = max;
        this.totalSegundos = getTotalSegundos();

        iniMin = 0;
        iniSeg = 0;
        
        pausado = false;
    }
    
    public void pausar(){
        pausado = true;
    }
    
    public void resume(){
        pausado = false;
    }

    /**
     * este método se llama cuando libero el click sobre el progress bar
     * de la duracion de la canción
     * @param porcentaje 
     */
    public void cambiar(int porcentaje) {
        Log.add(porcentaje + "%");

        if (porcentaje < 0) { // si el porcentaje es negativo, dejo los minutos y segundos en 0
            iniMin = 0;
            iniSeg = 0;
        } else if (porcentaje > 100) { // si es mayor a 100, lo dejo en el total
            iniMin = totalSegundos / 60;
            iniSeg = totalSegundos - (iniMin * 60);
        } else { // si no, se calcula el porcentaje del total.
            iniSeg = (porcentaje * totalSegundos) / 100;
            iniMin = iniSeg / 60;
            iniSeg = iniSeg - (iniMin * 60);

        }
        Log.add(iniMin + ":" + iniSeg);
    }

    @Override
    protected Void doInBackground() throws Exception {
        for (iniMin = 0; iniMin < 60; iniMin++) {
            for (iniSeg = 0; iniSeg < 60; iniSeg++) {
                while(pausado){
                    Thread.sleep(500);
                }
                
                publish(iniMin + ":" + (iniSeg < 10 ? "0" + iniSeg : iniSeg));
                Thread.sleep(1000);
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        lblDuracion.setText(chunks.get(0) + " - "+maxDuracionCancion);
    }

    private int getTotalSegundos() {
        String[] split = maxDuracionCancion.split(":");

        int minutos, segundos;

        minutos = Integer.parseInt(split[0]);
        minutos = minutos * 60;
        segundos = Integer.parseInt(split[1]);

        Log.add("SEGUNDOS: " + (minutos + segundos));

        return (minutos + segundos);
    }

}

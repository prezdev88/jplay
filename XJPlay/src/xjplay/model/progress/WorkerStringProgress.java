/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xjplay.model.progress;

import java.awt.Color;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import xjplay.recursos.Ruta;

/**
 *
 * @author prez
 */
public class WorkerStringProgress extends SwingWorker<Void, String> {

    private JProgressBar progressBar;
    private String maxString;
    private int totalSegundos;

    private int iniMin;
    private int iniSeg;
    private boolean pausado;

    public WorkerStringProgress(JProgressBar progressBar, String max) {
        this.progressBar = progressBar;
        maxString = max;
        this.totalSegundos = getTotalSegundos();

        iniMin = 0;
        iniSeg = 0;
        
        progressBar.setForeground(new Color(76,175,80));
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
        System.out.println(porcentaje + "%");

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
        System.out.println(iniMin + ":" + iniSeg);

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
        progressBar.setString(chunks.get(0));
    }

    private int getTotalSegundos() {
        String[] split = maxString.split(":");

        int minutos, segundos;

        minutos = Integer.parseInt(split[0]);
        minutos = minutos * 60;
        segundos = Integer.parseInt(split[1]);

        System.out.println("SEGUNDOS: " + (minutos + segundos));

        return (minutos + segundos);
    }

}

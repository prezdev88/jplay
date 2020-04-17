/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.prezdev.xjplay.test.progress;

import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author prez
 */
public class WorkerStringProgress extends SwingWorker<Void, String>{

    private JProgressBar progreso;

    public WorkerStringProgress(JProgressBar progreso) {
        this.progreso = progreso;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        for (int hor = 0; hor < 24; hor++) {
            for (int min = 0; min < 60; min++) {
                for (int seg = 0; seg < 60; seg++) {
                    publish(min + ":" + (seg < 10 ? "0" + seg : seg));
                    Thread.sleep(1000);
                }
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        progreso.setString(chunks.get(0));
    }
    
    

    
}

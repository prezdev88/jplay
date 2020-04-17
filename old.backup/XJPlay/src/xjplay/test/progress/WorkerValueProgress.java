/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xjplay.test.progress;

import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author prez
 */
public class WorkerValueProgress extends SwingWorker<Void, Integer>{

    private JProgressBar progreso;
    private int max;
    private WorkerStringProgress w;

    public WorkerValueProgress(JProgressBar progreso, int max, WorkerStringProgress w) {
        this.progreso = progreso;
        this.max = max;
        this.w = w;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        for (int i = 0; i <= max; i++) {
            try {
                publish(i);

                Thread.sleep(1);
            } catch (InterruptedException ex) {
                w.cancel(true);
            }
        }
        w.cancel(true);
        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        progreso.setValue(chunks.get(0));
    }

    
    
    

    
}

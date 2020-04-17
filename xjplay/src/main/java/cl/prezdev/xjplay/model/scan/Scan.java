package cl.prezdev.xjplay.model.scan;

import cl.prezdev.jlog.Log;
import cl.prezdev.jplay.Biblioteca;
import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.rules.Rule;
import cl.prezdev.xjplay.utils.Validar;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Scan extends Thread {

    private Biblioteca biblioteca;
    private UpdateBibliotecaUI update;
    private boolean huboCambios;

    public Scan(Biblioteca biblioteca, UpdateBibliotecaUI update) {
        this.biblioteca = biblioteca;
        this.update = update;
        this.huboCambios = false;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Log.add("PAUSE SCAN...");
                Thread.sleep(Rule.PAUSE_SCAN);

                scanner();
            }
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Método que escanea las rutas de la biblioteca. Lo deje en un método
     * para llamarlo desde la app principal pero sin un hilo.
     * 
     * @throws IOException 
     */
    public void scanner() throws IOException {
        try {
            Log.add("REMOVIENDO NO EXISTENTES...");
            int cant = biblioteca.removerNoExistentes();
            Log.add("OK ("+cant+" removidos)");

            for (File f : biblioteca.getRutas()) {
                Log.add("SCAN [" + f.getPath() + "]...");
                scan(f);
                Log.add("OK");
            }

            if(!huboCambios){
                huboCambios = cant != 0; // si se removió alguna cancion, hubo cambios
            }

            update.updateBibliotecaUI(huboCambios);
            huboCambios = false;
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("concurrent exception: "+e.getMessage());
        }
    }

    private void scan(File raiz) throws IOException {
        if(raiz.exists()){
            if (raiz.listFiles() != null) {
                for (File a : raiz.listFiles()) {
                    if (a.isDirectory()) {
                        scan(a);
                    } else if (Validar.isCancion(a)) {
                        Song c = new Song(a.getPath());
                        biblioteca.add(c);
                        huboCambios = true;
                    }
                }
            } else {
                Song c = new Song(raiz.getPath());
                biblioteca.add(c);
                huboCambios = true;
            }
        }else{
            if(biblioteca.removerRuta(raiz)){
                Log.add("Ruta removida! ["+raiz+"]");
                huboCambios = true;
            }
        }
    }

    
}

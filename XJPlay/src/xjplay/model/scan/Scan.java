package xjplay.model.scan;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jplay.model.Biblioteca;
import jplay.model.Cancion;
import xjplay.model.rules.Rules;
import xjplay.utils.Validar;

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
                System.out.println("PAUSE SCAN...");
                Thread.sleep(Rules.PAUSE_SCAN);

                System.out.print("REMOVIENDO NO EXISTENTES...");
                int cant = biblioteca.removerNoExistentes();
                System.out.println("OK ("+cant+" removidos)");
                scan();
                
                huboCambios = cant != 0; // si se removió alguna cancion, hubo cambios
                
                update.updateBibliotecaUI(huboCambios);
                huboCambios = false;
            }
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void scan() throws IOException {
        for (File f : biblioteca.getRutas()) {
            System.out.print("SCAN [" + f + "]...");
            scan(f);
            System.out.println("OK");
        }
    }

    private void scan(File raiz) throws IOException {
        if(raiz.exists()){
            if (raiz.listFiles() != null) {
                for (File a : raiz.listFiles()) {
                    if (a.isDirectory()) {
                        scan(a);
                    } else if (Validar.isCancion(a)) {
                        Cancion c = new Cancion(a.getPath());
                        biblioteca.add(c);
                        huboCambios = true;
                    }
                }
            } else {
                Cancion c = new Cancion(raiz.getPath());
                biblioteca.add(c);
                huboCambios = true;
            }
        }else{
            if(biblioteca.removerRuta(raiz)){
                System.out.println("Ruta removida! ["+raiz+"]");
                huboCambios = true;
            }
        }
    }
}

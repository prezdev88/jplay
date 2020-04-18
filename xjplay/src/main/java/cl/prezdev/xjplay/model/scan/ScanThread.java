package cl.prezdev.xjplay.model.scan;

import cl.prezdev.jlog.Log;
import cl.prezdev.jplay.MusicLibrary;
import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.rules.Rule;
import cl.prezdev.xjplay.utils.Validate;

import java.io.File;
import java.io.IOException;

import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScanThread extends Thread {

    private MusicLibrary musicLibrary;
    private MusicLibraryUiUpdate musicLibraryUiUpdate;
    private boolean hasChanged;

    public ScanThread(MusicLibrary musicLibrary, MusicLibraryUiUpdate libraryUiUpdate) {
        this.musicLibrary = musicLibrary;
        this.musicLibraryUiUpdate = libraryUiUpdate;
        this.hasChanged = false;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Log.add("PAUSE SCAN...");
                Thread.sleep(Rule.PAUSE_SCAN);

                scanMusicLibraryPaths();
            }
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(ScanThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Método que escanea las rutas de la biblioteca. Lo deje en un método
     * para llamarlo desde la app principal pero sin un hilo.
     * 
     * @throws IOException 
     */
    public void scanMusicLibraryPaths() throws IOException {
        try {
            Log.add("REMOVIENDO NO EXISTENTES...");
            int cant = musicLibrary.cleanLibrary();
            Log.add("OK ("+cant+" removidos)");

            for (File file : musicLibrary.getPaths()) {
                Log.add("SCAN [" + file.getPath() + "]...");
                scan(file);
                Log.add("OK");
            }

            if(!hasChanged){
                hasChanged = (cant != 0); // si se removió alguna cancion, hubo cambios
            }

            musicLibraryUiUpdate.updateMusicLibraryUI(hasChanged);
            hasChanged = false;
        } catch (ConcurrentModificationException ex) {
            System.out.println("concurrent exception: "+ex.getMessage());
        }
    }

    // @TODO: Analizar y desacoplar este método:
    // Definir que hace realmente (hace muchas cosas)
    // Cambiar el nombre
    private void scan(File rootFile) throws IOException {
        if(rootFile.exists()){
            if (rootFile.listFiles() != null) {
                for (File file : rootFile.listFiles()) {
                    if (file.isDirectory()) {
                        scan(file);
                    } else if (Validate.isSong(file)) {
                        Song song = new Song(file.getPath());
                        musicLibrary.addSong(song);
                        hasChanged = true;
                    }
                }
            } else {
                Song song = new Song(rootFile.getPath());
                musicLibrary.addSong(song);
                hasChanged = true;
            }
        }else{
            if(musicLibrary.removePath(rootFile)){
                Log.add("Ruta removida! ["+rootFile+"]");
                hasChanged = true;
            }
        }
    }
}

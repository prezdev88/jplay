package jplay.model;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import org.tritonus.share.sampled.file.TAudioFileFormat;

public class Cancion extends File {

    private String nombre;
    private String autor;
    private String album;
    private int track;
    private long microseconds;
//    private File coverFile;
    
    private int cantidadReproducciones;

    public Cancion(String pathname) {
        super(pathname);
        nombre = getName();
        autor = "Sin autor";
        album = "Sin album";
        track = -1;
        cargar();
//        coverFile = null;
        
        cantidadReproducciones = 0;
    }

    /**
     * Aumenta el contador de reproducciones en uno
     */
    public void aumentarContadorReproducciones() {
        cantidadReproducciones++;
    }

    public int getCantidadReproducciones() {
        return cantidadReproducciones;
    }

//    public File getCoverFile() {
//        return coverFile;
//    }
//
    

    public long getMicroseconds() {
        return microseconds;
    }

    public void setMicroseconds(long microseconds) {
        this.microseconds = microseconds;
    }
//
//    public void setCoverFile(File caratula) {
//        this.coverFile = caratula;
//        this.coverImage = null;
//    }
//
//    public ImageIcon getCoverImage() {
//        if (this.coverFile != null) { // si el archivo de la caratula tiene algo, 
//            //quiere decir que existe una caratula en la ruta de la canción
//            Image image = new ImageIcon(coverFile.getPath()).getImage();
//
//            return new ImageIcon(
//                    image.getScaledInstance(
//                            (int) Recurso.CARATULA.getWidth(),
//                            (int) Recurso.CARATULA.getHeight(),
//                            Image.SCALE_SMOOTH)
//            );
//        } else { // si no, quiere decir que debo cargar la caratula por defecto
//
//            return new ImageIcon(this.coverImage.getImage().getScaledInstance(
//                    (int) Recurso.CARATULA.getWidth(),
//                    (int) Recurso.CARATULA.getHeight(),
//                    Image.SCALE_SMOOTH));
//        }
//    }
//

    

    private void cargar() {
        try {
            nombre = get("title").toString().trim();

        } catch (NullPointerException e) {
        }

        try {
            autor = get("author").toString().trim();
        } catch (NullPointerException e) {

        }

        try {
            album = get("album").toString().trim();
        } catch (NullPointerException e) {
        }

        try {
            track = Integer.parseInt(get("mp3.id3tag.track").toString().trim());
        } catch (Exception ex) {
        }

        try {
            microseconds = (Long) get("duration");
        } catch (Exception ex) {

        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getAutor() {
        return autor;
    }

    public String getAlbum() {
        return album;
    }

    public int getTrack() {
        return track;
    }

    public String getDuracionAsString() {
        int mili = (int) (microseconds / 1000);
        int sec = (mili / 1000) % 60;
        int min = (mili / 1000) / 60;
        return min + ":" + (sec < 10 ? "0" + sec : sec);
    }

    public String getDuracionAsString(int actualEnMilis) {
        int sec = (actualEnMilis / 1000) % 60;
        int min = (actualEnMilis / 1000) / 60;
        return min + ":" + (sec < 10 ? "0" + sec : sec);
    }

    public long getDuracionEnMicrosegundos() {
        return microseconds;
    }

    public int getDuracionEnMilis() {
        return (int) (microseconds / 1000);
    }

    private Object get(String key) {
        try {
            //http://www.javazoom.net/mp3spi/documents.html
            //http://www.javazoom.net/jlgui/developerguide.html
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(this);
            if (fileFormat instanceof TAudioFileFormat) {
                Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();

                return properties.get(key);
            } else {
                return null;
            }
        } catch (UnsupportedAudioFileException | IOException ex) {
//            Logger.getLogger(Cancion.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String toString() {
        return this.getNombre();
    }

}

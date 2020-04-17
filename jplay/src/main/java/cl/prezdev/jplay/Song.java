package cl.prezdev.jplay;

import cl.prezdev.jlog.Log;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.Data;
import org.tritonus.share.sampled.file.TAudioFileFormat;

@Data
public class Song extends File {

    private String name;
    private String author;
    private String album;
    private String year;
    private int trackNumber;
    private long microseconds;
//    private File coverFile;
    
    private int playCount;

    public Song(String pathname) {
        super(pathname);
        
        name = getName();
        author = "Sin autor";
        album = "Sin album";
        trackNumber = -1;
        load();
//        coverFile = null;
        
        playCount = 0;
    }

    /**
     * Aumenta el contador de reproducciones en uno
     */
    public void increasePlayCount() {
        playCount++;
        Log.add("Se ha aumentado el contador: "+playCount);
    }

//    public File getCoverFile() {
//        return coverFile;
//    }
//
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

    private void load() {
        try {
            name = get("title").toString().trim();
        } catch (NullPointerException e) {}

        try {
            author = get("author").toString().trim();
        } catch (NullPointerException e) {}

        try {
            album = get("album").toString().trim();
        } catch (NullPointerException e) {}

        try {
            trackNumber = Integer.parseInt(get("mp3.id3tag.track").toString().trim());
        } catch (Exception ex) {}

        try {
            microseconds = (Long) get("duration");
        } catch (Exception ex) {}
        
        try {
            year = get("date").toString();
        } catch (Exception ex) {}
    }

    public String getFormattedYear() {
        if (year != null) {
            try {
                int an = Integer.parseInt(year.trim());
                
                return "[" + an + "] ";
            } catch (NumberFormatException e) {
                return "[           ] ";
            }
        }else{
            return "[           ] ";
        }
    }
    

    public String getDurationAsString() {
        int mili = (int) (microseconds / 1000);
        int sec = (mili / 1000) % 60;
        int min = (mili / 1000) / 60;
        return min + ":" + (sec < 10 ? "0" + sec : sec);
    }

    public String getDurationAsString(int actualEnMilis) {
        int sec = (actualEnMilis / 1000) % 60;
        int min = (actualEnMilis / 1000) / 60;
        return min + ":" + (sec < 10 ? "0" + sec : sec);
    }


    public int getMilisDuration() {
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
            return null;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}

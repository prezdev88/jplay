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
        loadMetaDataSong();
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

    private void loadMetaDataSong() {
        try {
            name = getMetaData("title").toString().trim();
        } catch (NullPointerException e) {}

        try {
            author = getMetaData("author").toString().trim();
        } catch (NullPointerException e) {}

        try {
            album = getMetaData("album").toString().trim();
        } catch (NullPointerException e) {}

        try {
            trackNumber = Integer.parseInt(getMetaData("mp3.id3tag.track").toString().trim());
        } catch (Exception ex) {}

        try {
            microseconds = (Long) getMetaData("duration");
        } catch (Exception ex) {}
        
        try {
            year = getMetaData("date").toString();
        } catch (Exception ex) {}
    }

    // @TODO: Desacoplar
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
    
    // @TODO: Desacoplar
    public String getDurationAsString() {
        int milliseconds = (int) (microseconds / 1000);
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / 1000) / 60;
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    // @TODO: Desacoplar
    public String getDurationAsString(int actualEnMilis) {
        int sec = (actualEnMilis / 1000) % 60;
        int min = (actualEnMilis / 1000) / 60;
        return min + ":" + (sec < 10 ? "0" + sec : sec);
    }

    public int getMilisDuration() {
        return (int) (microseconds / 1000);
    }

    // @TODO: Desacoplar
    private Object getMetaData(String key) {
        try {
            //http://www.javazoom.net/mp3spi/documents.html
            //http://www.javazoom.net/jlgui/developerguide.html
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(this);
            
            if (audioFileFormat instanceof TAudioFileFormat) {
                Map<?, ?> properties = ((TAudioFileFormat) audioFileFormat).properties();

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

    public boolean hasPlays() {
        return (this.getPlayCount() != 0);
    }
}

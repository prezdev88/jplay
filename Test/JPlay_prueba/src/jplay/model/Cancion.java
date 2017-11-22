package jplay.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.tritonus.share.sampled.file.TAudioFileFormat;

public class Cancion extends File{
    private String nombre;
    private String autor;
    private String album;
    private int track;
    private long microseconds;

    public Cancion(String pathname) {
        super(pathname);
        nombre = this.getName();
        autor = "Sin autor";
        album = "Sin album";
        track = -1;
        cargar();
    }

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
        
        try{
            track = Integer.parseInt(get("mp3.id3tag.track").toString().trim());
        }catch(Exception  ex){
        }
        
        microseconds = (Long) get("duration");
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public String getAutor(){
        return autor;
    }
    
    public String getAlbum(){
        return album;
    }
    
    public int getTrack(){
        return track;
    }
    
    public String getDuracionAsString(){
        int mili = (int) (microseconds / 1000);
        int sec = (mili / 1000) % 60;
        int min = (mili / 1000) / 60;
        return min + ":" + (sec < 10 ? "0"+sec : sec);
    }
    
    public String getDuracionAsString(int actualEnMilis){
        int sec = (actualEnMilis / 1000) % 60;
        int min = (actualEnMilis / 1000) / 60;
        return min + ":" + (sec < 10 ? "0"+sec : sec);
    }
    
    public long getDuracionEnMicrosegundos(){
        return microseconds; 
    }
    
    public int getDuracionEnMilis(){
        return (int) (microseconds / 1000);
    }
    
    private Object get(String key){
        try {
            //http://www.javazoom.net/mp3spi/documents.html
            //http://www.javazoom.net/jlgui/developerguide.html
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(this);
            if (fileFormat instanceof TAudioFileFormat) {
                Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
                
                return properties.get(key);
            }else{
                return null;
            }
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(Cancion.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @Override
    public String toString() {
        return this.getNombre();
    }

    

}
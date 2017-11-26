package jplay.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Album {
    private String artist;
    private String name;
    private List<Cancion> canciones;
    private ImageIcon cover;

    public Album(String artist, String name) {
        this.artist = artist;
        this.name = name;
        this.canciones = new ArrayList<>();
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageIcon getCover() {
        return cover;
    }

    public void setCover(ImageIcon cover) {
        this.cover = cover;
    }
    
    
    
    
    public void add(Cancion c){
        this.canciones.add(c);
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }
    
    
}

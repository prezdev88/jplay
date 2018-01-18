package jplay.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Album implements Serializable {

    private String artist;
    private String name;
    private final List<Cancion> canciones;
    private List<ImageIcon> covers;

    public Album(String artist, String name) {
        this.artist = artist;
        this.name = name;
        this.canciones = new ArrayList<>();
        covers = new ArrayList<>();
    }

    public boolean hasImagenes() {
        return !covers.isEmpty();
    }
    
    public void removeImage(ImageIcon cover){
        System.out.println("COVER REMOVIDO: "+covers.remove(cover));
    }

    public void setCovers(List<ImageIcon> covers) {
        this.covers = covers;
    }

    public List<ImageIcon> getCovers() {
        return covers;
    }
    
    public boolean existCancion(Cancion c) {
        return this.canciones.contains(c);
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

    public void addCancion(Cancion c) {
        this.canciones.add(c);
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    @Override
    public String toString() {
        return artist + " - " + name;
    }

}

package jplay.model;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Album implements Serializable {

    private String artist;
    private String name;
    private List<Cancion> canciones;
    private List<ImageIcon> imagenes;
    private ImageIcon lastFMImageCover;
    private ImageIcon defaultCover;

    public Album(String artist, String name) {
        this.artist = artist;
        this.name = name;
        this.canciones = new ArrayList<>();
        imagenes = new ArrayList<>();
        defaultCover = null;
    }

    public boolean hasImagenes() {
        return !imagenes.isEmpty();
    }

    public void setImagenes(List<ImageIcon> imagenes) {
        this.imagenes = imagenes;
    }

    public List<ImageIcon> getImagenes() {
        return imagenes;
    }

    public ImageIcon getLastFMImageCover() {
        return lastFMImageCover;
    }

    public void setLastFMImageCover(ImageIcon lastFMImageCover) {
        this.lastFMImageCover = lastFMImageCover;
    }

    public boolean hasLastFMImage() {
        return lastFMImageCover != null;
    }
    
    public ImageIcon getDefaultCover(){
        return this.defaultCover;
    }
    
    public void setDefaultCover(Image image) {
        this.defaultCover = new ImageIcon(image);
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

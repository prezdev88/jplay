package jplay.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import jlog.model.Log;

public class Album implements Serializable {

    private String artist;
    private String name;
    private final List<Cancion> canciones;
    private List<ImageIcon> covers;
    private String anio;

    public Album(String artist, String name, String anio) {
        this.artist = artist;
        this.name = name;
        this.anio = anio;
        this.canciones = new ArrayList<>();
        covers = new ArrayList<>();
    }

    public String getAnio() {

        if (anio != null) {
            try {
//                int an = Integer.parseInt(anio.trim());
//                
//                return "[" + an + "] ";
                return anio.trim();
            } catch (NumberFormatException e) {
                return "[           ] ";
            }
        } else {
            return "[           ] ";
        }

    }

    public boolean hasImagenes() {
        return !covers.isEmpty();
    }

    public void removeImage(ImageIcon cover) {
        Log.add("COVER REMOVIDO: " + covers.remove(cover));
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

    // https://www.rgagnon.com/javadetails/java-0601.html
    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public Color getColorPromedio() {

        if (this.hasImagenes()) {
            BufferedImage image = imageToBufferedImage(this.getCovers().get(0).getImage());

            int sumaR = 0, sumaG = 0, sumaB = 0;
            int alto = image.getHeight();
            int ancho = image.getWidth();
            int totalPixeles = alto * ancho;
            int clr;
            int red;
            int green;
            int blue;

            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    clr     = image.getRGB(i, j);
                    red     = (clr & 0x00ff0000) >> 16;
                    green   = (clr & 0x0000ff00) >> 8;
                    blue    = clr & 0x000000ff;

                    sumaR += red;
                    sumaG += green;
                    sumaB += blue;
//                    System.out.println("R="+red+"; G="+green+"; B="+blue+";");
                }
            }

            int promedioR = sumaR / totalPixeles;
            int promedioG = sumaG / totalPixeles;
            int promedioB = sumaB / totalPixeles;

            return new Color(promedioR, promedioG, promedioB);
        } else {
            return Color.white;
        }

    }

    @Override
    public String toString() {
        return artist + " - " + name;
    }

}

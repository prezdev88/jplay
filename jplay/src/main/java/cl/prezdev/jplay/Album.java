package cl.prezdev.jplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

import cl.prezdev.jlog.Log;

public class Album implements Serializable {

    private String artist;
    private String name;
    private final List<Song> songs;
    private List<ImageIcon> coversArt;
    private ImageIcon coverArt;
    private final String year;

    public Album(String artist, String name, String year) {
        this.artist = artist;
        this.name = name;
        this.year = year;
        this.songs = new ArrayList<>();
        this.coversArt = new ArrayList<>();
    }

    public Album(Song song) {
        this(song.getAuthor(), song.getAlbum(), song.getYear());
    }

    public String getYear() {
        if (year != null) {
            try {
                return year.trim();
            } catch (NumberFormatException ex) {
                return "[           ] ";
            }
        } else {
            return "[           ] ";
        }

    }

    public boolean hasCoversArt() {
        return !coversArt.isEmpty();
    }

    public void removeCoverArt(ImageIcon cover) {
        Log.add("COVER REMOVIDO: " + coversArt.remove(cover));
    }

    public void setCoversArt(List<ImageIcon> coversArt) {
        this.coversArt = coversArt;
        setMaxSizeCovertArt();
    }

    /**
     * Método que recorre los coverArts
     * y deja el que mida más
     */
    private void setMaxSizeCovertArt() {
        boolean first = true;

        for(ImageIcon coverArt : coversArt){
            if(first){
                first = false;
                this.setCoverArt(coverArt);
            }else if(coverArt.getIconWidth() > this.getCoverArt().getIconWidth()){
                this.setCoverArt(coverArt);
            }
        }
    }

    public List<ImageIcon> getCoversArt() {
        return coversArt;
    }

    public ImageIcon getCoverArt() {
        return coverArt;
    }

    public void setCoverArt(ImageIcon coverArt) {
        this.coverArt = coverArt;
    }

    public boolean songExist(Song song) {
        return this.songs.contains(song);
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

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public List<Song> getSongs() {
        return songs;
    }

    // @TODO: Desacoplar este método
    // https://www.rgagnon.com/javadetails/java-0601.html
    public static BufferedImage imageToBufferedImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage(
            image.getWidth(null), 
            image.getHeight(null), 
            BufferedImage.TYPE_INT_RGB
        );
        
        Graphics graphics = bufferedImage.getGraphics();
        
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        
        return bufferedImage;
    }

    // @TODO: Desacoplar este método
    public Color getAverageColor() {
        if (this.hasCoversArt()) {
            BufferedImage bufferedImage = imageToBufferedImage(
                this.getCoversArt().get(0).getImage()
            );

            int redSum = 0, greenSum = 0, blueSum = 0;
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();
            int totalPixels = height * width;
            int color;
            int red;
            int green;
            int blue;

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    color     = bufferedImage.getRGB(i, j);
                    red     = (color & 0x00ff0000) >> 16;
                    green   = (color & 0x0000ff00) >> 8;
                    blue    = color & 0x000000ff;

                    redSum += red;
                    greenSum += green;
                    blueSum += blue;
                }
            }

            int redAverage = redSum / totalPixels;
            int greenAverage = greenSum / totalPixels;
            int blueAverage = blueSum / totalPixels;

            return new Color(redAverage, greenAverage, blueAverage);
        } else {
            return Color.white;
        }
    }

    @Override
    public String toString() {
        return artist + " - " + name;
    }

}

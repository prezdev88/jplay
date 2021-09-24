package cl.prezdev.jplay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class Album implements Serializable {

    private static final Logger LOGGER = Logger.getLogger("cl.prezdev.jplay.Album");

    private String artistName;
    private String name;
    private final List<Song> songs;
    private List<ImageIcon> coversArt;
    private ImageIcon coverArt;
    private final String year;

    public Album(String artistName, String name, String year) {
        this.artistName = artistName;
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
            return year.trim();
        } else {
            return "";
        }

    }

    public boolean hasCoversArt() {
        return !coversArt.isEmpty();
    }

    public void removeCoverArt(ImageIcon cover) {
        LOGGER.log(Level.INFO, "COVER REMOVED: {0}", coversArt.remove(cover));
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

        for (ImageIcon coverArtImageIcon : coversArt) {
            if (first) {
                first = false;
                this.setCoverArt(coverArtImageIcon);
            } else if (coverArtImageIcon.getIconWidth() > this.getCoverArt().getIconWidth()) {
                this.setCoverArt(coverArtImageIcon);
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

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
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

    @Override
    public String toString() {
        return artistName + " - " + name;
    }
}

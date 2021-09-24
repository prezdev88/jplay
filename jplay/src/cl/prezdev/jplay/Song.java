package cl.prezdev.jplay;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Song extends File {

    private static final Logger LOGGER = Logger.getLogger("cl.prezdev.jplay.Song");

    private String name;
    private String author;
    private String album;
    private String year;
    private Integer trackNumber;
    private Long microseconds;
    private int playCount;

    public Song(String pathname) {
        super(pathname);

        name = super.getName();
        MetadataSong.loadMetadata(this);
        playCount = 0;
    }

    /**
     * Aumenta el contador de reproducciones en uno
     */
    public void increasePlayCount() {
        playCount++;
        LOGGER.log(Level.INFO, "increase play count: {0}", playCount);
    }

    public Integer getMilliSeconds() {
        if (microseconds == null) {
            return 0;
        }

        return (int) (microseconds / 1000);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public boolean hasPlays() {
        return (this.getPlayCount() != 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        if (!super.equals(o)) return false;
        Song song = (Song) o;
        return getPlayCount() == song.getPlayCount() && Objects.equals(getName(), song.getName()) && Objects.equals(getAuthor(), song.getAuthor()) && Objects.equals(getAlbum(), song.getAlbum()) && Objects.equals(getYear(), song.getYear()) && Objects.equals(getTrackNumber(), song.getTrackNumber()) && Objects.equals(getMicroseconds(), song.getMicroseconds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getAuthor(), getAlbum(), getYear(), getTrackNumber(), getMicroseconds(), getPlayCount());
    }
}

package cl.prezdev.jplay;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Data;

@Data
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
}

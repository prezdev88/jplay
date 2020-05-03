package cl.prezdev.jplay;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Map;

public class MetadataSong {

    private static Song song;

    public static void loadMetadata(Song song){
        MetadataSong.song = song;

        try {
            song.setName(getMetaData("title", String.class));
        } catch (PropertyNotFoundException e) {
        }

        try {
            song.setAuthor(getMetaData("author", String.class));
        } catch (PropertyNotFoundException e) {
            song.setAuthor("");
        }

        try {
            song.setAlbum(getMetaData("album", String.class));
        } catch (PropertyNotFoundException e) {
            song.setAlbum("");
        }

        try {
            song.setTrackNumber(getMetaData("mp3.id3tag.track", Integer.class));
        } catch (PropertyNotFoundException | NumberFormatException e) {
            song.setTrackNumber(-1);
        }

        try {
            song.setMicroseconds(getMetaData("duration", Long.class));
        } catch (PropertyNotFoundException | NumberFormatException e) {
            song.setMicroseconds(-1L);
        }

        try {
            song.setYear(getMetaData("date", String.class));
        } catch (PropertyNotFoundException e) {
            song.setYear("");
        }
    }

    private static <T> T getMetaData(String key, Class<T> _class) throws PropertyNotFoundException{
        try {
            //http://www.javazoom.net/mp3spi/documents.html
            //http://www.javazoom.net/jlgui/developerguide.html
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(MetadataSong.song);

            if (audioFileFormat instanceof TAudioFileFormat) {
                Map<?, ?> properties = audioFileFormat.properties();

                Object property = properties.get(key);

                if(property != null){
                    String value = property.toString().trim();
                    switch(_class.getSimpleName()){
                        case "Integer":
                            return (T) Integer.valueOf(value);
                        case "Long":
                            return (T) Long.valueOf(value);
                        case "String":
                            return (T) value;
                    }
                }
            }

            throw new PropertyNotFoundException();
        } catch (UnsupportedAudioFileException | IOException ex) {
            throw new PropertyNotFoundException();
        }
    }
}

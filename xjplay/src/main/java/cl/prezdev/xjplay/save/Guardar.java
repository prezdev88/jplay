package cl.prezdev.xjplay.save;

import cl.prezdev.jlog.LogEntry;
import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.artist.list.ArtistCoverArt;
import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;

public class Guardar implements Serializable{
    public List<Song>        canciones;
    public List<Album>          albums;
    public List<ArtistCoverArt>   coverArtistas;
    public int                  indiceActual;
    public int                  indexTab;
    public List<LogEntry>       logEntries;
    public Icon                 cover;
    public int                  volume;    
}

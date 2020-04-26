package cl.prezdev.xjplay.save;

import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.artist.list.ArtistCoverArt;
import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;

//@TODO: Cambiar a xml o json
public class Save implements Serializable{
    public List<Song>        songs;
    public List<Album>          albums;
    public List<ArtistCoverArt>   artistCoversArt;
    public int                  currentIndex;
    public int                  indexTab;
    public Icon                 cover;
    public int                  volume;    
}

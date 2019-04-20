package xjplay.save;

import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;
import jlog.model.LogEntry;
import jplay.model.Album;
import jplay.model.Cancion;
import xjplay.listaArtistas.CoverArtista;

public class Guardar implements Serializable{
    public List<Cancion>        canciones;
    public List<Album>          albums;
    public List<CoverArtista>   coverArtistas;
    public int                  indiceActual;
    public int                  indexTab;
    public List<LogEntry>       logEntries;
    public Icon                 cover;
    public int                  volume;    
}

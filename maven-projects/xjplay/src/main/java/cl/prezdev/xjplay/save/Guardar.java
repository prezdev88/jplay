package cl.prezdev.xjplay.save;

import cl.prezdev.jlog.LogEntry;
import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.Cancion;
import cl.prezdev.xjplay.listaArtistas.CoverArtista;
import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;

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

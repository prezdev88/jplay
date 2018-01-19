package xjplay.save;

import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;
import jlog.model.LogEntry;
import jplay.model.Cancion;

public class Guardar implements Serializable{
    public List<Cancion>    canciones;
    public int              indiceActual;
    public boolean          isMasEscuchadas;
    public int              indexTab;
    public List<LogEntry>   logEntries;
    public Icon             cover;
    
}

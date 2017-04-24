package xjplay.save;

import java.io.Serializable;
import java.util.List;
import jplay.model.Cancion;

public class Guardar implements Serializable{
    public List<Cancion> canciones;
    public int indiceActual;
    
}

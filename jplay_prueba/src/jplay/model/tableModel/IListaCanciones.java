package jplay.model.tableModel;

import java.util.List;
import jplay.model.Cancion;

public interface IListaCanciones {
    void add(Cancion c);
    void remove(Cancion c);
    List<Cancion> getCanciones();
    void setCanciones(List<Cancion> canciones);
    int getTipo();
}

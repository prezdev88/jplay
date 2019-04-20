package jplay.model;

import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class LMCancion implements ListModel{
    private List<Cancion> canciones;
    public LMCancion(List<Cancion> canciones){
       this.canciones = canciones;
    }
    
    @Override
    public int getSize() {
        return canciones.size();
    }

    @Override
    public Object getElementAt(int index) {
        return canciones.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

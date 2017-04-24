package jplay.model.tableModel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import jplay.model.Cancion;

public class TMCancion implements TableModel, IListaCanciones {

    private List<Cancion> canciones;
    
    public TMCancion(){
        canciones = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return canciones.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Nombre";
            case 1:
                return "Duración";

        }

        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cancion c = canciones.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return c;
            case 1:
                return c.getDuracionAsString();

        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void add(Cancion c){
        canciones.add(c);
    }
    
    @Override
    public List<Cancion> getCanciones(){
        return this.canciones;
    }
    
    @Override
    public void setCanciones(List<Cancion> canciones){
        this.canciones = canciones;
    }
    
    public void nuevaLista(){
        canciones = new ArrayList<>();
    }

    @Override
    public int getTipo() {
        return Tipo.LISTA;
    }

    @Override
    public void remove(Cancion c) {
        canciones.remove(c);
    }

}

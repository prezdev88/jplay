package cl.prezdev.xjplay.model.tableModel;

import cl.prezdev.jplay.Song;
import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TMCancionBiblioteca implements TableModel {
    public static final int OBJETO_COMPLETO = 3;

    public final List<Song> canciones;

    public TMCancionBiblioteca(List<Song> canciones) {
        this.canciones = canciones;
    }

    @Override
    public int getRowCount() {
        return canciones.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "#";
            case 1:
                return "Artista";
            case 2:
                return "Album";
            case 3:
                return "Nombre";
            case 4:
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
        Song c = canciones.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                if(c.getTrackNumber() != -1){
                    return c.getTrackNumber();
                }else{
                    return "";
                }
            case 1:
                return c.getAuthor();
            case 2:;
                return c.getFormattedYear()+c.getAlbum();
            case 3:
                return c;
            case 4:
                return c.getDurationAsString();

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
//        l.tableChanged(new TableModelEvent(this));
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

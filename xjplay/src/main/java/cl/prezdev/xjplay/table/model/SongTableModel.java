package cl.prezdev.xjplay.table.model;

import cl.prezdev.jplay.Song;
import cl.prezdev.jplay.common.Util;

import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SongTableModel implements TableModel {

    private List<Song> songs;

    public SongTableModel(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public int getRowCount() {
        return songs.size();
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
        Song song = songs.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return song;
            case 1:
                return Util.getDurationAsString(song.getMicroseconds());

        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {}

    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {}

    @Override
    public void removeTableModelListener(TableModelListener tableModelListener) {}

}

package cl.prezdev.xjplay.table.model;

import cl.prezdev.jplay.Song;
import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class MusicLabrarySongTableModel implements TableModel {
    public static final int COMPLETE_OBJECT_INDEX = 3;

    public final List<Song> songs;

    public MusicLabrarySongTableModel(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public int getRowCount() {
        return songs.size();
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
        Song song = songs.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                if(song.getTrackNumber() != -1){
                    return song.getTrackNumber();
                }else{
                    return "";
                }
            case 1:
                return song.getAuthor();
            case 2:
                return song.getFormattedYear()+song.getAlbum();
            case COMPLETE_OBJECT_INDEX:
                return song;
            case 4:
                return song.getDurationAsString();

        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {}

    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {}

    @Override
    public void removeTableModelListener(TableModelListener tableModelListenerl) {}

}

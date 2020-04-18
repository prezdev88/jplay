package cl.prezdev.xjplay.list.model;

import cl.prezdev.jplay.Album;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class AlbumListModel implements ListModel<Album>{
    
    private final List<Album> albums;

    public AlbumListModel(List<Album> albums) {
        this.albums = albums;
    }
    
    @Override
    public int getSize() {
        return albums.size();
    }

    @Override
    public Album getElementAt(int i) {
        return albums.get(i);
    }

    @Override
    public void addListDataListener(ListDataListener ll) {
    }

    @Override
    public void removeListDataListener(ListDataListener ll) {
    }
    
}

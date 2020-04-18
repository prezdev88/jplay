package cl.prezdev.xjplay.list.model;

import cl.prezdev.xjplay.artist.list.ArtistCoverArt;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class ArtistListModel implements ListModel<ArtistCoverArt>{
    
    private final List<ArtistCoverArt> coverArtistas;

    public ArtistListModel(List<ArtistCoverArt> coverArtistas) {
        this.coverArtistas = coverArtistas;
    }
    
    @Override
    public int getSize() {
        return coverArtistas.size();
    }

    @Override
    public ArtistCoverArt getElementAt(int i) {
        return coverArtistas.get(i);
    }

    @Override
    public void addListDataListener(ListDataListener ll) {
    }

    @Override
    public void removeListDataListener(ListDataListener ll) {
    }
    
}

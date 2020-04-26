package cl.prezdev.xjplay.list.model;

import cl.prezdev.xjplay.artist.list.ArtistCoverArt;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class ArtistListModel implements ListModel<ArtistCoverArt>{
    
    private final List<ArtistCoverArt> artistCoverArts;

    public ArtistListModel(List<ArtistCoverArt> artistCoverArts) {
        this.artistCoverArts = artistCoverArts;
    }
    
    @Override
    public int getSize() {
        return artistCoverArts.size();
    }

    @Override
    public ArtistCoverArt getElementAt(int i) {
        return artistCoverArts.get(i);
    }

    @Override
    public void addListDataListener(ListDataListener ll) {
    }

    @Override
    public void removeListDataListener(ListDataListener ll) {
    }
    
}

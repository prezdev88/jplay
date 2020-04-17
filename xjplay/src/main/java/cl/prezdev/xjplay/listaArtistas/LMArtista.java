package cl.prezdev.xjplay.listaArtistas;

import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class LMArtista implements ListModel<CoverArtista>{
    
    private final List<CoverArtista> coverArtistas;

    public LMArtista(List<CoverArtista> coverArtistas) {
        this.coverArtistas = coverArtistas;
    }
    
    @Override
    public int getSize() {
        return coverArtistas.size();
    }

    @Override
    public CoverArtista getElementAt(int i) {
        return coverArtistas.get(i);
    }

    @Override
    public void addListDataListener(ListDataListener ll) {
    }

    @Override
    public void removeListDataListener(ListDataListener ll) {
    }
    
}

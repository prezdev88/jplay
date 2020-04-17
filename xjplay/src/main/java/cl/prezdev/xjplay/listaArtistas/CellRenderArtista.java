package cl.prezdev.xjplay.listaArtistas;

import java.awt.Component;
import java.awt.Image;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class CellRenderArtista extends DefaultListCellRenderer {

    private final List<CoverArtista> coversArtistas;

    public CellRenderArtista(List<CoverArtista> coversArtistas) {
        this.coversArtistas = coversArtistas;
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        CoverArtista coverArtista = coversArtistas.get(index);
        Image image = coverArtista.getCover().getImage();
        
        label.setIcon(new ImageIcon(image));
        label.setText(coverArtista.getArtista().toUpperCase());
        
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        label.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        return label;
    }
}

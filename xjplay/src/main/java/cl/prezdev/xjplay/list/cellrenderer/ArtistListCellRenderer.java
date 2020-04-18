package cl.prezdev.xjplay.list.cellrenderer;

import cl.prezdev.xjplay.artist.list.ArtistCoverArt;
import java.awt.Component;
import java.awt.Image;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class ArtistListCellRenderer extends DefaultListCellRenderer {

    private final List<ArtistCoverArt> artistCoversArt;

    public ArtistListCellRenderer(List<ArtistCoverArt> artistCoversArt) {
        this.artistCoversArt = artistCoversArt;
    }

    @Override
    public Component getListCellRendererComponent(
        JList list, 
        Object value, 
        int index,
        boolean isSelected, 
        boolean cellHasFocus
    ) {
        ArtistCoverArt artistCoverArt = artistCoversArt.get(index);
        Image image = artistCoverArt.getCoverArt().getImage();

        JLabel artistListLabel = (JLabel) super.getListCellRendererComponent(
            list, value, index, isSelected, cellHasFocus
        );

        artistListLabel.setIcon(new ImageIcon(image));
        artistListLabel.setText(artistCoverArt.getArtistName().toUpperCase());
        
        artistListLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        artistListLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        artistListLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        return artistListLabel;
    }
}

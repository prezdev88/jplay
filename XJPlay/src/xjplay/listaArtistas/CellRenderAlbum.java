package xjplay.listaArtistas;

import java.awt.Component;
import java.awt.Image;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import jplay.model.Album;
import xjplay.recursos.Recurso;
import xjplay.rules.Rule;

public class CellRenderAlbum extends DefaultListCellRenderer {

    private final List<Album> albums;

    public CellRenderAlbum(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        Album album = albums.get(index);

        if (album instanceof AtrasAlbum) {
            label.setText("ATRÁS");
        } else {
            Image image = album.getLastCover();

            if (image != null) {
                image = album.getLastCover().getScaledInstance(
                        Rule.ARTISTA_COVER.width,
                        Rule.ARTISTA_COVER.height,
                        Image.SCALE_SMOOTH
                );
            } else {
                // el disco no tiene imagen
                image = Recurso.ICONO_JPLAY.getScaledInstance(
                        Rule.ARTISTA_COVER.width,
                        Rule.ARTISTA_COVER.height,
                        Image.SCALE_SMOOTH
                );
            }
            
            label.setIcon(new ImageIcon(image));
            label.setText(album.getName().toUpperCase() +"("+album.getAnio()+")");
        }

//        label.setIcon(new ImageIcon(image));
        

        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        label.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        return label;
    }
}

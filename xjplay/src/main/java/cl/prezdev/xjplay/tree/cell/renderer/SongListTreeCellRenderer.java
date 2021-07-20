package cl.prezdev.xjplay.tree.cell.renderer;

import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.MusicPlayer;
import cl.prezdev.jplay.Song;
import cl.prezdev.jplay.common.ImageProcessor;
import cl.prezdev.jplay.common.Util;
import cl.prezdev.xjplay.resources.Path;
import cl.prezdev.xjplay.rules.Rule;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class SongListTreeCellRenderer extends JLabel implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree, Object value, boolean selected, 
        boolean expanded, boolean leaf, int row, boolean hasFocus
    ) {
        tree.setBackground(Rule.BACKGROUND_COLOR.brighter());
        this.setBackground(Rule.BACKGROUND_COLOR.brighter());

        this.setOpaque(true);

        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) value;
        Object userObject = defaultMutableTreeNode.getUserObject();

        boolean isCurrentAlbum = false;

        if (userObject instanceof Song) {
            Song song = (Song) userObject;
            this.setText(
                song.getTrackNumber()+".- "+
                song.toString() +
                " ("+ Util.getDurationAsString(song.getMicroseconds()) +")"
            );

            setIcon(null);// @TODO: Probar sacando esta linea
        } else if (userObject instanceof Album) {
            Album album = (Album) userObject;
            this.setText(album.getYear() + " " +userObject.toString());

            try {// intento colocar el cover que tenga el album
                // @TODO: encapsular esto en un método de album
                Image coverImage = album.getCoversArt().get(0).getImage().getScaledInstance(
                    (int) Rule.COVERT_ART_MINI.getWidth(),
                    (int) Rule.COVERT_ART_MINI.getHeight(),
                    Image.SCALE_SMOOTH
                );
                setIcon(new ImageIcon(coverImage));
            } catch (IndexOutOfBoundsException ex) {

                // si no hay cover, cargo el icono de la app
                setIcon(new ImageIcon(
                        SongListTreeCellRenderer.getImageIcon(Path.JPLAY_ICON).getImage().getScaledInstance(
                            (int) Rule.COVERT_ART_MINI.getWidth(),
                            (int) Rule.COVERT_ART_MINI.getHeight(),
                            Image.SCALE_SMOOTH
                        )
                    )
                );
            }

            if (MusicPlayer.getInstance().hasCurrentSong()) {
                Song currentSong = MusicPlayer.getInstance().getCurrentSong();
                String albumAndArtist = userObject.toString();

                if (albumAndArtist.equalsIgnoreCase(
                    currentSong.getAuthor() + " - " +
                    currentSong.getAlbum()
                )) {
                    isCurrentAlbum = true;
                }
            }
        }

        boolean isCurrentSong = false;

        if (MusicPlayer.getInstance().hasCurrentSong()) {
            if (userObject instanceof Song) {
                Song song = (Song) userObject;
                if (MusicPlayer.getInstance().getCurrentSong().equals(song)) {
                    isCurrentSong = true;
                }
            }
        }

        // @TODO: DEsacoplar esta parte del código
        Color backgroundColor = Rule.BACKGROUND_COLOR.darker().darker();
        Color foregroungColor = ImageProcessor.getForeGroundColorBasedOnBGBrightness(backgroundColor);

        if (selected) {
            this.setForeground(foregroungColor);
            this.setBackground(backgroundColor);
        } else {
            this.setForeground(ImageProcessor.getForeGroundColorBasedOnBGBrightness(Rule.BACKGROUND_COLOR.brighter()));
            this.setBackground(Rule.BACKGROUND_COLOR.brighter());
        }

        if (isCurrentSong) {
            this.setForeground(foregroungColor);
            this.setBackground(backgroundColor);
        }

        if (isCurrentAlbum) {
            this.setForeground(foregroungColor);
            this.setBackground(backgroundColor);
        }

        return this;
    }

    /**
     * Método para construir un icono a través de la ruta de un paquete
     *
     * @param packagePath ruta del paquete. Ejemplo:
     * "/xml/images/16atributo.png"
     * @return Un objeto del tipo ImageIcon
     */
    // @TODO: Desacoplar este método
    public static ImageIcon getImageIcon(String packagePath) {
        return new ImageIcon(SongListTreeCellRenderer.class.getResource(packagePath));
    }
}

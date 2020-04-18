package cl.prezdev.xjplay.tree.cell.renderer;

import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.main.JPlay;
import cl.prezdev.xjplay.recursos.Path;
import cl.prezdev.xjplay.rules.Rule;
import cl.prezdev.xjplay.utils.Util;
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
//        try {
            tree.setBackground(Util.BACKGROUND_COLOR.brighter());
            this.setBackground(Util.BACKGROUND_COLOR.brighter());
            
            this.setOpaque(true);

//            Font fuente;
//            fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
//            fuente = fuente.deriveFont(Font.PLAIN, Rule.FONT_SIZE_CANCIONES);
//
//            this.setFont(fuente);
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) value;
            Object userObject = defaultMutableTreeNode.getUserObject();

            boolean isCurrentAlbum = false;

            if (userObject instanceof Song) {
                Song song = (Song) userObject;
                this.setText(
                    song.getTrackNumber()+".- "+
                    song.toString() + 
                    " ("+song.getDurationAsString()+")"
                );
                
                setIcon(null);// @TODO: Probar sacando esta linea
            } else if (userObject instanceof Album) {
                Album album = (Album) userObject;
                this.setText(album.getYear() + " " +userObject.toString());

                try {// intento colocar el cover que tenga el album
                    // @TODO: encapsular esto en un método de album
                    Image coverImage = album.getCovers().get(0).getImage().getScaledInstance(
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

                if (JPlay.musicPlayer != null) {
                    Song currentSong = JPlay.musicPlayer.getCurrentSong();
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

            if (JPlay.musicPlayer != null) {
                if (userObject instanceof Song) {
                    Song song = (Song) userObject;
                    if (JPlay.musicPlayer.getCurrentSong().equals(song)) {
                        isCurrentSong = true;
                    }
                }
            }

            // @TODO: DEsacoplar esta parte del código
            Color backgroundColor = Util.BACKGROUND_COLOR.darker().darker();
            Color foregroungColor = Util.getForeGroundColorBasedOnBGBrightness(backgroundColor);
            
            if (selected) {
                this.setForeground(foregroungColor);
                this.setBackground(backgroundColor);
            } else {
                this.setForeground(Util.getForeGroundColorBasedOnBGBrightness(Util.BACKGROUND_COLOR.brighter()));
                this.setBackground(Util.BACKGROUND_COLOR.brighter());
            }

            if (isCurrentSong) {
                this.setForeground(foregroungColor);
                this.setBackground(backgroundColor);
            }

            if (isCurrentAlbum) {
                this.setForeground(foregroungColor);
                this.setBackground(backgroundColor);
            }
//        } catch (FontFormatException | IOException ex) {
//            Logger.getLogger(CellRenderCancionLista.class.getName()).log(Level.SEVERE, null, ex);
//        }

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

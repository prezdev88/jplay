package cl.prezdev.xjplay.tree.cell.renderer;

import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.Song;
import cl.prezdev.jplay.common.Util;
import cl.prezdev.xjplay.resources.Path;
import cl.prezdev.xjplay.rules.Rule;

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
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) value;
        Object userObject = defaultMutableTreeNode.getUserObject();


        switch (userObject) {
            case Song song -> {
                this.setText(
                        song.getTrackNumber() + ".- " +
                                song.toString() +
                                " (" + Util.getDurationAsString(song.getMicroseconds()) + ")"
                );
                
                setIcon(null);
            }
            case Album album -> {
                this.setText(album.getYear() + " " + userObject.toString());
                
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
            }
            default -> {
            }
        }

        return this;
    }

    /**
     * Método para construir un icono a través de la ruta de un paquete
     *
     * @param packagePath ruta del paquete. Ejemplo:
     *                    "/xml/images/16atributo.png"
     * @return Un objeto del tipo ImageIcon
     */
    // @TODO: Desacoplar este método
    public static ImageIcon getImageIcon(String packagePath) {
        return new ImageIcon(SongListTreeCellRenderer.class.getResource(packagePath));
    }
}

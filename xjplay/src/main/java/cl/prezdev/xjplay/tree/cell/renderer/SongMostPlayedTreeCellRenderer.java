package cl.prezdev.xjplay.tree.cell.renderer;

import cl.prezdev.jplay.Song;
import cl.prezdev.jplay.common.Util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class SongMostPlayedTreeCellRenderer extends JLabel implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.setOpaque(true);

        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) value;
        Object userObject = defaultMutableTreeNode.getUserObject();

        if (userObject instanceof Song) {
            Song song = (Song) userObject;

            int playCount = song.getPlayCount();

            this.setText(" [ "+playCount+" ] "+song.getAuthor()+" - "+song.toString() + " ("+ Util.getDurationAsString(song.getMicroseconds()) +") ");
            this.setToolTipText(playCount+(playCount == 1?" vez reproducida":" veces reproducidas"));
            this.setIcon(null); // @TODO: probar sacando esta linea
        }

        if (selected) {
            // @TODO: Almacenar color en alguna clasem hasta ahora se
            // almacena en Util
            this.setBackground(new Color(217, 238, 208));
        } else {
            this.setForeground(Color.black);
            this.setBackground(Color.white);
        }

        return this;
    }

    /**
     * Método para construir un icono a través de la ruta de un paquete
     *
     * @param rutaPaquete ruta del paquete. Ejemplo:
     * "/xml/images/16atributo.png"
     * @return Un objeto del tipo ImageIcon
     */
    // @TODO: Desacoplar este método
    // Este método ya esta en SongListTreeCellRenderer y ExplorerTreeCellRenderer
    public static ImageIcon crearIcono(String rutaPaquete) {
        return new ImageIcon(SongMostPlayedTreeCellRenderer.class.getResource(rutaPaquete));
    }
}

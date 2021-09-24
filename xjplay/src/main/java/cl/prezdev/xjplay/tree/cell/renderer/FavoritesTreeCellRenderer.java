package cl.prezdev.xjplay.tree.cell.renderer;


import cl.prezdev.jplay.Song;
import cl.prezdev.jplay.common.Util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class FavoritesTreeCellRenderer extends JLabel implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus
    ) {
//        try {
        this.setOpaque(true);

//            Font fuente;
//            fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
//            fuente = fuente.deriveFont(Font.PLAIN, Rule.FONT_SIZE_CANCIONES);
//
//            this.setFont(fuente);
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) value;
        Object userObject = defaultMutableTreeNode.getUserObject();

        if (userObject instanceof Song) {
            Song song = (Song) userObject;
            int count = song.getPlayCount();


            String durationAsString = Util.getDurationAsString(song.getMicroseconds());
            this.setText(" [ " + count + " ] " + song.getAuthor() + " - " + song.toString() + " (" + durationAsString + ") ");
            this.setToolTipText(count + (count == 1 ? " vez reproducida" : " veces reproducidas"));

            this.setIcon(null);// @TODO: Probar sacando esta linea
        }

        if (selected) {
            this.setBackground(new Color(217, 238, 208));
        } else {
            this.setForeground(Color.black);
            this.setBackground(Color.white);
        }

//        } catch (FontFormatException | IOException ex) {
//            Logger.getLogger(CellRenderFavoritos.class.getName()).log(Level.SEVERE, null, ex);
//        }

        return this;
    }
}

package cl.prezdev.xjplay.model.tree;

import cl.prezdev.xjplay.recursos.Recurso;

import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.rules.Rule;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class CellRenderFavoritos extends JLabel implements TreeCellRenderer {


    public CellRenderFavoritos() {
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//        try {
            this.setOpaque(true);

//            Font fuente;
//            fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
//            fuente = fuente.deriveFont(Font.PLAIN, Rule.FONT_SIZE_CANCIONES);
//
//            this.setFont(fuente);
            DefaultMutableTreeNode v = (DefaultMutableTreeNode) value;
            Object ob = v.getUserObject();

            if (ob instanceof Song) {
                Song c = (Song) ob;
                int cant = c.getPlayCount();
                this.setText(" [ "+cant+" ] "+c.getAuthor()+" - "+c.toString() + " ("+c.getDurationAsString()+") ");
                this.setToolTipText(cant+(cant == 1?" vez reproducida":" veces reproducidas"));
                this.setIcon(null);
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

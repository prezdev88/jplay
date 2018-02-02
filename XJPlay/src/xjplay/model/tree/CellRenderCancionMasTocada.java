package xjplay.model.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import jplay.model.Cancion;
import xjplay.model.rules.Rules;
import xjplay.recursos.Recurso;

/**
 *
 * @author Patricio Pérez Pinto
 */
public class CellRenderCancionMasTocada extends JLabel implements TreeCellRenderer {

    private ImageIcon discIcon = null;
    private ImageIcon playIcon = null;
    private ImageIcon actualDiscIcon = null;

    public CellRenderCancionMasTocada(ImageIcon playIcon, ImageIcon discIcon) {
        this.playIcon = playIcon;
        this.discIcon = discIcon;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        try {
            this.setOpaque(true);

            Font fuente;
            fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
            fuente = fuente.deriveFont(Font.PLAIN, Rules.FONT_SIZE_CANCIONES);

            this.setFont(fuente);
            DefaultMutableTreeNode v = (DefaultMutableTreeNode) value;
            Object ob = v.getUserObject();

            if (ob instanceof Cancion) {
                
                Cancion c = (Cancion) ob;
                int cant = c.getCantidadReproducciones();
                this.setText(" [ "+cant+" ] "+c.getAutor()+" - "+c.toString() + " ("+c.getDuracionAsString()+") ");
                this.setToolTipText(cant+(cant == 1?" vez reproducida":" veces reproducidas"));
                this.setIcon(null);
            } 
//
//            boolean isCancionActual = false;
//
//            if (JPlay.reproductor != null) {
//                if (ob instanceof Cancion) {
//                    Cancion c = (Cancion) ob;
//                    if (JPlay.reproductor.getCancion().equals(c)) {
//                        System.out.println("igual a " + c.getNombre());
//                        isCancionActual = true;
//                    }
//                }
//            }

            if (selected) {
//                this.setForeground(Color.white);
                this.setBackground(new Color(217, 238, 208));
            } else {
                this.setForeground(Color.black);
                this.setBackground(Color.white);
            }

//            if (isCancionActual) {
////                this.setForeground(Color.red);
////                
////                if (actualDiscIcon == null) {
////                    setIcon(playIcon);
////                }
//                setIcon(playIcon);
//                
//                this.setFont(fuente.deriveFont(Font.BOLD, Rules.FONT_SIZE_CANCIONES));
//                this.setText(this.getText().trim() + "");
//                
//            }

//            if (isDiscoActual) {
//                this.setFont(fuente.deriveFont(Font.BOLD, Rules.FONT_SIZE_CANCIONES));
//            }

        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(CellRenderCancionMasTocada.class.getName()).log(Level.SEVERE, null, ex);
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
    public static ImageIcon crearIcono(String rutaPaquete) {
        return new ImageIcon(CellRenderCancionMasTocada.class.getResource(rutaPaquete));
    }
}

package xjplay.model.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import xjplay.main.JPlay;
import jplay.model.Cancion;
import xjplay.model.rules.Rules;
import xjplay.recursos.Recurso;

/**
 *
 * @author Patricio Pérez Pinto
 */
public class CellRenderCancionLista extends JLabel implements TreeCellRenderer {

    private ImageIcon discIcon = null;
    private ImageIcon playIcon = null;
    private ImageIcon actualDiscIcon = null;

    public CellRenderCancionLista(ImageIcon playIcon, ImageIcon discIcon) {
        this.playIcon = playIcon;
        this.discIcon = discIcon;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        try {
            this.setOpaque(true);

            Font fuente;
            fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
            fuente = fuente.deriveFont(Font.PLAIN, 14);

            this.setFont(fuente);
            DefaultMutableTreeNode v = (DefaultMutableTreeNode) value;
            Object ob = v.getUserObject();

            boolean isDiscoActual = false;

            if (ob instanceof Cancion) {
//                File f = (File) ob;
                Cancion c = (Cancion) ob;
                this.setText("       " + ob.toString());
                if (c.hasImagenes()) {
                    Image im = c.getImagenes().get(0).getImage().getScaledInstance(
                            (int) Rules.MINI_CARATULA.getWidth(),
                            (int) Rules.MINI_CARATULA.getHeight(),
                            Image.SCALE_SMOOTH);
                    actualDiscIcon = new ImageIcon(im);
                    setIcon(actualDiscIcon);
                } else if (c.hasLastFMImage()) {
                    Image im = c.getLastFMImageCover().getImage().getScaledInstance(
                            (int) Rules.MINI_CARATULA.getWidth(),
                            (int) Rules.MINI_CARATULA.getHeight(),
                            Image.SCALE_SMOOTH);
                    actualDiscIcon = new ImageIcon(im);
                    setIcon(actualDiscIcon);
                } else {
                    setIcon(null);
                }
            } else if (ob instanceof String) { // es un disco
                this.setText(ob.toString());
                setIcon(this.discIcon);

                if (JPlay.reproductor != null) {
                    Cancion actual = JPlay.reproductor.getCancion();
                    String disco = ob.toString();

                    if (disco.equalsIgnoreCase(actual.getAutor() + " - " + actual.getAlbum())) {
                        isDiscoActual = true;
                    }
                }
            }

            boolean isCancionActual = false;

            if (JPlay.reproductor != null) {
                if (ob instanceof Cancion) {
                    Cancion c = (Cancion) ob;
                    if (JPlay.reproductor.getCancion().equals(c)) {
                        System.out.println("igual a " + c.getNombre());
                        isCancionActual = true;
                    }
                }
            }

            if (selected) {
//                this.setForeground(Color.white);
                this.setBackground(new Color(217, 238, 208));
            } else {
                this.setForeground(Color.black);
                this.setBackground(Color.white);
            }

            if (isCancionActual) {
//                this.setForeground(Color.red);
//                
                if (actualDiscIcon == null) {
                    setIcon(playIcon);
                }
//                setIcon(playIcon);

                this.setText(">> " + this.getText().trim() + "");
                this.setFont(fuente.deriveFont(Font.BOLD, 14));
            }

            if (isDiscoActual) {
                this.setFont(fuente.deriveFont(Font.BOLD, 14));
            }

        } catch (FontFormatException ex) {
            Logger.getLogger(CellRenderCancionLista.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CellRenderCancionLista.class.getName()).log(Level.SEVERE, null, ex);
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
        return new ImageIcon(CellRenderCancionLista.class.getResource(rutaPaquete));
    }
}

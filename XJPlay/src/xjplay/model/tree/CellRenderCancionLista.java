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
import jplay.model.Album;
import xjplay.main.JPlay;
import jplay.model.Cancion;
import xjplay.model.log.Log;
import xjplay.model.rules.Rules;
import xjplay.recursos.Recurso;
import xjplay.recursos.Ruta;

/**
 *
 * @author Patricio Pérez Pinto
 */
public class CellRenderCancionLista extends JLabel implements TreeCellRenderer {

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

            boolean isDiscoActual = false;

            if (ob instanceof Cancion) {
                Cancion c = (Cancion) ob;
                this.setText(c.getTrack()+".- "+c.toString() + " ("+c.getDuracionAsString()+")");
                this.setIcon(null);
            } else if (ob instanceof Album) { // es un disco
                Album album = (Album) ob;
                this.setText(ob.toString());

                try {// intento colocar el cover que tenga el album
                    Image im = album.getCovers().get(0).getImage().getScaledInstance(
                        (int) Rules.MINI_CARATULA.getWidth(),
                        (int) Rules.MINI_CARATULA.getHeight(),
                        Image.SCALE_SMOOTH);
                    setIcon(new ImageIcon(im));
                } catch (IndexOutOfBoundsException e) {
                    // si no hay cover, cargo el icono de la app
                    setIcon(new ImageIcon(
                        CellRenderCancionLista.crearIcono(Ruta.ICONO_JPLAY).getImage().getScaledInstance(
                            (int) Rules.MINI_CARATULA.getWidth(),
                            (int) Rules.MINI_CARATULA.getHeight(),
                            Image.SCALE_SMOOTH)
                        )
                    );
                }

                if (JPlay.reproductor != null) {
                    Cancion actual = JPlay.reproductor.getCancionActual();
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
                    if (JPlay.reproductor.getCancionActual().equals(c)) {
                        Log.add("igual a " + c.getNombre());
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
//                if (actualDiscIcon == null) {
//                    setIcon(playIcon);
//                }
//                setIcon(playIcon);
//                
//                this.setFont(fuente.deriveFont(Font.BOLD, Rules.FONT_SIZE_CANCIONES));
//                this.setText(this.getText().trim() + "");
                this.setBackground(Color.black);
                this.setForeground(Color.white);
            }

            if (isDiscoActual) {
                this.setBackground(Color.black);
                this.setForeground(Color.white);
//                this.setFont(fuente.deriveFont(Font.BOLD, Rules.FONT_SIZE_CANCIONES));
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

package cl.prezdev.xjplay.model.tree;

import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.main.JPlay;
import cl.prezdev.xjplay.recursos.Recurso;
import cl.prezdev.xjplay.recursos.Ruta;
import cl.prezdev.xjplay.rules.Rule;
import cl.prezdev.xjplay.utils.Util;
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

public class CellRenderCancionLista extends JLabel implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//        try {
            tree.setBackground(Util.COLOR_FONDO.brighter());
            this.setBackground(Util.COLOR_FONDO.brighter());
            
            this.setOpaque(true);

//            Font fuente;
//            fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
//            fuente = fuente.deriveFont(Font.PLAIN, Rule.FONT_SIZE_CANCIONES);
//
//            this.setFont(fuente);
            DefaultMutableTreeNode v = (DefaultMutableTreeNode) value;
            Object ob = v.getUserObject();

            boolean isDiscoActual = false;

            if (ob instanceof Song) {
                Song c = (Song) ob;
                this.setText(c.getTrackNumber()+".- "+c.toString() + " ("+c.getDurationAsString()+")");
                this.setIcon(null);
            } else if (ob instanceof Album) { // es un disco
                Album album = (Album) ob;
                this.setText(album.getYear() + " " +ob.toString());

                try {// intento colocar el cover que tenga el album
                    Image im = album.getCovers().get(0).getImage().getScaledInstance((int) Rule.MINI_CARATULA.getWidth(),
                        (int) Rule.MINI_CARATULA.getHeight(),
                        Image.SCALE_SMOOTH);
                    setIcon(new ImageIcon(im));
                } catch (IndexOutOfBoundsException e) {
                    // si no hay cover, cargo el icono de la app
                    setIcon(new ImageIcon(
                        CellRenderCancionLista.crearIcono(Ruta.ICONO_JPLAY).getImage().getScaledInstance((int) Rule.MINI_CARATULA.getWidth(),
                            (int) Rule.MINI_CARATULA.getHeight(),
                            Image.SCALE_SMOOTH)
                        )
                    );
                }

                if (JPlay.reproductor != null) {
                    Song actual = JPlay.reproductor.getCurrentSong();
                    String disco = ob.toString();

                    if (disco.equalsIgnoreCase(actual.getAuthor() + " - " + actual.getAlbum())) {
                        isDiscoActual = true;
                    }
                }
            }

            boolean isCancionActual = false;

            if (JPlay.reproductor != null) {
                if (ob instanceof Song) {
                    Song c = (Song) ob;
                    if (JPlay.reproductor.getCurrentSong().equals(c)) {
                        isCancionActual = true;
                    }
                }
            }

            Color colorFondo = Util.COLOR_FONDO.darker().darker();
            Color colorFore = Util.getForeGroundColorBasedOnBGBrightness(colorFondo);
            
            if (selected) {
                this.setForeground(colorFore);
                this.setBackground(colorFondo);
            } else {
                this.setForeground(Util.getForeGroundColorBasedOnBGBrightness(Util.COLOR_FONDO.brighter()));
                this.setBackground(Util.COLOR_FONDO.brighter());
            }

            if (isCancionActual) {
                this.setForeground(colorFore);
                this.setBackground(colorFondo);
            }

            if (isDiscoActual) {
                this.setForeground(colorFore);
                this.setBackground(colorFondo);
            }
//        } catch (FontFormatException | IOException ex) {
//            Logger.getLogger(CellRenderCancionLista.class.getName()).log(Level.SEVERE, null, ex);
//        }

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

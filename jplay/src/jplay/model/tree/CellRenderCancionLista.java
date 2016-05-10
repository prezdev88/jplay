package jplay.model.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import jplay.main.JPlay;
import jplay.model.Cancion;

/**
 *
 * @author Patricio Pérez Pinto
 */
public class CellRenderCancionLista extends JLabel implements TreeCellRenderer {

    private ImageIcon discIcon = null;
    private ImageIcon playIcon = null;

    public CellRenderCancionLista(ImageIcon playIcon, ImageIcon discIcon) {
        this.playIcon = playIcon;
        this.discIcon = discIcon;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        try {
            this.setOpaque(true);

            Font fuente;
            InputStream myStream ;
            String font = "Roboto-Regular.ttf";

            //"src/jplay/font/Roboto-Regular.ttf"
            myStream = new BufferedInputStream(new FileInputStream("src/jplay/font/" + font));
            fuente = Font.createFont(Font.TRUETYPE_FONT, myStream);
            fuente = fuente.deriveFont(Font.PLAIN, 14);

            
            this.setFont(fuente);
//            this.setFont(new Font("Roboto-Regulars", Font.PLAIN, 12));
            DefaultMutableTreeNode v = (DefaultMutableTreeNode) value;
            Object ob = v.getUserObject();
            
            boolean isDiscoActual = false;
            
            if (ob instanceof Cancion) {
                File f = (File) ob;
                this.setText("       " + f.toString());
                setIcon(null);
            } else if (ob instanceof String) {
                this.setText(ob.toString());
                setIcon(this.discIcon);
                
                if (JPlay.reproductor != null) {
                    Cancion actual = JPlay.reproductor.getCancion();
                    String disco = ob.toString();
                    
                    if (disco.equalsIgnoreCase(actual.getAutor()+" - "+actual.getAlbum())) {
                        isDiscoActual = true;
                    } 
                }
            }
            
            
            boolean isCancionActual = false;

            if (JPlay.reproductor != null) {
                if (ob instanceof Cancion) {
                    Cancion c = (Cancion) ob;
                    if (JPlay.reproductor.getCancion().equals(c)) {
                        System.out.println("igual a "+c.getNombre());
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
            
            if(isCancionActual){
//                this.setForeground(Color.red);
                this.setIcon(playIcon);
                this.setText(this.getText().trim());
                this.setFont(fuente.deriveFont(Font.BOLD, 14));
            }
            
            if(isDiscoActual){
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

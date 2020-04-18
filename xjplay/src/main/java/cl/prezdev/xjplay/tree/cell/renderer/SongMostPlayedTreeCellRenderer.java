package cl.prezdev.xjplay.tree.cell.renderer;

import cl.prezdev.jplay.Song;
import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class SongMostPlayedTreeCellRenderer extends JLabel implements TreeCellRenderer {

    private ImageIcon discIcon = null;
    private ImageIcon playIcon = null;
    private ImageIcon actualDiscIcon = null;

    public SongMostPlayedTreeCellRenderer(ImageIcon playIcon, ImageIcon discIcon) {
        this.playIcon = playIcon;
        this.discIcon = discIcon;
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
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) value;
            Object userObject = defaultMutableTreeNode.getUserObject();

            if (userObject instanceof Song) {
                Song song = (Song) userObject;
                
                int playCount = song.getPlayCount();
                
                this.setText(" [ "+playCount+" ] "+song.getAuthor()+" - "+song.toString() + " ("+song.getDurationAsString()+") ");
                this.setToolTipText(playCount+(playCount == 1?" vez reproducida":" veces reproducidas"));
                this.setIcon(null); // @TODO: probar sacando esta linea
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
                // @TODO: Almacenar color en alguna clasem hasta ahora se 
                // almacena en Util
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
//                this.setFont(fuente.deriveFont(Font.BOLD, Rule.FONT_SIZE_CANCIONES));
//                this.setText(this.getText().trim() + "");
//                
//            }

//            if (isDiscoActual) {
//                this.setFont(fuente.deriveFont(Font.BOLD, Rule.FONT_SIZE_CANCIONES));
//            }

//        } catch (FontFormatException | IOException ex) {
//            Logger.getLogger(CellRenderCancionMasTocada.class.getName()).log(Level.SEVERE, null, ex);
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
    // @TODO: Desacoplar este método
    // Este método ya esta en SongListTreeCellRenderer y ExplorerTreeCellRenderer
    public static ImageIcon crearIcono(String rutaPaquete) {
        return new ImageIcon(SongMostPlayedTreeCellRenderer.class.getResource(rutaPaquete));
    }
}

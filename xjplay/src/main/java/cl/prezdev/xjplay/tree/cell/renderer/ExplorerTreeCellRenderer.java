package cl.prezdev.xjplay.tree.cell.renderer;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 * Este es el render del explorador del lado izquierdo
 */
public class ExplorerTreeCellRenderer extends JLabel implements TreeCellRenderer {
    // @TODO: null?? 
    private ImageIcon folderIcon = null;
    private ImageIcon musicIcon = null;

    public ExplorerTreeCellRenderer(ImageIcon musicIcon, ImageIcon folderIcon) {
        this.musicIcon = musicIcon;
        this.folderIcon = folderIcon;
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus
    ) {
//        try {
        this.setOpaque(true);

//            Font fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
//            fuente = fuente.deriveFont(Font.PLAIN, Rule.FONT_SIZE_EXPLORER);


        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) value;
        Object userObject = defaultMutableTreeNode.getUserObject();
        if (userObject instanceof File) {
//                this.setFont(fuente);
            File file = (File) userObject;

            this.setText(file.toString());

            if (file.isDirectory()) {
                setIcon(this.folderIcon);
            } else {
                setIcon(this.musicIcon);
            }
        }

        if (selected) {
            this.setForeground(Color.white);
            // @TODO: Almacenar color en alguna clasem hasta ahora se
            // almacena en Util
            this.setBackground(new Color(51, 153, 255));
        } else {
            this.setForeground(Color.black);
            this.setBackground(Color.white);
        }

//            
//        } catch (FontFormatException ex) {
//            Logger.getLogger(CellRenderExplorer.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(CellRenderExplorer.class.getName()).log(Level.SEVERE, null, ex);
//        } 

        return this;
    }

    /**
     * Método para construir un icono a través de la ruta de un paquete
     *
     * @param rutaPaquete ruta del paquete. Ejemplo:
     *                    "/xml/images/16atributo.png"
     * @return Un objeto del tipo ImageIcon
     */
    // @TODO: Desacoplar este método
    // Este método ya esta en SongListTreeCellRenderer
    public static ImageIcon getImageIcon(String rutaPaquete) {
        return new ImageIcon(ExplorerTreeCellRenderer.class.getResource(rutaPaquete));
    }
}

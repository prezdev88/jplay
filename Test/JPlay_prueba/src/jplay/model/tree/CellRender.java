package jplay.model.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Patricio Pérez Pinto
 */
public class CellRender extends JLabel implements TreeCellRenderer{
    private ImageIcon folderIcon = null;
    private ImageIcon musicIcon = null;
    
    public CellRender(ImageIcon musicIcon, ImageIcon folderIcon){
        this.musicIcon = musicIcon;
        this.folderIcon = folderIcon;
    }
    
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.setOpaque(true);
        this.setFont(new Font("Verdana", Font.PLAIN, 12));
        
        DefaultMutableTreeNode v = (DefaultMutableTreeNode)value;
        Object ob = v.getUserObject();
        if(ob instanceof File){
            this.setFont(new Font("Verdana", Font.BOLD, 12));
            File f = (File)ob;
            this.setText(f.toString());
            if(f.isDirectory()){
                setIcon(this.folderIcon);
            }else{
                setIcon(this.musicIcon);
            }
        }
        
        if(selected){
            this.setForeground(Color.white);
            this.setBackground(new Color(51, 153, 255));
        }else{
            this.setForeground(Color.black);
            this.setBackground(Color.white);
        }
        
        return this;
    }
    
    /**
     * Método para construir un icono a través de la ruta de un paquete
     * @param rutaPaquete ruta del paquete. Ejemplo: 
     * "/xml/images/16atributo.png"
     * @return Un objeto del tipo ImageIcon
     */
    public static ImageIcon crearIcono(String rutaPaquete){
        return new ImageIcon(CellRender.class.getResource(rutaPaquete));
    }
}

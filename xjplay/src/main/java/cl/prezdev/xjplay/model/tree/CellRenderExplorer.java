package cl.prezdev.xjplay.model.tree;

import cl.prezdev.xjplay.recursos.Recurso;
import cl.prezdev.xjplay.rules.Rule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;


/**
 *
 * @author Patricio Pérez Pinto
 * 
 * Este es el render del explorador del lado izquierdo
 */

public class CellRenderExplorer extends JLabel implements TreeCellRenderer{
    private ImageIcon folderIcon = null;
    private ImageIcon musicIcon = null;
    
    public CellRenderExplorer(ImageIcon musicIcon, ImageIcon folderIcon){
        this.musicIcon = musicIcon;
        this.folderIcon = folderIcon;
    }
    
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//        try {
            this.setOpaque(true);
            
//            Font fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
//            fuente = fuente.deriveFont(Font.PLAIN, Rule.FONT_SIZE_EXPLORER);
            
            
            DefaultMutableTreeNode v = (DefaultMutableTreeNode)value;
            Object ob = v.getUserObject();
            if(ob instanceof File){
//                this.setFont(fuente);
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
     * @param rutaPaquete ruta del paquete. Ejemplo: 
     * "/xml/images/16atributo.png"
     * @return Un objeto del tipo ImageIcon
     */
    public static ImageIcon crearIcono(String rutaPaquete){
        return new ImageIcon(CellRenderExplorer.class.getResource(rutaPaquete));
    }
}

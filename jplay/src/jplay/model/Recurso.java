package jplay.model;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Recurso {
    public static final File FUENTE_ROBOTO = 
            new File("recursos"+File.separator+"Roboto-Regular.ttf");
    
    public static final Image ICONO_JPLAY = new ImageIcon(Recurso.class.getResource(Ruta.ICONO_JPLAY)).getImage();
    
    public static final Dimension CARATULA = new Dimension(168, 168);

    public static List<File> getFotos(File f){
        List<File> fotos = new ArrayList<>();
        
        for(File archivo : f.getParentFile().listFiles()){
            System.out.println(archivo.getName());
            if(
                archivo.getName().contains(".jpg") ||
                archivo.getName().contains(".png")
            ){
                fotos.add(archivo);
            }
        }
        
        return fotos;
    }
}

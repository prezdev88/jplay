package xjplay.recursos;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import javax.swing.ImageIcon;
import xjplay.model.rules.Rules;

public class Recurso {

    public static final File FUENTE_ROBOTO
            = new File("recursos" + File.separator + "Roboto-Regular.ttf");
    public static final File FUENTE_TYPEWRITER
            = new File("recursos" + File.separator + "TravelingTypewriter.ttf");

    public static final Image ICONO_JPLAY = new ImageIcon(Recurso.class.getResource(Ruta.ICONO_JPLAY)).getImage();

    

    public static List<ImageIcon> getFotos(File f) {
        List<ImageIcon> fotos = new ArrayList<>();
        Image image;
        ImageIcon imageIcon;
        for (File archivo : f.getParentFile().listFiles()) {
            System.out.println(archivo.getName());
            if (archivo.getName().contains(".jpg")
                    || archivo.getName().contains(".png")) {
                image = new ImageIcon(archivo.getPath()).getImage();
                System.out.println("RUTA FOTO: "+archivo.getPath());
                imageIcon = new ImageIcon(
                        image.getScaledInstance(
                                (int)Rules.CARATULA.getWidth(),
                                (int)Rules.CARATULA.getHeight(),
                                Image.SCALE_SMOOTH)
                );
                fotos.add(imageIcon);
            }
        }

        return fotos;
    }

//    public static List<File> getFotos(File f){
//        List<File> fotos = new ArrayList<>();
//        
//        for(File archivo : f.getParentFile().listFiles()){
//            System.out.println(archivo.getName());
//            if(
//                archivo.getName().contains(".jpg") ||
//                archivo.getName().contains(".png")
//            ){
//                fotos.add(archivo);
//            }
//        }
//        
//        return fotos;
//    }
}

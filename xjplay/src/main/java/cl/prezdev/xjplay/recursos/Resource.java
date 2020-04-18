package cl.prezdev.xjplay.recursos;

import cl.prezdev.jlog.Log;
import cl.prezdev.xjplay.rules.Rule;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Resource {

    /*
    public static final File FUENTE_ROBOTO
            = new File("/fonts/Roboto-Regular.ttf");
    public static final File FUENTE_TYPEWRITER
            = new File("/fonts/TravelingTypewriter.ttf");
    */

    public static final Image JPLAY_ICON = new ImageIcon(Resource.class.getResource(Path.JPLAY_ICON)).getImage();

    /**
     * Obtiene una lista de imagenes de una ruta en específico
     * @param rootFile
     * @return 
     */
    public static List<ImageIcon> getCoversArt(File rootFile) {
        List<ImageIcon> coversArt = new ArrayList<>();
        ImageIcon coverArt;
        Image image;
        
        for (File file : rootFile.getParentFile().listFiles()) {
            Log.add(file.getName());
            if (file.getName().contains(".jpg")
                    || file.getName().contains(".png")) {
                image = new ImageIcon(file.getPath()).getImage();
                
                Log.add("RUTA FOTO: "+file.getPath());
                
                coverArt = new ImageIcon(
                    image.getScaledInstance(
                        (int)Rule.COVER_ART_DIMENSION.getWidth(),
                        (int)Rule.COVER_ART_DIMENSION.getHeight(),
                        Image.SCALE_SMOOTH
                    )
                );
                
                coversArt.add(coverArt);
            }
        }

        return coversArt;
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

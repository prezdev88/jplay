package cl.prezdev.xjplay.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Resource {

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
            if (file.getName().contains(".jpg")
                    || file.getName().contains(".png")) {
                image = new ImageIcon(file.getPath()).getImage();

                /*
                coverArt = new ImageIcon(
                    image.getScaledInstance(
                        (int)Rule.COVER_ART_DIMENTION.getWidth(),
                        (int)Rule.COVER_ART_DIMENTION.getHeight(),
                        Image.SCALE_SMOOTH
                    )
                );
                 */
                coverArt = new ImageIcon(image);
                
                coversArt.add(coverArt);
            }
        }

        return coversArt;
    }
}

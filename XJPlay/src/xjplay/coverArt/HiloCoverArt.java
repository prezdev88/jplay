package xjplay.coverArt;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import xjplay.model.rules.Rules;
/**
 *
 * @author pperezp
 * -Dsun.java2d.opengl=true
 */
public class HiloCoverArt extends Thread {
    private JLabel lbl;
    private List<ImageIcon> imagenes;

    public HiloCoverArt(JLabel lbl, List<ImageIcon> imagenes) {
        this.lbl = lbl;
        this.imagenes = new ArrayList<>();
        ImageIcon ii;
        for (ImageIcon im : imagenes) {
            ii = new ImageIcon(im.getImage().getScaledInstance(Rules.COVER_DIMENSION.width, Rules.COVER_DIMENSION.height, Image.SCALE_SMOOTH));
            this.imagenes.add(ii);
        }
    }
    
    @Override
    public void run() {
        
        try {
//            lbl1.setIcon(imagenes.get(0));
            while (true) {
                for (int i = 0; i < imagenes.size(); i++) {
                    lbl.setIcon(imagenes.get(i));
                    Thread.sleep(Rules.PAUSE_ENTRE_FOTOS);
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("Hilo covert art interrumpido");
        }
    }
}

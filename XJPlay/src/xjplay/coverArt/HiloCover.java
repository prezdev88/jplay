package xjplay.coverArt;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import xjplay.model.log.Log;
import xjplay.model.rules.Rules;
/**
 *
 * @author pperezp
 * -Dsun.java2d.opengl=true
 */
public class HiloCover extends Thread {
    private final JLabel lbl;
    private final List<ImageIcon> covers;
    private ImageIcon actualCover;

    public HiloCover(JLabel lblCover, List<ImageIcon> covers) {
        this.covers = covers;
        this.lbl = lblCover;
        /*
        Esto no es necesario, ya que al momento de incluirlas en el album
        ya hacia esta conversión de tamaños
        
        VER: método getFotos(de Recurso.java)
        */
//        this.covers = new ArrayList<>();
//        ImageIcon ii;
//        for (ImageIcon im : covers) {
//            ii = new ImageIcon(im.getImage().getScaledInstance(Rules.COVER_DIMENSION.width, Rules.COVER_DIMENSION.height, Image.SCALE_SMOOTH));
//            this.covers.add(ii);
//        }
    }
    
    @Override
    public void run() {
        
        try {
//            lbl1.setIcon(imagenes.get(0));
            while (true) {
                for (int i = 0; i < covers.size(); i++) {
                    actualCover = covers.get(i);
                    lbl.setIcon(actualCover);
                    Thread.sleep(Rules.PAUSE_ENTRE_FOTOS);
                }
            }
        } catch (InterruptedException ex) {
            Log.add("Hilo covert art interrumpido");
        }
    }
    
    public ImageIcon getActualCover(){
        return this.actualCover;
    }
    
}

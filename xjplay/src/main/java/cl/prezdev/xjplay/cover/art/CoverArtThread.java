package cl.prezdev.xjplay.cover.art;

import cl.prezdev.jlog.Log;
import cl.prezdev.xjplay.rules.Rule;
import java.awt.Image;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author pperezp -Dsun.java2d.opengl=true
 */
public class CoverArtThread extends Thread {

    private final JLabel coverArtLabel;
    private final List<ImageIcon> coverArtImages;
    private ImageIcon currentCoverArt;

    public CoverArtThread(JLabel coverArtLabel, List<ImageIcon> coverArtImages) {
        this.coverArtImages = coverArtImages;
        this.coverArtLabel = coverArtLabel;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (int i = 0; i < coverArtImages.size(); i++) {
                    currentCoverArt = coverArtImages.get(i);
                    coverArtLabel.setIcon(new ImageIcon(getCurrentCoverArt()));
                    Thread.sleep(Rule.PAUSE_BETWEEN_COVERS);
                }
            }
        } catch (InterruptedException ex) {
            Log.add("Hilo covert art interrumpido");
        }
    }

    public Image getCurrentCoverArt() {
        return this.currentCoverArt.getImage().getScaledInstance(
            (int) Rule.COVER_DIMENSION.getWidth(),
            (int) Rule.COVER_DIMENSION.getHeight(),
            Image.SCALE_SMOOTH
        );
    }
}

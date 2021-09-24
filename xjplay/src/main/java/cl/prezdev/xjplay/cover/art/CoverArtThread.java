package cl.prezdev.xjplay.cover.art;

import cl.prezdev.xjplay.rules.Rule;

import java.awt.Image;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * @author pperezp -Dsun.java2d.opengl=true
 */
public class CoverArtThread extends Thread {

    private final JLabel coverArtLabel;
    private final List<ImageIcon> coverArtImages;
    private ImageIcon currentCoverArt;

    private static final Logger LOGGER = Logger.getLogger("cl.prezdev.xjplay.cover.art.CoverArtThread");

    public CoverArtThread(JLabel coverArtLabel, List<ImageIcon> coverArtImages) {
        this.coverArtImages = coverArtImages;
        this.coverArtLabel = coverArtLabel;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (ImageIcon coverArtImage : coverArtImages) {
                    currentCoverArt = coverArtImage;
                    coverArtLabel.setIcon(new ImageIcon(getCurrentCoverArt()));
                    Thread.sleep(Rule.BETWEEN_COVERS_PAUSE);
                }
            }
        } catch (InterruptedException interruptedException) {
            LOGGER.log(Level.WARNING, "Interrupted!", interruptedException);
            Thread.currentThread().interrupt();
        }
    }

    public Image getCurrentCoverArt() {
        return this.currentCoverArt.getImage().getScaledInstance(
                (int) Rule.COVER_ART_DIMENTION.getWidth(),
                (int) Rule.COVER_ART_DIMENTION.getHeight(),
                Image.SCALE_SMOOTH
        );
    }
}

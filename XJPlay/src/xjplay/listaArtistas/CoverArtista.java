package xjplay.listaArtistas;

import java.awt.Image;
import java.io.Serializable;
import javax.swing.ImageIcon;
import xjplay.model.lastFM.LastFM;
import xjplay.rules.Rule;

public class CoverArtista implements Serializable {

    private final String artista;
    private final ImageIcon cover;

    public CoverArtista(String artista) throws Exception {
        this.artista = artista;

        Image image = LastFM.getImage(artista);

        cover = new ImageIcon(image.getScaledInstance(
                Rule.ARTISTA_COVER.width,
                Rule.ARTISTA_COVER.height,
                Image.SCALE_SMOOTH)
        );
    }

    public String getArtista() {
        return artista;
    }

    public ImageIcon getCover() {
        return cover;
    }

    @Override
    public String toString() {
        return artista;
    }
}

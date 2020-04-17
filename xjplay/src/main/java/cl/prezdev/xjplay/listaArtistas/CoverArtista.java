package cl.prezdev.xjplay.listaArtistas;

import cl.prezdev.xjplay.model.lastFM.LastFM;
import cl.prezdev.xjplay.rules.Rule;
import java.awt.Image;
import java.io.Serializable;
import javax.swing.ImageIcon;

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

package cl.prezdev.xjplay.artist.list;

import cl.prezdev.xjplay.model.lastFM.LastFM;
import cl.prezdev.xjplay.recursos.Resource;
import cl.prezdev.xjplay.rules.Rule;
import java.awt.Image;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class ArtistCoverArt implements Serializable {

    private final String artistName;
    private final ImageIcon covertArt;

    public ArtistCoverArt(String artistName) throws Exception {
        this.artistName = artistName;

        Image artistCoverArt = LastFM.getCoverArt(artistName);

        if(artistCoverArt != null){
            covertArt = new ImageIcon(artistCoverArt.getScaledInstance(
                Rule.ARTIST_COVER_ART.width,
                Rule.ARTIST_COVER_ART.height,
                Image.SCALE_SMOOTH)
            );
        }else{
            covertArt = new ImageIcon(
                Resource.JPLAY_ICON.getScaledInstance(
                    Rule.ARTIST_COVER_ART.width,
                    Rule.ARTIST_COVER_ART.height,
                    Image.SCALE_SMOOTH
                )
            );
        }
    }

    public String getArtistName() {
        return artistName;
    }

    public ImageIcon getCoverArt() {
        return covertArt;
    }

    @Override
    public String toString() {
        return artistName;
    }
}

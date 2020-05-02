package cl.prezdev.xjplay.rules;

import java.awt.*;

// @TODO: Agrupar reglas en clases staticas
public class Rule {
    // @TODO: Cambiar el nombre de variables
    /*SCAN DE RUTAS DE BIBLIOTECAS*/
    private static final int SECOND                = 1;
    private static final int MINUTE                 = 1000 * Rule.SECOND;
    private static final int PAUSE_SCAN_MINUTES     = 5;
    public static int PAUSE_SCAN                    = Rule.PAUSE_SCAN_MINUTES * Rule.MINUTE;
    /*SCAN DE RUTAS DE BIBLIOTECAS*/
    
    public static final String NAME               = "JPlay";
    public static final String VERSION              = "rev. 0.6.2";
    
    public static int MOVE_PAUSE                   = 3;         // el pause para mover la x de la foto
    public static int BETWEEN_COVERS_PAUSE             = 10000;     // pause entre cada fotos;
    public static int SEARCH_FONT_SIZE              = 13;
    
    public static int SONG_FONT_SIZE           = 12;
    public static int NORMAL_FONT_SIZE              = 12;
    public static int TRACK_NUMBER_COLUMN_SIZE      = 12;
    public static int ARTIST_COLUMN_SIZE            = 100;
    public final static int ALBUM_COLUMN_SIZE             = 100;
    public static int NAME_COLUMN_SIZE              = 300;
    public static String API_KEY                    = "e4af175c34493321a0df649859069c40";
    // No es final, porque en el constructor de JPlay se establece según el jlabel del cover
    public static Dimension COVER_ART_DIMENTION;
    public static final Dimension COVERT_ART_MINI     = new Dimension(48, 48);
    public static final Dimension ARTIST_COVER_ART     = new Dimension(200, 200);
    
    /*EXPLORER*/
    public static final int ICON_EXPLORER_SIZE          = 14;
    public static final int ICON_EXPLORER_MUSIC_SIZE    = 14;
    public static int FONT_SIZE_EXPLORER                = 12;
    /*EXPLORER*/

    public static Color BACKGROUND_COLOR = Color.white;
    public static Color FOREGROUND_COLOR = Color.black;
    
    public static class TabIndex {
        public static final int EXPLORER        = 0;
        public static final int MUSIC_LIBRARY      = 1;
        public static final int CURRENT_SONGS_LIST = 2;
        public static final int MOST_PLAYED  = 3;
        public static final int FAVORITES       = 4;
        public static final int LOGGER          = 5;
    }
    
    public static final int WIDTH               = 900;
    public static final int HEIGHT                = 600;
    
    
}

package xjplay.rules;

import java.awt.Dimension;

public class Rule {
    /*SCAN DE RUTAS DE BIBLIOTECAS*/
    private static final int SEGUNDO                = 1;
    private static final int MINUTO                 = 1000 * Rule.SEGUNDO;
    private static final int MINUTOS_PAUSE_SCAN     = 5;
    public static int PAUSE_SCAN                    = Rule.MINUTOS_PAUSE_SCAN * Rule.MINUTO;
    /*SCAN DE RUTAS DE BIBLIOTECAS*/
    
    public static final String NOMBRE               = "JPlay";
    public static final String VERSION              = "rev. 0.6.2";
    
    public static int PAUSE_MOVER                   = 3;         // el pause para mover la x de la foto
    public static int PAUSE_ENTRE_FOTOS             = 10000;     // pause entre cada fotos;
    public static int FONT_SIZE_SEARCH              = 13;
    
    public static int FONT_SIZE_CANCIONES           = 12;
    public static int FONT_SIZE_NORMAL              = 12;
    public static int TRACK_NUMBER_COLUMN_SIZE      = 12;
    public static int ARTIST_COLUMN_SIZE            = 100;
    public final static int ALBUM_COLUMN_SIZE             = 100;
    public static int NAME_COLUMN_SIZE              = 300;
    public static String API_KEY                    = "e4af175c34493321a0df649859069c40";
    // No es final, porque en el constructor de JPlay se establece según el jlabel del cover
    public static Dimension COVER_DIMENSION;
    public static final Dimension MINI_CARATULA     = new Dimension(48, 48);
    
    /*EXPLORER*/
    public static final int ICON_EXPLORER_SIZE          = 14;
    public static final int ICON_EXPLORER_MUSIC_SIZE    = 14;
    public static int FONT_SIZE_EXPLORER                = 12;
    /*EXPLORER*/
    
    public static class Tabs{
        public static final int EXPLORER        = 0;
        public static final int BIBLIOTECA      = 1;
        public static final int LISTA_ACTUAL    = 2;
        public static final int MAS_ESCUCHADAS  = 3;
        public static final int FAVORITOS       = 4;
        public static final int LOGGER          = 5;
    }
    
    public static final int ANCHO               = 900;
    public static final int ALTO                = 600;
    
    
}

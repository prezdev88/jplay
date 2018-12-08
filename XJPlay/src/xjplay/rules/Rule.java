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
    public static final String VERSION              = "rev. 0.6";
    
    public static int PAUSE_MOVER                   = 3;         // el pause para mover la x de la foto
    public static int PAUSE_ENTRE_FOTOS             = 10000;     // pause entre cada fotos;
    public static int FONT_SIZE_SEARCH              = 13;
    
    public static int FONT_SIZE_CANCIONES           = 14;
    public static int FONT_SIZE_NORMAL              = 17;
    public static int TRACK_NUMBER_COLUMN_SIZE      = 15;
    public static int ARTIST_COLUMN_SIZE            = 100;
    public final static int ALBUM_COLUMN_SIZE             = 100;
    public static int NAME_COLUMN_SIZE              = 300;
    public static String API_KEY                    = "e4af175c34493321a0df649859069c40";
    public static final Dimension COVER_DIMENSION   = new Dimension(155, 155);
    public static final Dimension MINI_CARATULA     = new Dimension(48, 48);
    
    /*EXPLORER*/
    public static final int ICON_EXPLORER_SIZE          = 20;
    public static final int ICON_EXPLORER_MUSIC_SIZE    = 20;
    public static int FONT_SIZE_EXPLORER                = 14;
    /*EXPLORER*/
    
    public static class Tabs{
        public static final int EXPLORER        = 0;
        public static final int BIBLIOTECA      = 1;
        public static final int LISTA_ACTUAL    = 2;
        public static final int MAS_ESCUCHADAS  = 3;
        public static final int FAVORITOS       = 4;
        public static final int LOGGER          = 5;
    }
    
    public static final int ANCHO               = 1024;
    public static final int ALTO                = 502;
    
    
}

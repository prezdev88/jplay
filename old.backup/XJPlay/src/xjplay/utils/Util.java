package xjplay.utils;

import java.awt.Color;

public class Util {
    
    public static Color COLOR_FONDO = Color.white;
    public static Color COLOR_FOREGROUND = Color.black;
    

    /*http://tech.chitgoks.com/2010/07/27/check-if-color-is-dark-or-light-using-java/*/
    private static int getBrightness(Color c) {
        return (int) Math.sqrt(
                c.getRed() * c.getRed() * .241
                + c.getGreen() * c.getGreen() * .691
                + c.getBlue() * c.getBlue() * .068);
    }

    public static Color getForeGroundColorBasedOnBGBrightness(Color color) {
        if (getBrightness(color) < 130) {
            return Color.white;
        } else {
            return Color.black;
        }
    }
    /*http://tech.chitgoks.com/2010/07/27/check-if-color-is-dark-or-light-using-java/*/
}

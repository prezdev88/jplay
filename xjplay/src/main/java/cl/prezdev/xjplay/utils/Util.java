package cl.prezdev.xjplay.utils;

import java.awt.Color;

public class Util {
    
    public static Color BACKGROUND_COLOR = Color.white;
    public static Color FOREGROUND_COLOR = Color.black;
    
    /*http://tech.chitgoks.com/2010/07/27/check-if-color-is-dark-or-light-using-java/*/
    private static int getBrightness(Color color) {
        return (int) Math.sqrt(
                color.getRed() * color.getRed() * .241
                + color.getGreen() * color.getGreen() * .691
                + color.getBlue() * color.getBlue() * .068);
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

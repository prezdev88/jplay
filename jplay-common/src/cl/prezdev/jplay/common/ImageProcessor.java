package cl.prezdev.jplay.common;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {

    private ImageProcessor() {
        throw new UnsupportedOperationException();
    }

    public static Color getAverageColor(ImageIcon imageIcon) {
        return ImageProcessor.getAverageColor(imageIcon.getImage());
    }

    public static Color getAverageColor(Image image) {
        BufferedImage bufferedImage = imageToBufferedImage(image);

        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        int totalPixels = height * width;
        int color;
        int red;
        int green;
        int blue;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color = bufferedImage.getRGB(j, i);
                red = (color & 0x00ff0000) >> 16;
                green = (color & 0x0000ff00) >> 8;
                blue = color & 0x000000ff;

                redSum += red;
                greenSum += green;
                blueSum += blue;
            }
        }

        int redAverage = redSum / totalPixels;
        int greenAverage = greenSum / totalPixels;
        int blueAverage = blueSum / totalPixels;

        return new Color(redAverage, greenAverage, blueAverage);
    }

    public static BufferedImage imageToBufferedImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics graphics = bufferedImage.getGraphics();

        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return bufferedImage;
    }

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

}

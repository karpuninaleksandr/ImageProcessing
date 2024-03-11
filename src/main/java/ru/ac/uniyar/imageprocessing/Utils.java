package ru.ac.uniyar.imageprocessing;

import java.awt.image.BufferedImage;

public class Utils {

    public static int[] getPixelsArray(BufferedImage bufferedImage) {
        int[] pict = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
        for (int i = 0; i < bufferedImage.getHeight(); i++)
            for (int j = 0; j < bufferedImage.getWidth(); j++)
                pict[i * bufferedImage.getWidth() + j] = bufferedImage.getRGB(j, i) & 0xFFFFFF;
        return pict;
    }

    public static void updateBufferedImagePixels(BufferedImage bufferedImage, int[] pixels) {
        bufferedImage.setRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());
    }

    public static int getRed(int color) {
        return color >> 16;
    }
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    public static int getBlue(int color) {
        return color & 0xFF;
    }
}

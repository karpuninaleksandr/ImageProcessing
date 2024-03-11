package ru.ac.uniyar.imageprocessing.processor;

import ru.ac.uniyar.imageprocessing.Utils;
import ru.ac.uniyar.imageprocessing.model.ImageContainer;
import ru.ac.uniyar.imageprocessing.model.ProcessType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageProcessor {
    public static ImageContainer processImage(ImageContainer image, ProcessType processType, List<Double> params) {
        BufferedImage resultImage =  switch (processType) {
            case HALFTONE -> halftoneProcessing(image);
            case BINARY -> binaryProcessing(image);
            case NEGATIVE -> negativeProcessing(image);
            case LOGARITHM -> logarithmProcessing(image, params);
            case POWER -> powerProcessing(image, params);
        };
        ImageContainer result = new ImageContainer();
        result.setName(image.getName());
        result.setType(image.getType());
        result.updateValueViaBufferedImage(resultImage);
        return result;
    }

    private static BufferedImage halftoneProcessing(ImageContainer image) {
        BufferedImage bufferedImage = image.getAsBufferedImage();
        int[] pixels = Utils.getPixelsArray(bufferedImage);

        for (int i = 0; i < bufferedImage.getHeight(); i++)
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                int intense = (Utils.getRed(pixels[i * bufferedImage.getWidth() + j]) +
                        Utils.getGreen(pixels[i * bufferedImage.getWidth() + j]) +
                        Utils.getBlue(pixels[i * bufferedImage.getWidth() + j])) / 3;
                pixels[i * bufferedImage.getWidth() + j] = intense + (intense << 8) + (intense << 16);
            }

        Utils.updateBufferedImagePixels(bufferedImage, pixels);

        return bufferedImage;
    }

    private static BufferedImage binaryProcessing(ImageContainer image) {
        BufferedImage bufferedImage = image.getAsBufferedImage();

        BufferedImage binaryImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < bufferedImage.getHeight(); ++i)
            for (int j = 0; j < bufferedImage.getWidth(); ++j) {
                int val = bufferedImage.getRGB(j, i);
                binaryImage.setRGB(j, i, (Utils.getRed(val) + Utils.getGreen(val) + Utils.getBlue(val) >= 383) ?
                        Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }

        return binaryImage;
    }

    private static BufferedImage negativeProcessing(ImageContainer image) {
        BufferedImage bufferedImage = image.getAsBufferedImage();
        int[] pixels = Utils.getPixelsArray(bufferedImage);

        for (int i = 0; i < bufferedImage.getHeight(); ++i)
            for (int j = 0; j < bufferedImage.getWidth(); ++j)
                pixels[i * bufferedImage.getWidth() + j] = ~pixels[i * bufferedImage.getWidth() + j] & 0xFFFFFF;

        Utils.updateBufferedImagePixels(bufferedImage, pixels);

        return bufferedImage;
    }

    private static BufferedImage logarithmProcessing(ImageContainer image, List<Double> params) {
        BufferedImage bufferedImage = image.getAsBufferedImage();
        double c = params.get(0);

        BufferedImage resultImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] pixels = Utils.getPixelsArray(bufferedImage);

        for (int i = 0; i < bufferedImage.getHeight(); ++i)
            for (int j = 0; j < bufferedImage.getWidth(); ++j) {
                Color newColor = new Color(
                        (int) Math.min(255, Math.max(0, c * Math.log(1 + Utils.getRed(pixels[i * bufferedImage.getWidth() + j])))),
                        (int) Math.min(255, Math.max(0, c * Math.log(1 + Utils.getGreen(pixels[i * bufferedImage.getWidth() + j])))),
                        (int) Math.min(255, Math.max(0, c * Math.log(1 + Utils.getBlue(pixels[i * bufferedImage.getWidth() + j])))));
                resultImage.setRGB(j, i, (newColor).getRGB());
            }

        return resultImage;
    }

    private static BufferedImage powerProcessing(ImageContainer image, List<Double> params) {
        BufferedImage bufferedImage = image.getAsBufferedImage();
        double c = params.get(0);
        double y = params.get(1);

        BufferedImage resultImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] pixels = Utils.getPixelsArray(bufferedImage);

        for (int i = 0; i < bufferedImage.getHeight(); ++i) {
            for (int j = 0; j < bufferedImage.getWidth(); ++j) {
                Color newColor = new Color(
                        (int) Math.min(255, Math.max(0, c * Math.pow(Utils.getRed(pixels[i * bufferedImage.getWidth() + j]), y))),
                        (int) Math.min(255, Math.max(0, c * Math.pow(Utils.getGreen(pixels[i * bufferedImage.getWidth() + j]), y))),
                        (int) Math.min(255, Math.max(0, c * Math.pow(Utils.getBlue(pixels[i * bufferedImage.getWidth() + j]), y))));
                resultImage.setRGB(j, i, (newColor).getRGB());
            }
        }

        return resultImage;
    }
}

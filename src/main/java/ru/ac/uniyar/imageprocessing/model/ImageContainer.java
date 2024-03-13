package ru.ac.uniyar.imageprocessing.model;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
@Setter
public class ImageContainer {
    private byte[] value;
    private String name;
    private String type;

    public BufferedImage getAsBufferedImage() {
        try {
            return ImageIO.read(new ByteArrayInputStream(value));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateValueViaBufferedImage(BufferedImage bufferedImage) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            value = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCutType() {
        return type.replace("image/", "");
    }
}

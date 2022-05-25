package Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    private static final int MAX_IMAGE_ICON_SIZE = 100;
    private static final int MAX_IMAGE_RESULT_SIZE = 800;

    public static BufferedImage resizeImage(BufferedImage image, boolean isIcon) {

        int height = image.getHeight();
        int width = image.getWidth();
        int maxSize = (isIcon) ? MAX_IMAGE_ICON_SIZE : MAX_IMAGE_RESULT_SIZE;

        if (height > width) {
            width = maxSize * width / height;
            height = maxSize;
        } else {
            height = maxSize * height / width;
            width = maxSize;
        }

        Image resultingImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;

    }

}

import java.awt.image.BufferedImage;

public class PatternData {

    private final int INCH_GAUGE = 4;
    private final int STITCH_GAUGE = 18;
    private final int ROW_GAUGE = 9;

    private BufferedImage inputImage;
    private int colorCount = 3;
    private final double[] inchSize = new double[2];
    private final int[] crochetSize = new int[2];
    private final int[] imageSize = new int[2];

    public boolean isValid() {
        return inputImage != null && getStitchCount() > 0 && getRowCount() > 0;
    }

    public BufferedImage getInputImage() {
        return inputImage;
    }

    public int getColorCount() {
        return colorCount;
    }

    public int getStitchCount() {
        return crochetSize[0];
    }

    public int getRowCount() {
        return crochetSize[1];
    }

    public void setColorCount(int newColorCount) {
        colorCount = newColorCount;
    }

    public void setInputImage(BufferedImage newImage) {
        inputImage = newImage;
        imageSize[0] = newImage.getWidth();
        imageSize[1] = newImage.getHeight();
        inchSize[0] = -1;
        inchSize[1] = -1;
        crochetSize[0] = -1;
        crochetSize[1] = -1;
    }

    public String[] setStitches(String stitchText) {
        int stitches;
        try {
            stitches = Integer.parseInt(stitchText);
        } catch (NumberFormatException e) {
            System.out.println("Could not update size");
            System.out.println(e);
            return new String[]{"Error","","",""};
        }
        return updateSizeMeasurements(stitches);
    }

    public String[] setRows(String rowText) {
        int rows;
        try {
            rows = Integer.parseInt(rowText);
        } catch (NumberFormatException e) {
            System.out.println("Could not update size");
            System.out.println(e);
            return new String[]{"","Error","",""};
        }
        double imgWidthHeightRatio = 1.0 * imageSize[0] / imageSize[1];
        double stitchRowRatio = 1.0 * STITCH_GAUGE / ROW_GAUGE;
        return updateSizeMeasurements((int) Math.round(imgWidthHeightRatio * stitchRowRatio * rows));
    }

    public String[] setWidth(String widthText) {
        double width;
        try {
            width = Double.parseDouble(widthText);
        } catch (NumberFormatException e) {
            System.out.println("Could not update size");
            System.out.println(e);
            return new String[]{"","","Error",""};
        }
        double stitchInchRatio = 1.0 * STITCH_GAUGE / INCH_GAUGE;
        return updateSizeMeasurements((int) Math.round(stitchInchRatio * width));
    }

    public String[] setHeight(String heightText) {
        double height;
        try {
            height = Double.parseDouble(heightText);
        } catch (NumberFormatException e) {
            System.out.println("Could not update size");
            System.out.println(e);
            return new String[]{"","","","Error"};
        }
        double imgWidthHeightRatio = 1.0 * imageSize[0] / imageSize[1];
        double stitchRowRatio = 1.0 * STITCH_GAUGE / ROW_GAUGE;
        double rowPerInch = 1.0 * ROW_GAUGE / INCH_GAUGE;
        double rows = rowPerInch * height;
        return updateSizeMeasurements((int) Math.round(imgWidthHeightRatio * stitchRowRatio * rows));
    }

    private String[] updateSizeMeasurements(int stitch) {
        double imgHeightWidthRatio = 1.0 * imageSize[1] / imageSize[0];
        double rowStitchRatio = 1.0 * ROW_GAUGE / STITCH_GAUGE;
        double inchStitchRatio = 1.0 * INCH_GAUGE / STITCH_GAUGE;
        double inchRowRatio = 1.0 * INCH_GAUGE / ROW_GAUGE;
        crochetSize[0] = stitch;
        crochetSize[1] = (int) Math.round(imgHeightWidthRatio * rowStitchRatio * stitch);
        inchSize[0] = stitch * inchStitchRatio;
        inchSize[1] = crochetSize[1] * inchRowRatio;
        return new String[]{String.valueOf(crochetSize[0]), String.valueOf(crochetSize[1]), String.format("%.2f", inchSize[0]), String.format("%.2f", inchSize[1])};
    }

}

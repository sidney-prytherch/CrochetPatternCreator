import Utils.KMeans;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Pattern {

    private BufferedImage finalImage;
    private BufferedImage finalImageWithoutBorders;
    private BufferedImage inputImage;
    private BufferedImage simulatedProductImage;
    private int[][] patternColorArray;
    private Set<Integer> colorSet;
    private final String[] colorNames = new String[]{"ColorA", "ColorB", "ColorC", "ColorD", "ColorE", "ColorF", "ColorG", "ColorH"};

    private String patternInstructionsText;

    private void createPatternText() {
        StringBuilder patternString = new StringBuilder();
        HashMap<Integer, String> colorNameMap = new HashMap<>(colorSet.size());
        int currIndex = 0;
        for (Integer color : colorSet) {
            colorNameMap.put(color, colorNames[currIndex++]);
        }

        patternString.append(String.format("Foundation Row: chain %d\n", patternColorArray[0].length));
        for (int i = patternColorArray.length - 1; i >= 0; i--) {
            patternString.append(String.format("Row %d: ", patternColorArray.length - i));
            int currentColor = -1;
            int colorCount = 0;
            for (int j = patternColorArray[i].length - 1; j >= 0; j--) {
                if (patternColorArray[i][j] == currentColor) {
                    colorCount++;
                } else {
                    if (colorCount > 0) {
                        patternString.append(String.format("%d %s, ", colorCount, colorNameMap.getOrDefault(currentColor, String.valueOf(currentColor))));
                    }
                    currentColor = patternColorArray[i][j];
                    colorCount = 1;
                }
            }
            patternString.append(String.format("%d %s", colorCount, colorNameMap.getOrDefault(currentColor, String.valueOf(currentColor))));
            patternString.append("\n");
        }
        patternInstructionsText = patternString.toString();
    }

    private void createSimulatedProductImage() {
        int stitches = getStitches();
        int rows = getRows();

        simulatedProductImage = new BufferedImage(stitches * 9, rows * 18, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < patternColorArray.length; i++) {
            for (int j = 0; j < patternColorArray[i].length; j++) {

                for (int x = 0; x < 9; x++) {
                    for (int y = 0; y < 18; y++) {
                        simulatedProductImage.setRGB(j * 9 + x, i * 18 + y, patternColorArray[i][j]);
                    }
                }

                for (int x = 1; x < 8; x++) {
                    simulatedProductImage.setRGB(j * 9 + x, i * 18, 0);
                    simulatedProductImage.setRGB(j * 9 + x, i * 18 + 4, 0);
                }
                for (int x = 1; x < 4; x++) {
                    simulatedProductImage.setRGB(j * 9, i * 18 + x, 0);
                    simulatedProductImage.setRGB(j * 9 + 8, i * 18 + x, 0);
                    simulatedProductImage.setRGB(j * 9 + x, i * 18 + 17, 0);
                    simulatedProductImage.setRGB(j * 9 + x + 4, i * 18 + 17, 0);
                }
                for (int x = 5; x < 17; x++) {
                    simulatedProductImage.setRGB(j * 9, i * 18 + x, 0);
                    simulatedProductImage.setRGB(j * 9 + 4, i * 18 + x, 0);
                    simulatedProductImage.setRGB(j * 9 + 8, i * 18 + x, 0);
                }

            }
        }
    }

    private int getStitches() {
        return patternColorArray[0].length;
    }

    private int getRows() {
        return patternColorArray.length;
    }

    private void createFinalImage() {
        int stitches = getStitches();
        int rows = getRows();
        finalImage = new BufferedImage(stitches * 9, rows * 18, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < patternColorArray.length; i++) {
            for (int j = 0; j < patternColorArray[i].length; j++) {

                for (int x = 0; x < 9; x++) {
                    for (int y = 0; y < 18; y++) {
                        finalImage.setRGB(j * 9 + x, i * 18 + y, patternColorArray[i][j]);
                    }
                }
                for (int x = 0; x < 9; x++) {
                    finalImage.setRGB(j * 9 + x, i * 18, 0);
                    finalImage.setRGB(j * 9 + x, i * 18 + 17, 0);
                }
                for (int x = 0; x < 18; x++) {
                    finalImage.setRGB(j * 9, i * 18 + x, 0);
                    finalImage.setRGB(j * 9 + 8, i * 18 + x, 0);
                }
            }
        }
    }

    public void generateImages(PatternData patternData) { //(BufferedImage image, int stitches, int rows, int colorCount) {

        inputImage = patternData.getInputImage();
        int stitches = patternData.getStitchCount();
        int rows = patternData.getRowCount();
        int colorCount = patternData.getColorCount();

        KMeans kmeans = new KMeans();
        finalImageWithoutBorders = kmeans.calculate(inputImage, colorCount, 1);

        double pxPerStitch = 1.0 * finalImageWithoutBorders.getWidth() / stitches;
        double pxPerRow = 1.0 * finalImageWithoutBorders.getHeight() / rows;

        patternColorArray = new int[rows][stitches];
        colorSet = new HashSet<>(colorCount);

        HashMap<Integer, Integer> colorCounts = new HashMap<>(colorCount);

        for (int i = 0; i < rows; i++) {
            int firstRowPx = (int) (i * pxPerRow);
            int lastRowPx = (int) ((i + 1) * pxPerRow);
            for (int j = 0; j < stitches; j++) {
                int firstStitchPx = (int) (j * pxPerStitch);
                int lastStitchPx = (int) ((j + 1) * pxPerStitch);

                int selectedColor = -1;
                int highestColorCount = 0;

                colorCounts.clear();
                for (int pxi = firstRowPx; pxi < lastRowPx; pxi++) {
                    for (int pxj = firstStitchPx; pxj < lastStitchPx; pxj++) {
                        int color = finalImageWithoutBorders.getRGB(pxj, pxi);
                        if (colorCounts.containsKey(color)) {
                            colorCounts.put(color, colorCounts.get(color) + 1);
                        } else {
                            colorCounts.put(color, 1);
                        }
                        int currColorCount = colorCounts.get(color);
                        if (currColorCount > highestColorCount) {
                            highestColorCount = currColorCount;
                            selectedColor = color;
                        }
                    }
                }

                for (int pxi = firstRowPx; pxi < lastRowPx; pxi++) {
                    for (int pxj = firstStitchPx; pxj < lastStitchPx; pxj++) {
                        finalImageWithoutBorders.setRGB(pxj, pxi, selectedColor);
                        patternColorArray[i][j] = selectedColor;
                        colorSet.add(selectedColor);
                    }
                }
            }
        }

        createFinalImage();
        createSimulatedProductImage();
        createPatternText();

    }

    public BufferedImage getFinalImage() {
        return finalImage;
    }

    public BufferedImage getFinalImageWithoutBorders() {
        return finalImageWithoutBorders;
    }

    public BufferedImage getInputImage() {
        return inputImage;
    }

    public BufferedImage getSimulatedProductImage() {
        return simulatedProductImage;
    }

    public String getPatternInstructionsText() {
        return patternInstructionsText;
    }

}

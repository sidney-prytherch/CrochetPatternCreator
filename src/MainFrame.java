import Utils.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainFrame extends JFrame {

    private Pattern pattern;
    private final PatternData patternData;
    private String path = "";
    private JButton uploadButton;
    private JLabel fileName;
    private JLabel fileImage;
    private JPanel mainFramePanel;
    private JTextField widthTextField;
    private JTextField heightTextField;
    private JTextField stitchTextField;
    private JTextField rowTextField;
    private JButton updateWidthButton;
    private JButton updateHeightButton;
    private JButton updateStitchesButton;
    private JButton updateRowsButton;
    private JLabel pixelHeightLabel;
    private JLabel pixelWidthLabel;
    private JLabel outputImage;
    private JButton continueButton;
    private JSpinner colorSpinner;
    private JButton downloadButton;

    public MainFrame() {
        setContentPane(mainFramePanel);
        setTitle("Crochet Pattern Creator");
        setSize(1000, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        patternData = new PatternData();
        uploadButton.addActionListener(e -> selectFile());
        updateWidthButton.addActionListener(e -> updateText(patternData.setWidth(widthTextField.getText())));
        updateHeightButton.addActionListener(e -> updateText(patternData.setHeight(heightTextField.getText())));
        updateStitchesButton.addActionListener(e -> updateText(patternData.setStitches(stitchTextField.getText())));
        updateRowsButton.addActionListener(e -> updateText(patternData.setRows(rowTextField.getText())));
        continueButton.addActionListener(e -> generateImage());
        downloadButton.addActionListener(e -> downloadPattern());
        SpinnerModel spinnerModel = new SpinnerNumberModel(3, 2, 8, 1);
        colorSpinner.setModel(spinnerModel);
        colorSpinner.addChangeListener(e -> patternData.setColorCount(Integer.parseInt(((JSpinner) e.getSource()).getValue().toString())));
    }


    public void updateText(String[] stringSizes) {
        stitchTextField.setText(stringSizes[0]);
        rowTextField.setText(stringSizes[1]);
        widthTextField.setText(stringSizes[2]);
        heightTextField.setText(stringSizes[3]);
    }

    public void downloadPattern() {
        String inputImageFilePath = path + "/crochetPattern-original.jpg";
        String pixelResultFilePath = path + "/crochetPattern-image.jpg";
        String pixelResultBorderlessFilePath = path + "/crochetPattern-borderless-image.jpg";
        String mimicResultFilePath = path + "/crochetPattern-mimicResult.jpg";
        String textPatternFilePath = path + "/crochetPattern-text.txt";
        try {
            Files.deleteIfExists(Path.of(inputImageFilePath));
            Files.deleteIfExists(Path.of(pixelResultFilePath));
            Files.deleteIfExists(Path.of(pixelResultBorderlessFilePath));
            Files.deleteIfExists(Path.of(mimicResultFilePath));
            Files.deleteIfExists(Path.of(textPatternFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File inputImageFile = new File(inputImageFilePath);
        File pixelResultFile = new File(pixelResultFilePath);
        File pixelResultBorderlessFile = new File(pixelResultBorderlessFilePath);
        File mimicResultFile = new File(mimicResultFilePath);
        try {
            ImageIO.write(pattern.getInputImage(), "jpg", inputImageFile);
            ImageIO.write(pattern.getFinalImage(), "jpg", pixelResultFile);
            ImageIO.write(pattern.getFinalImageWithoutBorders(), "jpg", pixelResultBorderlessFile);
            ImageIO.write(pattern.getSimulatedProductImage(), "jpg", mimicResultFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (PrintWriter out = new PrintWriter(textPatternFilePath)) {
            out.println(pattern.getPatternInstructionsText());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateImage() {
        if (patternData.isValid()) {
            pattern = new Pattern();
            pattern.generateImages(patternData);

            ImageIcon icon = new ImageIcon(ImageUtils.resizeImage(pattern.getSimulatedProductImage(), false));

            outputImage.setIcon(icon);
        }
    }

    public void selectFile() {
        JFileChooser chooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif", "bmp");
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();

            fileName.setText(f.getName());
            String imagePath = f.getPath();
            path = imagePath.substring(0, imagePath.lastIndexOf("/"));
            BufferedImage inputImage;

            try {
                inputImage = ImageIO.read(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (inputImage != null) {
                patternData.setInputImage(inputImage);
                updateText(new String[]{"", "", "", ""});
                outputImage.setIcon(null);
                pixelWidthLabel.setText(String.valueOf(inputImage.getWidth()));
                pixelHeightLabel.setText(String.valueOf(inputImage.getHeight()));
                Image iconImage = ImageUtils.resizeImage(inputImage, true);
                ImageIcon icon = new ImageIcon(iconImage);
                fileImage.setIcon(icon);
            }
        }
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}

package com.curtisnewbie.app;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;

/**
 * Program for generatign QR code based on the given text.
 *
 */
public class App {

    public static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {

        // get console input
        System.out.println("\n------------------------"
                + "\n\nThis is the QRCodeGenerator. Please enter the text that you want to encode:"
                + "\n\n------------------------");
        String text = in.nextLine();

        System.out.println("\nPlease specify the width of the QR code image [whole number/int]");
        int width = in.nextInt();

        System.out.println("\nPlease specify the height of the QR code image [whole number/int]");
        int height = in.nextInt();

        if (width <= 0 || height <= 0) {
            System.out.println("Width and height must be greater than 0");
            System.exit(0);
        }

        System.out.println("\nPlease specify the output path of the image");
        in.nextLine();
        String outputPath = in.nextLine();

        System.out.println("GeneratingQRIMage:...");
        try {
            // generate QR code image
            generateQRImage(text, width, height, outputPath);
            System.out.println("Done!");

            // open the created images
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("window")) {
                System.out.println("Attempting to open the file");
                openFile(outputPath);
                System.out.println("The file is located at: " + "\"" + outputPath + "\"");
            }

        } catch (WriterException e) {
            System.out.println("Failed to generate QR image, existing...");
            System.out.println("\n" + e.toString());
            System.exit(0);
        } catch (IOException e) {
            System.out.println("\n" + e.toString());
            System.exit(0);
        }

    }

    /**
     * 
     * @param t text
     * @param w width
     * @param h height
     * @param p outputPath
     */
    public static void generateQRImage(String t, int w, int h, String p) throws WriterException, IOException {

        // writer
        QRCodeWriter writer = new QRCodeWriter();

        // encode into a bit matrix
        BitMatrix bitMatrix = writer.encode(t, BarcodeFormat.QR_CODE, w, h);

        // access to the FileSystem and get the path
        Path path = FileSystems.getDefault().getPath(p);

        // writer th image using image writer
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * Open the created file. This function only supports windows os, beacuse I use
     * Windows (unfortunately).
     * 
     * @param p file path as a string.
     */
    public static void openFile(String p) throws IOException {

        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd.exe /c \"" + p + "\"");
    }
}

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
 */
public class App {

    // arguments
    public static final String MSG_ARG = "-m";
    public static final String WIDTH_ARG = "-w";
    public static final String HEIGHT_ARG = "-h";
    public static final String OUTPUT_ARG = "-o";
    public static final String WIFI_ARG = "-wifi";
    public static final String WIFI_PWD_ARG = "-p";
    public static final String WIFI_NAME_ARG = "-n";
    private static final int DEF_WIDTH = 300;
    private static final int DEF_HEIGHT = 300;

    public static void main(String[] args) {
        var map = parseArgs(args);
        if (map.isEmpty()) {
            printArgsList();
            System.exit(0);
        }
        if (!validateParam(map)) {
            printArgsList();
            System.exit(1);
        }

        try {
            int width = map.containsKey(ParamType.WIDTH) ? Integer.parseInt(map.get(ParamType.WIDTH)) : DEF_WIDTH;
            int height = map.containsKey(ParamType.HEIGHT) ? Integer.parseInt(map.get(ParamType.HEIGHT)) : DEF_HEIGHT;
            if (width <= 0 || height <= 0) {
                System.err.println("Width and height must be greater than 0");
                System.exit(1);
            }

            String msg;
            if (!map.containsKey(ParamType.WIFI_ENCODING_FLAG)) {
                msg = map.get(ParamType.MSG);
            } else {
                msg = String.format("WIFI:T:WPA;S:%s;P:%s;;", map.get(ParamType.WIFI_NAME), map.get(ParamType.WIFI_PASSWORD));
            }
            String outputPath = map.get(ParamType.OUTPUT_PATH);

            System.out.println("Generating QR Image ...");
            // generate QR code image
            generateQRImage(msg, width, height, outputPath);
            System.out.println("Done!");

            // open the created images
            String os = System.getProperty("os.name").toLowerCase();
            openFile(outputPath, os);
            System.out.println("The file is located at: " + "\"" + outputPath + "\"");
        } catch (WriterException e) {
            System.err.println("Failed to generate QR image, existing...");
            System.err.println("\n" + e.toString());
            System.exit(0);
        } catch (IOException e) {
            System.err.println("\n" + e.toString());
            System.exit(0);
        } catch (NumberFormatException e) {
            System.err.println("Width and Height must be Integer");
        }
    }

    /**
     * Generate QRcode Image
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

        // write image using image writer
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * Open the created QRcode image.
     *
     * @param p file path as a string.
     */
    public static void openFile(String p, String os) throws IOException {
        System.out.println("Attempting to open the file");
        Runtime runtime = Runtime.getRuntime();
        if (os.contains("window")) {
            runtime.exec(new String[]{"cmd.exe", "/c", "'" + p + "'"});
        } else if (os.contains("linux")) {
            runtime.exec(new String[]{"bash", "-c", "xdg-open '" + p + "'"});
        }
    }

    /**
     * Parse the arguments, and load them to the map
     *
     * @param args
     * @return {@code a Map<Parameters, String>} of parsed arguments
     */
    public static Map<ParamType, String> parseArgs(String args[]) {
        Map<ParamType, String> argMap = new EnumMap<>(ParamType.class);
        for (int i = 0; i < args.length; i++) {
            if (i < args.length - 1) {
                if (args[i].equals(MSG_ARG) && !argMap.containsKey(ParamType.MSG)) // msg
                    argMap.put(ParamType.MSG, args[++i]);
                else if (args[i].equals(WIDTH_ARG) && !argMap.containsKey(ParamType.WIDTH)) // width
                    argMap.put(ParamType.WIDTH, args[++i]);
                else if (args[i].equals(HEIGHT_ARG) && !argMap.containsKey(ParamType.HEIGHT)) // height
                    argMap.put(ParamType.HEIGHT, args[++i]);
                else if (args[i].equals(OUTPUT_ARG) && !argMap.containsKey(ParamType.OUTPUT_PATH)) // output path
                    argMap.put(ParamType.OUTPUT_PATH, args[++i]);
                else if (args[i].equals(WIFI_ARG) && !argMap.containsKey(ParamType.WIFI_ENCODING_FLAG)) // enable wifi credential encoding
                    argMap.put(ParamType.WIFI_ENCODING_FLAG, "");
                else if (args[i].equals(WIFI_NAME_ARG) && !argMap.containsKey(ParamType.WIFI_NAME)) // output path
                    argMap.put(ParamType.WIFI_NAME, args[++i]);
                else if (args[i].equals(WIFI_PWD_ARG) && !argMap.containsKey(ParamType.WIFI_PASSWORD)) // output path
                    argMap.put(ParamType.WIFI_PASSWORD, args[++i]);
            }
        }
        return argMap;
    }

    /**
     * Print a list of arguments for this program
     */
    public static void printArgsList() {
        System.err.println(String.format(
                "\n  Arguments List:\n" +
                        "\t%s message to be encoded\n" +
                        "\t%s width of the QRcode (Integer)\n" +
                        "\t%s height of the QRcode (Integer)\n" +
                        "\t%s absolute/relative path of the output QRcode image\n" +
                        "\t%s encode WIFI name and password\n" +
                        "\t%s (if WIFI encoding is enabled) WIFI's name\n" +
                        "\t%s (if WIFI encoding is enabled) WIFI's password\n" +
                        "\n  E.g.,\n   -m 'I am the best!' -w 300 -h 300 -o best.png\u001B[0m",
                MSG_ARG, WIDTH_ARG, HEIGHT_ARG, OUTPUT_ARG, WIFI_ARG, WIFI_NAME_ARG, WIFI_PWD_ARG));
    }

    /**
     * Print error due to the incorrect number of arguments
     */
    public static void printError(String msg) {
        System.err.format("\n\u001B[1m\u001B[31m %s \u001B[39m\n", msg);
    }

    enum ParamType {
        MSG, WIDTH, HEIGHT, OUTPUT_PATH, WIFI_ENCODING_FLAG, WIFI_NAME, WIFI_PASSWORD
    }

    private static boolean validateParam(Map<ParamType, String> map) {
        if (!map.containsKey(ParamType.OUTPUT_PATH)) {
            printError("Output path should be provided");
            return false;
        }
        if (map.containsKey(ParamType.WIFI_ENCODING_FLAG)) {
            if (!map.containsKey(ParamType.WIFI_NAME)) {
                printError("WIFI name should be provided");
                return false;
            }
            if (!map.containsKey(ParamType.WIFI_PASSWORD)) {
                printError("WIFI password should be provided");
                return false;
            }
        } else {
            if (!map.containsKey(ParamType.MSG)) {
                printError("Message should be provided");
                return false;
            }
        }
        return true;
    }
}

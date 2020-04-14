# QRCodeGenTool

QRCodeGenTool powered by **zxing**

A simple program that encodes the given text into a QR Code image and output that image to a speicified file path.

## Prerequisite

- Java 11

## How To Use It?

This program takes four arguments:

    Arguments List:
        -m message to be encoded
        -w width of the QRcode (Integer)
        -h height of the QRcode (Integer)
        -o absolute/relative path of the output QRcode image

To use it, you will need to download the Jar file in **RELEASE** and run it as follows:

    java -jar QRCodeGenTool-1.0-SNAPSHOT.jar -m [your_msg] -w [300] -h [300] -o [path/to/the/QRimg.png]

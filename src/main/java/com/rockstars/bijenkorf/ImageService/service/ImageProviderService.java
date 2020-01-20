package com.rockstars.bijenkorf.ImageService.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

@Service
public class ImageProviderService {
    static final String AWS_FOLDER = "aws_images/";
    static final String SOURCE_FOLDER = "image/";
    static final String ORIGINAL_TYPENAME = "original";

    public byte[] loadOptimizedImage(String typeName, String fileName) throws IOException {
        var file = new File(AWS_FOLDER + typeName + "/" + fileName);
        return Files.readAllBytes(file.toPath());
    }

    public BufferedImage loadOriginalImage(String fileName) throws IOException {
        try {
            return loadImageFromFile(ORIGINAL_TYPENAME, fileName);
        } catch( IOException e) {
            System.out.println("Original image not found on server, getting from source.");
        }

        ClassPathResource sourceImage = loadFileFromSource(fileName);
        saveFile(sourceImage.getInputStream(), ORIGINAL_TYPENAME, fileName);
        return loadImageFromFile(ORIGINAL_TYPENAME, fileName);
    }

    public void saveOptimizedImage(ByteArrayOutputStream optimizedImageStream, String typeName, String fileName, String formatName) throws IOException {
        File folder = new File(AWS_FOLDER + typeName + "/");
        folder.mkdirs();

        File file = new File(AWS_FOLDER + typeName + "/" + fileName);
        file.createNewFile();

        OutputStream fileOutputStream = new FileOutputStream(file);
        optimizedImageStream.writeTo(fileOutputStream);
    }

    private ClassPathResource loadFileFromSource(String fileName) throws IOException {
        return new ClassPathResource(SOURCE_FOLDER + fileName);
    }

    private BufferedImage loadImageFromFile(String folder, String fileName) throws IOException {
        var file = new File(AWS_FOLDER + folder + "/" + fileName);
        return ImageIO.read(file);
    }

    private void saveFile(InputStream inputStream, String destinationFolder, String fileName) throws IOException {
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        File targetFolder = new File(AWS_FOLDER + destinationFolder);
        targetFolder.mkdirs();

        File targetFile = new File(AWS_FOLDER + destinationFolder + "/" + fileName);
        targetFile.createNewFile();

        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.flush();
    }
}

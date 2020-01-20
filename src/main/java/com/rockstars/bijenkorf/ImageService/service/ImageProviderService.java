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

    /**
     * Load the optimized image from disk. Throws an IOException if the image does not exist.
     * @param typeName - The name of the predefined resize type requested
     * @param fileName - The unique name of the file
     * @return - A byte array of the resized image
     * @throws IOException - Gets thrown if the image does not exist.
     */
    public byte[] loadOptimizedImage(String typeName, String fileName) throws IOException {
        var file = new File(getFolderForTypeName(typeName, fileName) + fileName);
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Get the original image from disk, throws IOException if the image does not exist.
     * @param fileName - The unique filename of the image.
     * @return - A buffered image
     * @throws IOException - Gets thrown if the image does not exist.
     */
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

    /**
     * Save the optimized image to disk. Throws IOException if something goes wrong.
     * @param optimizedImageStream - The outputstream of the image
     * @param typeName - The name of the predefined resize type, used to determine the save location
     * @param fileName - The unique name of the file
     * @throws IOException - Gets thrown when an error occurs during saving
     */
    public void saveOptimizedImage(ByteArrayOutputStream optimizedImageStream, String typeName, String fileName) throws IOException {
        String folder = getFolderForTypeName(typeName, fileName);
        File folderFile = new File(folder);
        folderFile.mkdirs();

        File file = new File(folder + fileName);
        file.createNewFile();

        OutputStream fileOutputStream = new FileOutputStream(file);
        optimizedImageStream.writeTo(fileOutputStream);
    }

    private ClassPathResource loadFileFromSource(String fileName) throws IOException {
        return new ClassPathResource(SOURCE_FOLDER + fileName);
    }

    private BufferedImage loadImageFromFile(String typeName, String fileName) throws IOException {
        String folder = getFolderForTypeName(typeName, fileName);
        var file = new File(folder + fileName);
        return ImageIO.read(file);
    }

    private void saveFile(InputStream inputStream, String typeName, String fileName) throws IOException {
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        String targetFolder = getFolderForTypeName(typeName, fileName);
        File targetFolderFile = new File(targetFolder);
        targetFolderFile.mkdirs();

        File targetFile = new File(targetFolder + fileName);
        targetFile.createNewFile();

        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.flush();
    }

    private String getFolderForTypeName(String typeName, String fileName) {
        String[] split = fileName.split("\\.");
        String base;
        if(split.length > 1) {
            String extension = split[split.length - 1];
            base = fileName.substring(0, fileName.length() - extension.length() - 1);
        } else {
            base = fileName;
        }
        String folder = AWS_FOLDER + typeName + "/";
        if(base.length() > 4) {
            folder += fileName.substring(0, 4) + "/";
            if(base.length() > 8) {
                folder += fileName.substring(4, 8) + "/";
            }
        }
        return folder;
    }
}

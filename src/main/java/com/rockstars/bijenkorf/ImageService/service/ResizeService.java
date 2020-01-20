package com.rockstars.bijenkorf.ImageService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockstars.bijenkorf.ImageService.model.ImageType;
import com.rockstars.bijenkorf.ImageService.model.PredefinedImageType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ResizeService {

    public ByteArrayOutputStream getResizedImageBytes(BufferedImage sourceImage, PredefinedImageType predefinedImageType) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage resizedImage = resizeImage(sourceImage, predefinedImageType);
        if (predefinedImageType.getType() == ImageType.JPG) {
            compressJpeg(resizedImage, outputStream, predefinedImageType.getQuality());
        } else {
            ImageIO.write( resizedImage, predefinedImageType.getType().getType(), outputStream );
        }
        outputStream.flush();
        return outputStream;
    }

    private void compressJpeg(BufferedImage sourceImage, ByteArrayOutputStream outputStream, int quality) throws IOException {
        ImageWriter writer  = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality / 100f);

        writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        writer.write(null, new IIOImage(sourceImage, null, null), param);
        writer.dispose();
    }

    private BufferedImage resizeImage(BufferedImage sourceImage, PredefinedImageType predefinedImageType) {
        BufferedImage resultImage = new BufferedImage(predefinedImageType.getWidth(), predefinedImageType.getHeight(), sourceImage.getType());

        // scales the source image to the result image
        Graphics2D g2d = resultImage.createGraphics();
        switch (predefinedImageType.getScaleType()) {
            case CROP:
                drawCroppedImage(g2d,sourceImage, predefinedImageType.getWidth(), predefinedImageType.getHeight());
                break;
            case FILL:
                drawFilledImage(g2d, sourceImage, predefinedImageType.getWidth(), predefinedImageType.getHeight(), predefinedImageType.getFillColor());
                break;
            default:
                drawSkewedImage(g2d, sourceImage, predefinedImageType.getWidth(), predefinedImageType.getHeight());
                break;
        }
        g2d.dispose();

        return resultImage;
    }

    private void drawSkewedImage(Graphics2D g2d, BufferedImage sourceImage, int width, int height) {
        g2d.drawImage(sourceImage, 0, 0, width, height, null);
    }

    private void drawCroppedImage(Graphics2D g2d, BufferedImage sourceImage, int width, int height) {

        float widthRatio = (float)sourceImage.getWidth() / width;
        float heightRatio =(float)sourceImage.getHeight() / height;
        float targetAspectRatio = (float)width / height;

        int sourceRectangleWidth;
        int sourceRectangleHeight;
        int startX;
        int startY;
        if(widthRatio > heightRatio){ //shrink to fixed height
            sourceRectangleWidth = Math.round(sourceImage.getHeight() * targetAspectRatio);
            sourceRectangleHeight = sourceImage.getHeight();
            startX = sourceImage.getWidth() / 2 - sourceRectangleWidth / 2;
            startY = 0;
        } else { //shrink to fixed width
            sourceRectangleWidth = sourceImage.getWidth();
            sourceRectangleHeight = Math.round(sourceImage.getWidth() / targetAspectRatio);
            startY = sourceImage.getHeight() / 2 - sourceRectangleHeight / 2;
            startX = 0;
        }

        g2d.drawImage(sourceImage, 0,0,width,height,startX,startY,startX + sourceRectangleWidth, startY + sourceRectangleHeight, null);
    }

    private void drawFilledImage(Graphics2D g2d, BufferedImage sourceImage, int width, int height, int color) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();
        int destinationRectangleWidth = sourceWidth;
        int destinationRectangleHeight = sourceHeight;

        // first check if we need to scale width
        if (sourceWidth > width) {
            //scale width to fit
            destinationRectangleWidth = width;
            //scale height to maintain aspect ratio
            destinationRectangleHeight = (destinationRectangleWidth * sourceHeight) / sourceWidth;
        }

        // then check if we need to scale even with the new height
        if (destinationRectangleHeight > height) {
            //scale height to fit instead
            destinationRectangleHeight = height;
            //scale width to maintain aspect ratio
            destinationRectangleWidth = (destinationRectangleHeight * sourceWidth) / sourceHeight;
        }

        int startX = width / 2 - destinationRectangleWidth / 2;
        int startY = height / 2 - destinationRectangleHeight / 2;

        g2d.setColor(new Color(color));
        g2d.fillRect(0,0, width, height);
        g2d.drawImage(sourceImage,startX,startY,startX + destinationRectangleWidth,startY + destinationRectangleHeight, 0,0,sourceWidth, sourceHeight, null);

    }

    public PredefinedImageType getImageTypeByTypeName(String typeName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        var resource = new ClassPathResource("imagetype/" + typeName + ".json");
        return objectMapper.readValue(resource.getInputStream(), PredefinedImageType.class);
    }
}

package com.rockstars.bijenkorf.ImageService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockstars.bijenkorf.ImageService.model.PredefinedImageType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ResizeService {

    public byte[] getResizedImageBytes(BufferedImage sourceImage, String typeName) throws IOException {
        PredefinedImageType predefinedImageType;
        predefinedImageType = getImageTypeByTypeName(typeName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage resizedImage = resizeImage(sourceImage, predefinedImageType);
        ImageIO.write( resizedImage, predefinedImageType.getType().getType(), outputStream );
        outputStream.flush();
        return outputStream.toByteArray();
    }

    private BufferedImage resizeImage(BufferedImage sourceImage, PredefinedImageType predefinedImageType) {
        BufferedImage resultImage = new BufferedImage(predefinedImageType.getWidth(), predefinedImageType.getHeight(), sourceImage.getType());

        // scales the source image to the result image
        Graphics2D g2d = resultImage.createGraphics();
        g2d.drawImage(sourceImage, 0, 0, predefinedImageType.getWidth(), predefinedImageType.getHeight(), null);
        g2d.dispose();

        return resultImage;
    }

    private PredefinedImageType getImageTypeByTypeName(String typeName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        var resource = new ClassPathResource("imagetype/" + typeName + ".json");
        return objectMapper.readValue(resource.getInputStream(), PredefinedImageType.class);
    }
}

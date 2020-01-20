package com.rockstars.bijenkorf.ImageService.controller;

import com.rockstars.bijenkorf.ImageService.model.PredefinedImageType;
import com.rockstars.bijenkorf.ImageService.service.ImageProviderService;
import com.rockstars.bijenkorf.ImageService.service.ResizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController("/image")
public class ResizeController {

    @Autowired
    ResizeService resizeService;

    @Autowired
    ImageProviderService imageProviderService;

    @GetMapping(value = {"/show/{typeName}/{seoName}", "/show/{typeName}"})
    public ResponseEntity<byte[]> getImage(@PathVariable() String typeName, @PathVariable(required = false) String seoName, @RequestParam() String reference) {
        PredefinedImageType predefinedImageType;
        try {
            predefinedImageType = resizeService.getImageTypeByTypeName(typeName);
        } catch(IOException e) {
            System.out.println("typeName does not exist");
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageArray = imageProviderService.loadOptimizedImage(typeName, reference);
            return ResponseEntity.ok()
                    .contentType(predefinedImageType.getMediaType())
                    .body(imageArray);
        } catch( IOException e) {
            System.out.println("Optimized image not found, trying original image");
        }

        BufferedImage image;
        try {
            image = imageProviderService.loadOriginalImage(reference);
        } catch(IOException e) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayOutputStream resizedImageBytes = resizeService.getResizedImageBytes(image, predefinedImageType);
            imageProviderService.saveOptimizedImage(resizedImageBytes, typeName, reference, predefinedImageType.getType().getType());

            return ResponseEntity.ok()
                    .contentType(predefinedImageType.getMediaType())
                    .body(resizedImageBytes.toByteArray());

        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }

    }
}

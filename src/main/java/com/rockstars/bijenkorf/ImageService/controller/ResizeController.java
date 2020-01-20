package com.rockstars.bijenkorf.ImageService.controller;

import com.rockstars.bijenkorf.ImageService.model.PredefinedImageType;
import com.rockstars.bijenkorf.ImageService.service.ImageProviderService;
import com.rockstars.bijenkorf.ImageService.service.ResizeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController("/image")
public class ResizeController {

    final
    ResizeService resizeService;

    final
    ImageProviderService imageProviderService;

    public ResizeController(ResizeService resizeService, ImageProviderService imageProviderService) {
        this.resizeService = resizeService;
        this.imageProviderService = imageProviderService;
    }

    /**
     * Endpoint to get the requested resized image.
     * @param typeName - Name of the predefined resize type
     * @param seoName - Non-used parameter for search engines, optional
     * @param reference - Unique filename of the image
     * @return - The resized image
     */
    @GetMapping(value = {"/show/{typeName}/{seoName}", "/show/{typeName}"})
    public ResponseEntity<byte[]> getImage(@PathVariable() String typeName, @PathVariable(required = false) String seoName, @RequestParam() String reference) {

        String safeFileName = getSafeFileName(reference);

        PredefinedImageType predefinedImageType;
        try {
            predefinedImageType = resizeService.getImageTypeByTypeName(typeName);
        } catch(IOException e) {
            System.out.println("typeName does not exist");
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageArray = imageProviderService.loadOptimizedImage(typeName, safeFileName);
            return ResponseEntity.ok()
                    .contentType(predefinedImageType.getMediaType())
                    .body(imageArray);
        } catch( IOException e) {
            System.out.println("Optimized image not found, trying original image");
        }

        BufferedImage image;
        try {
            image = imageProviderService.loadOriginalImage(safeFileName);
        } catch(IOException e) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayOutputStream resizedImageBytes = resizeService.getResizedImageBytes(image, predefinedImageType);
            imageProviderService.saveOptimizedImage(resizedImageBytes, typeName, safeFileName);

            return ResponseEntity.ok()
                    .contentType(predefinedImageType.getMediaType())
                    .body(resizedImageBytes.toByteArray());

        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    private String getSafeFileName(String reference) {
        return reference.replace('/', '_');
    }
}

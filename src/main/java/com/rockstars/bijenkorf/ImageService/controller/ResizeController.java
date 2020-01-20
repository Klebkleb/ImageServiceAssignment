package com.rockstars.bijenkorf.ImageService.controller;

import com.rockstars.bijenkorf.ImageService.model.PredefinedImageType;
import com.rockstars.bijenkorf.ImageService.service.ImageProviderService;
import com.rockstars.bijenkorf.ImageService.service.ResizeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController()
@RequestMapping("/image")
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

        // Convert '/' to '_'
        String safeFileName = getSafeFileName(reference);

        // Get predefined type from json file
        PredefinedImageType predefinedImageType;
        try {
            predefinedImageType = resizeService.getImageTypeByTypeName(typeName);
        } catch(IOException e) {
            System.out.println("typeName does not exist.");
            return ResponseEntity.notFound().build();
        }

        // Try to load the optimized image from disk.
        try {
            byte[] imageArray = imageProviderService.loadOptimizedImage(typeName, safeFileName);
            System.out.println("Optimized image found.");
            return ResponseEntity.ok()
                    .contentType(predefinedImageType.getMediaType())
                    .body(imageArray);
        } catch( IOException e) {
            System.out.println("Optimized image not found, trying original image.");
        }

        // Try to load the original image (first from disk then from source).
        BufferedImage image;
        try {
            image = imageProviderService.loadOriginalImage(safeFileName);
            System.out.println("Original image loaded correctly.");
        } catch(IOException e) {
            System.out.println("Could not load original image.");
            return ResponseEntity.notFound().build();
        }

        // Resize image and save the result to disk.
        try {
            ByteArrayOutputStream resizedImageBytes = resizeService.getResizedImageBytes(image, predefinedImageType);
            System.out.println("Resized image correctly.");
            imageProviderService.saveOptimizedImage(resizedImageBytes, typeName, safeFileName);
            System.out.println("Saved optimized image correctly.");
            return ResponseEntity.ok()
                    .contentType(predefinedImageType.getMediaType())
                    .body(resizedImageBytes.toByteArray());

        } catch(Exception e) {
            System.out.println("Could not resize or save image.");
            return ResponseEntity.notFound().build();
        }

    }

    private String getSafeFileName(String reference) {
        return reference.replace('/', '_');
    }
}

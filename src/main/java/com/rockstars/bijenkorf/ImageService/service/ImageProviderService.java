package com.rockstars.bijenkorf.ImageService.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class ImageProviderService {

    public BufferedImage loadOriginalImage(String fileName) throws IOException {
        var imgResource = new ClassPathResource("image/original/" + fileName);
        return ImageIO.read(imgResource.getInputStream());
    }
}

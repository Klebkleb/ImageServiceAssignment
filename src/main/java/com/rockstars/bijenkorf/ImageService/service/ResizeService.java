package com.rockstars.bijenkorf.ImageService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockstars.bijenkorf.ImageService.model.PredefinedImageType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ResizeService {

    public String test(String name) {
        return "Hello " + name;
    }

    public PredefinedImageType getImageType(String name) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        var resource = new ClassPathResource("imagetype/" + name + ".json");
        return objectMapper.readValue(resource.getInputStream(), PredefinedImageType.class);
    }
}

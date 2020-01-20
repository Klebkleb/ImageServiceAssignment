package com.rockstars.bijenkorf.ImageService.controller;

import com.rockstars.bijenkorf.ImageService.service.ResizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResizeController {

    @Autowired
    ResizeService resizeService;

    @GetMapping("/test/{name}")
    public String getTest(@PathVariable() String name) {
        return resizeService.test(name);
    }
}

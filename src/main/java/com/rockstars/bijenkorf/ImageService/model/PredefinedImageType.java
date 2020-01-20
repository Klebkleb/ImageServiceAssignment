package com.rockstars.bijenkorf.ImageService.model;

import org.springframework.http.MediaType;

public class PredefinedImageType {
    private int height;
    private int width;
    private int quality;
    private ScaleType scaleType;
    private String fillColor;
    private ImageType type;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public MediaType getMediaType() {
        switch(type) {
            case PNG:
                return MediaType.IMAGE_PNG;
            default:
                return MediaType.IMAGE_JPEG;
        }
    }
}

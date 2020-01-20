package com.rockstars.bijenkorf.ImageService.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ImageType {
    JPG("jpg"),
    PNG("png");

    private String type;

    ImageType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}


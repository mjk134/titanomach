package io.github.mjk134.titanomach.utils;

public class Skin {
    private String texture;
    private String signature;
    private String UUID;
    public Skin(String texture, String signature, String UUID) {
        this.texture = texture;
        this.signature = signature;
        this.UUID = UUID;
    }

    public String getTexture() {
        return texture;
    }

    public String getSignature() {
        return signature;
    }

    public String getUUID() {
        return UUID;
    }
}

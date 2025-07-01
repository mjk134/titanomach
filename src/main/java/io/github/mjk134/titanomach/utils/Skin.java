package io.github.mjk134.titanomach.utils;

public class Skin {
    private final String texture;
    private final String signature;
    private final String uuid;
    public Skin(String texture, String signature, String uuid) {
        this.texture = texture;
        this.signature = signature;
        this.uuid = uuid;
    }

    public String getTexture() {
        return texture;
    }

    public String getSignature() {
        return signature;
    }

    public String getUuid() {
        return uuid;
    }
}

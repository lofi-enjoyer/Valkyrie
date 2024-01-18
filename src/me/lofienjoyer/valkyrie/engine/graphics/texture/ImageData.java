package me.lofienjoyer.valkyrie.engine.graphics.texture;

import java.nio.ByteBuffer;

public class ImageData {

    private final int width, height;
    private final ByteBuffer data;

    public ImageData(int width, int height, ByteBuffer data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getData() {
        return data;
    }

}

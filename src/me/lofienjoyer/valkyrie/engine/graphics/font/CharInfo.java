package me.lofienjoyer.valkyrie.engine.graphics.font;

import org.joml.Vector2f;

public class CharInfo {

    private final int sourceX, sourceY;
    private final int width, height, descent;

    private final Vector2f[] textureCoords;

    public CharInfo(int sourceX, int sourceY, int width, int height, int descent) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.width = width;
        this.height = height;
        this.descent = descent;
        this.textureCoords = new Vector2f[4];
    }

    public void calculateTextureCoords(int fontWidth, int fontHeight) {
        float x0 = (float) sourceX / (float) fontWidth;
        float x1 = (float) (sourceX + width) / (float) fontWidth;
        float y0 = (float) (sourceY - height) / (float) fontHeight;
        float y1 = (float) (sourceY + descent) / (float) fontHeight;

        textureCoords[0] = new Vector2f(x0, y0);
        textureCoords[1] = new Vector2f(x0, y1);
        textureCoords[2] = new Vector2f(x1, y0);
        textureCoords[3] = new Vector2f(x1, y1);
    }

    public int getSourceX() {
        return sourceX;
    }

    public int getSourceY() {
        return sourceY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height + descent;
    }

    public int getDescent() {
        return descent;
    }

    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }

}

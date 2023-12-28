package me.lofienjoyer.valkyrie.engine.graphics.font;

import me.lofienjoyer.valkyrie.Valkyrie;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ValkyrieFont {

    private final String filePath;
    private final int fontSize;
    private int textureId;

    private int width, height, lineHeight;

    private final Map<Integer, CharInfo> characterMap;

    public ValkyrieFont(String filePath, int fontSize) {
        this.filePath = filePath;
        this.fontSize = fontSize;
        this.characterMap = new HashMap<>();
        generateBitmap();
    }

    private void generateBitmap() {
        var img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        var graphicsContext = img.createGraphics();
        Font font = new Font(filePath, Font.PLAIN, fontSize);
        graphicsContext.setFont(font);
        var fontMetrics = graphicsContext.getFontMetrics();

        int estimatedWidth = (int) Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
        width = 0;
        height = fontMetrics.getHeight();
        lineHeight = fontMetrics.getHeight();

        int x = 0;
        int y = (int) (fontMetrics.getHeight() * 1.4f);

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                var charInfo = new CharInfo(x, y, fontMetrics.charWidth(i), fontMetrics.getHeight());
                characterMap.put(i, charInfo);
                width = Math.max(x + fontMetrics.charWidth(i), width);

                x += charInfo.getWidth();
                if (x > estimatedWidth) {
                    x = 0;
                    y += fontMetrics.getHeight() * 1.4f;
                    height += fontMetrics.getHeight() * 1.4f;
                }
            }
        }
        height += fontMetrics.getHeight() * 1.4f;
        graphicsContext.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphicsContext = img.createGraphics();
        graphicsContext.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphicsContext.setFont(font);
        graphicsContext.setColor(Color.WHITE);

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                var charInfo = characterMap.get(i);
                charInfo.calculateTextureCoords(width, height);
                graphicsContext.drawString("" + (char)i, charInfo.getSourceX(), charInfo.getSourceY());
            }
        }

        this.textureId = Valkyrie.LOADER.loadTexture(img);
        graphicsContext.dispose();
    }

    public int getTextureId() {
        return textureId;
    }

    public CharInfo getGlyph(int character) {
        return characterMap.get(character);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}

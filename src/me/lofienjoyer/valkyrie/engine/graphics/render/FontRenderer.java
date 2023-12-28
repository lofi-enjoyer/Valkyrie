package me.lofienjoyer.valkyrie.engine.graphics.render;

import me.lofienjoyer.valkyrie.engine.graphics.font.ValkyrieFont;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BatchMesh;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class FontRenderer {

    private static final int BATCH_SIZE = 4096;

    private final ValkyrieFont font;
    private final BatchMesh batchMesh;

    public FontRenderer(ValkyrieFont font) {
        this.font = font;
        this.batchMesh = new BatchMesh(BATCH_SIZE);
    }

    public void render(String text) {
        List<Float> data = new ArrayList<>();
        float xOffset = 0f;
        float yOffset = 0f;
        var counter = 0;
        for (byte aByte : text.getBytes()) {
            var charInfo = font.getGlyph(aByte);
            if (aByte == '\n') {
                yOffset += 1 / 600f + charInfo.getHeight() / (float)font.getHeight();
                xOffset = 0;
                continue;
            }
            if (data.size() + 4 * 6 >= BATCH_SIZE / 4) {
                draw(data);
            }

            data.addAll(List.of(xOffset, 0f - charInfo.getHeight() / (float)font.getHeight() + yOffset, charInfo.getTextureCoords()[0].x, charInfo.getTextureCoords()[0].y));
            data.addAll(List.of(xOffset, 0f + yOffset, charInfo.getTextureCoords()[1].x, charInfo.getTextureCoords()[1].y));
            data.addAll(List.of(xOffset + charInfo.getWidth() / (float)font.getWidth(), 0f - charInfo.getHeight() / (float)font.getHeight() + yOffset, charInfo.getTextureCoords()[2].x, charInfo.getTextureCoords()[2].y));

            data.addAll(List.of(xOffset + charInfo.getWidth() / (float)font.getWidth(), 0f - charInfo.getHeight() / (float)font.getHeight() + yOffset, charInfo.getTextureCoords()[2].x, charInfo.getTextureCoords()[2].y));
            data.addAll(List.of(xOffset, 0f + yOffset, charInfo.getTextureCoords()[1].x, charInfo.getTextureCoords()[1].y));
            data.addAll(List.of(xOffset + charInfo.getWidth() / (float)font.getWidth(), 0f + yOffset, charInfo.getTextureCoords()[3].x, charInfo.getTextureCoords()[3].y));

            xOffset += charInfo.getWidth() / (float)font.getWidth();
            counter++;
        }
        draw(data);
    }

    private void draw(List<Float> data) {
        var dataArray = new float[data.size()];
        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i] = data.get(i);
        }
        data.clear();

        batchMesh.updateMesh(dataArray);

        glBindVertexArray(batchMesh.getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, dataArray.length);
    }

}

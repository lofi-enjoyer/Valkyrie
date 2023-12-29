package me.lofienjoyer.valkyrie.engine.graphics.render;

import me.lofienjoyer.valkyrie.engine.graphics.font.ValkyrieFont;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BatchMesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
import me.lofienjoyer.valkyrie.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class FontRenderer {

    private static final int BATCH_SIZE = 1024 * 32;

    private final ValkyrieFont font;
    private final BatchMesh batchMesh;
    private final Shader shader;

    public FontRenderer(ValkyrieFont font) {
        this.font = font;
        this.batchMesh = new BatchMesh(BATCH_SIZE);

        this.shader = ResourceLoader.loadShader("fontShader", "res/shaders/font/font_vertex.glsl", "res/shaders/font/font_fragment.glsl");
        var projMatrix = new Matrix4f();
        projMatrix.ortho(-8, 8, -8, 8, -50, 50);
        shader.bind();
        shader.loadMatrix("projMatrix", projMatrix);
        shader.loadMatrix("transformationMatrix", Maths.createTransformationMatrix(new Vector2f(0, 0), 32));
    }

    public void render(String text, int x, int y) {
        shader.bind();
        shader.loadMatrix("transformationMatrix", Maths.createTransformationMatrix(new Vector2f(x, y), 2048));
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glBindTexture(GL_TEXTURE_2D, font.getTextureId());

        List<Float> data = new ArrayList<>();
        float xOffset = 0f;
        float yOffset = 0f;
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
        }
        draw(data);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }

    private void draw(List<Float> data) {
        var dataArray = new float[data.size()];
        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i] = data.get(i);
        }
        data.clear();

        batchMesh.updateMesh(dataArray);

        glBindVertexArray(batchMesh.getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, dataArray.length / 4);
    }

    public void setupProjectionMatrix(int width, int height) {
        var projMatrix = new Matrix4f();
        projMatrix.ortho(0, width, height, 0, -50, 50);
        shader.bind();
        shader.loadMatrix("projMatrix", projMatrix);
    }

}

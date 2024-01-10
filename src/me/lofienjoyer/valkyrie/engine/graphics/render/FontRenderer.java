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

    private static BatchMesh batchMesh;
    private static Shader shader;

    private static boolean loaded;

    private FontRenderer() {
    }

    public static void init() {
        if (loaded)
            return;
        batchMesh = new BatchMesh(BATCH_SIZE);

        shader = ResourceLoader.loadShader("fontShader", "res/shaders/font/font_vertex.glsl", "res/shaders/font/font_fragment.glsl");

        loaded = true;
    }

    public static void render(String text, int x, int y, ValkyrieFont font) {
        shader.bind();
        shader.loadMatrix("transformationMatrix", Maths.createTransformationMatrix(new Vector2f(x, y), 1));
        Renderer.disableDepthTest();
        Renderer.disableCullFace();
        Renderer.enableBlend();
        glBindTexture(GL_TEXTURE_2D, font.getTextureId());

        List<Float> data = new ArrayList<>();
        float xOffset = 0f;
        float yOffset = 0f;
        for (byte aByte : text.getBytes()) {
            var charInfo = font.getGlyph(aByte);
            if (aByte == '\n') {
                yOffset += charInfo.getHeight() + 4;
                xOffset = 0;
                continue;
            }
            if (data.size() + 4 * 6 >= BATCH_SIZE / 4) {
                draw(data);
            }

            data.addAll(List.of(xOffset, 0f - charInfo.getHeight() + yOffset, charInfo.getTextureCoords()[0].x, charInfo.getTextureCoords()[0].y));
            data.addAll(List.of(xOffset, 0f + yOffset, charInfo.getTextureCoords()[1].x, charInfo.getTextureCoords()[1].y));
            data.addAll(List.of(xOffset + charInfo.getWidth(), 0f - charInfo.getHeight() + yOffset, charInfo.getTextureCoords()[2].x, charInfo.getTextureCoords()[2].y));

            data.addAll(List.of(xOffset + charInfo.getWidth(), 0f - charInfo.getHeight() + yOffset, charInfo.getTextureCoords()[2].x, charInfo.getTextureCoords()[2].y));
            data.addAll(List.of(xOffset, 0f + yOffset, charInfo.getTextureCoords()[1].x, charInfo.getTextureCoords()[1].y));
            data.addAll(List.of(xOffset + charInfo.getWidth(), 0f + yOffset, charInfo.getTextureCoords()[3].x, charInfo.getTextureCoords()[3].y));

            xOffset += charInfo.getWidth();
        }
        draw(data);

        Renderer.enableDepthTest();
        Renderer.enableCullFace();
        Renderer.disableBlend();
    }

    private static void draw(List<Float> data) {
        var dataArray = new float[data.size()];
        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i] = data.get(i);
        }
        data.clear();

        batchMesh.updateMesh(dataArray);

        glBindVertexArray(batchMesh.getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, dataArray.length / 4);
    }

    public static void setupProjectionMatrix(int width, int height) {
        var projMatrix = new Matrix4f();
        projMatrix.ortho(0, width, height, 0, -50, 50);
        shader.bind();
        shader.loadMatrix("projMatrix", projMatrix);
    }

}

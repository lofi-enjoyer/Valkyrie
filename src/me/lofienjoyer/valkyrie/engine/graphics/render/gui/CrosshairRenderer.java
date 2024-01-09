package me.lofienjoyer.valkyrie.engine.graphics.render.gui;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.QuadMesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
import me.lofienjoyer.valkyrie.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL46.*;

public class CrosshairRenderer {

    private final int crosshairTexture;
    private final QuadMesh quadMesh;
    private final Shader fboShader;

    public CrosshairRenderer() {
        this.crosshairTexture = Valkyrie.LOADER.loadTexture("res/textures/gui/crosshair.png");
        this.quadMesh = new QuadMesh();
        this.fboShader = ResourceLoader.loadShader("crosshair-shader", "res/shaders/crosshair_vertex.glsl", "res/shaders/crosshair_fragment.glsl");

        var crosshairTransform = Maths.createTransformationMatrix(new Vector3f(0), 0);
        fboShader.bind();
        fboShader.loadMatrix("transformationMatrix", crosshairTransform);
    }

    public void render() {
        fboShader.bind();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(quadMesh.getVaoId());
        glDisable(GL_DEPTH_TEST);
        glBindTexture(GL_TEXTURE_2D, crosshairTexture);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glDisable(GL_BLEND);
    }

    public void setupProjectionMatrix(int width, int height) {
        var crosshairProjection = new Matrix4f();
        crosshairProjection.ortho(-width / 32f, width / 32f, -height / 32f, height / 32f, -50, 50);

        fboShader.bind();
        fboShader.loadMatrix("projectionMatrix", crosshairProjection);
    }

}

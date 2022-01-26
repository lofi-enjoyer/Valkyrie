package me.aurgiyalgo.nublada;

import me.aurgiyalgo.nublada.graphics.camera.Camera;
import me.aurgiyalgo.nublada.graphics.display.Window;
import me.aurgiyalgo.nublada.graphics.loader.Loader;
import me.aurgiyalgo.nublada.graphics.render.WorldRenderer;
import me.aurgiyalgo.nublada.graphics.shaders.StaticShader;
import me.aurgiyalgo.nublada.log.NubladaLogHandler;
import me.aurgiyalgo.nublada.world.BlockRegistry;
import me.aurgiyalgo.nublada.world.World;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Nublada {

    public static final Logger LOG = NubladaLogHandler.initLogs();
    public static final Loader LOADER = new Loader();

    private final Window window;
    private final WorldRenderer worldRenderer;

    public Nublada() {
        LOG.setLevel(Level.INFO);

        // FIXME: 09/01/2022 Make this customizable
        this.window = new Window(640, 360, "Nublada");
        this.worldRenderer = new WorldRenderer();
        worldRenderer.setupProjectionMatrix(640, 360);

        window.setResizeCallback(worldRenderer::setupProjectionMatrix);
    }

    public void init() {
        GL.createCapabilities();

        BlockRegistry.setup();

        StaticShader shader = new StaticShader();
        Camera camera = new Camera();
        World world = new World();

        window.setClearColor(Color.CYAN);

        int mouse = 0;

        long timer = System.nanoTime();
        float delta = 1f;

        window.show();

        while (window.keepOpen()) {
            window.clearBuffers();

            camera.update(window.getId(), delta);
            worldRenderer.updateFrustum(camera);

            shader.start();
            shader.loadViewMatrix(camera);
            worldRenderer.render(world, shader, camera);
            shader.stop();

            if (GLFW.glfwGetMouseButton(window.getId(), 0) == 0 &&
                    GLFW.glfwGetMouseButton(window.getId(), 1) == 0) mouse = 0;

            if (mouse != 1 && GLFW.glfwGetMouseButton(window.getId(), 0) != 0) {
                mouse = 1;
                world.raycast(camera.getPosition(), camera.getDirection(), 10, false);
            }

            if (mouse != 1 && GLFW.glfwGetMouseButton(window.getId(), 1) != 0) {
                mouse = 1;
                world.raycast(camera.getPosition(), camera.getDirection(), 10, true);
            }

            window.update();

            delta = (System.nanoTime() - timer) / 1000000000f;
            timer = System.nanoTime();

            GLFW.glfwSetWindowTitle(window.getId(), "Nublada | FPS: " + (int) (1f / delta) + " (delta: " + delta + "s)");
        }
    }

    public void dispose() {
        LOADER.dispose();
    }

}

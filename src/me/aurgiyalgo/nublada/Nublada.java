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

import java.util.logging.Level;
import java.util.logging.Logger;

public class Nublada {

    public static final Logger LOG = NubladaLogHandler.initLogs();

    private final Window window;
    public static Loader loader;
    private final WorldRenderer worldRenderer;

    public Nublada() {
        LOG.setLevel(Level.INFO);

        // FIXME: 09/01/2022 Make this customizable
        this.window = new Window(1280, 720, "Nublada");
        loader = new Loader();
        this.worldRenderer = new WorldRenderer();
        worldRenderer.setupProjectionMatrix(1280, 720);

        window.setResizeCallback(worldRenderer::setupProjectionMatrix);

        BlockRegistry.setup();
    }

    public void init() {
        GL.createCapabilities();

        StaticShader shader = new StaticShader();

        Camera camera = new Camera();

        window.show();
        window.setClearColor(0.5f, 0.125f, 0.25f, 1f);

        World world = new World(loader);

        int mouse = 0;

        new Thread(() -> {
            while (window.keepOpen()) {
                world.generateNextChunk();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        float timer = System.nanoTime();
        float delta = 1f;

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

            world.updateNextChunk();

            delta = (System.nanoTime() - timer) / 1000000000f;
            timer = System.nanoTime();

            GLFW.glfwSetWindowTitle(window.getId(), "Nublada | FPS: " + (int) (1f / delta) + " (delta: " + delta + "s)");
        }

        loader.dispose();
    }

}

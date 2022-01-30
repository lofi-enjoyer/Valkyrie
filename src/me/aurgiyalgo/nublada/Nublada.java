package me.aurgiyalgo.nublada;

import me.aurgiyalgo.nublada.engine.scene.IScene;
import me.aurgiyalgo.nublada.graphics.camera.Camera;
import me.aurgiyalgo.nublada.graphics.display.Window;
import me.aurgiyalgo.nublada.graphics.loader.Loader;
import me.aurgiyalgo.nublada.graphics.render.WorldRenderer;
import me.aurgiyalgo.nublada.graphics.shaders.WorldShader;
import me.aurgiyalgo.nublada.log.NubladaLogHandler;
import me.aurgiyalgo.nublada.world.BlockRegistry;
import me.aurgiyalgo.nublada.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Nublada {

    public static final Logger LOG = NubladaLogHandler.initLogs();
    public static final Loader LOADER = new Loader();

    private final Window window;

    private IScene currentScene;

    public Nublada() {
        LOG.setLevel(Level.INFO);

        // FIXME: 09/01/2022 Make this customizable
        this.window = new Window(640, 360, "Nublada");

        GL.createCapabilities();
    }

    public void init() {

        BlockRegistry.setup();

        window.setClearColor(0.45f, 0.71f, 1.00f, 1f);

        long timer = System.nanoTime();
        float delta = 1f;

        window.show();

        while (window.keepOpen()) {
            window.clearBuffers();

            if (currentScene != null)
                currentScene.render(delta);

            window.update();

            delta = (System.nanoTime() - timer) / 1000000000f;
            timer = System.nanoTime();

            GLFW.glfwSetWindowTitle(window.getId(), "Nublada | FPS: " + (int) (1f / delta) + " (delta: " + delta + "s)");
        }
    }

    public void setCurrentScene(IScene scene) {
        if (currentScene != null)
            currentScene.dispose();
        scene.init();
        this.currentScene = scene;
        window.setResizeCallback(scene::onResize);
    }

    public void dispose() {
        LOADER.dispose();
    }

}

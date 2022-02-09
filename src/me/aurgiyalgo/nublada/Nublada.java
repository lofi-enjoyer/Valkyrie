package me.aurgiyalgo.nublada;

import me.aurgiyalgo.nublada.engine.scene.IScene;
import me.aurgiyalgo.nublada.engine.graphics.display.Window;
import me.aurgiyalgo.nublada.engine.graphics.loader.Loader;
import me.aurgiyalgo.nublada.engine.log.NubladaLogHandler;
import me.aurgiyalgo.nublada.engine.world.BlockRegistry;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Nublada {

    public static final Logger LOG = NubladaLogHandler.initLogs();
    public static final Loader LOADER = new Loader();

    private final Window window;

    public static long WINDOW_ID;

    private IScene currentScene;

    public Nublada() {
        LOG.setLevel(Level.INFO);

        // FIXME: 09/01/2022 Make this customizable
        this.window = new Window(640, 360, "Nublada");

        WINDOW_ID = window.getId();
    }

    public void init() {
        GL.createCapabilities();

        BlockRegistry.setup();

        window.setClearColor(0.45f, 0.71f, 1.00f, 1f);

        window.show();
    }

    public void loop() {
        long timer = System.nanoTime();
        float delta = 1f;

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

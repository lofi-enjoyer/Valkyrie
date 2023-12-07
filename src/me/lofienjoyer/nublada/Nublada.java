package me.lofienjoyer.nublada;

import me.lofienjoyer.nublada.engine.events.EventHandler;
import me.lofienjoyer.nublada.engine.events.global.StartupEvent;
import me.lofienjoyer.nublada.engine.graphics.display.Window;
import me.lofienjoyer.nublada.engine.graphics.framebuffer.ColorFramebuffer;
import me.lofienjoyer.nublada.engine.graphics.framebuffer.Framebuffer;
import me.lofienjoyer.nublada.engine.graphics.loader.Loader;
import me.lofienjoyer.nublada.engine.graphics.mesh.QuadMesh;
import me.lofienjoyer.nublada.engine.graphics.shaders.FboShader;
import me.lofienjoyer.nublada.engine.log.NubladaLogHandler;
import me.lofienjoyer.nublada.engine.scene.IScene;
import me.lofienjoyer.nublada.engine.world.BlockRegistry;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL45.*;

public class Nublada {

    public static final Logger LOG = NubladaLogHandler.initLogs();
    public static final Loader LOADER = new Loader();
    public static final EventHandler EVENT_HANDLER = new EventHandler();

    private final Window window;
    public static long WINDOW_ID;
    private Framebuffer framebuffer;

    private IScene currentScene;

    public Nublada() {
        LOG.setLevel(Level.INFO);

        // FIXME: 09/01/2022 Make this customizable
        this.window = new Window(1280, 720, "Nublada");

        WINDOW_ID = window.getId();
        EVENT_HANDLER.registerListener(StartupEvent.class, (event) -> {
            LOG.info("Successful startup!");
            LOG.info(glGetString(GL_VENDOR) + " - " + glGetString(GL_RENDERER));
        });
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

        framebuffer = new ColorFramebuffer(window.getWidth(), window.getHeight());
        FboShader shader = new FboShader();
        QuadMesh quadMesh = new QuadMesh();

        EVENT_HANDLER.process(new StartupEvent());

        while (window.keepOpen()) {
            framebuffer.bind();
            glClearColor(0.125f, 0f, 1.0f, 0.5f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glEnable(GL_DEPTH_TEST);
            glViewport(0, 0, framebuffer.getWidth(), framebuffer.getHeight());

            if (currentScene != null)
                currentScene.render(delta);

            framebuffer.unbind();
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            shader.start();

            glViewport(0, 0, window.getWidth(), window.getHeight());
            glBindVertexArray(quadMesh.getVaoId());
            glDisable(GL_DEPTH_TEST);
            glBindTexture(GL_TEXTURE_2D, framebuffer.getColorTextureId());
            glDrawArrays(GL_TRIANGLES, 0, 6);

            window.update();
            EVENT_HANDLER.update();

            delta = (System.nanoTime() - timer) / 1000000000f;
            timer = System.nanoTime();

            GLFW.glfwSetWindowTitle(window.getId(), "Nublada | FPS: " + (int) (1f / delta) + " (delta: " + delta + "s) | " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 2024) + "MB");
        }

        currentScene.onClose();
    }

    public void setCurrentScene(IScene scene) {
        if (currentScene != null)
            currentScene.dispose();
        scene.init();
        this.currentScene = scene;
        window.setResizeCallback((width, height) -> {
            onResize(width, height);
            scene.onResize(width, height);
        });
    }

    private void onResize(int width, int height) {
        framebuffer.resize(width, height);
    }

    public void dispose() {
        LOADER.dispose();
    }

}

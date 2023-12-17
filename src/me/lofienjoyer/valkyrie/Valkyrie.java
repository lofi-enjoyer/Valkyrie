package me.lofienjoyer.valkyrie;

import me.lofienjoyer.valkyrie.engine.events.EventHandler;
import me.lofienjoyer.valkyrie.engine.events.global.StartupEvent;
import me.lofienjoyer.valkyrie.engine.graphics.display.Window;
import me.lofienjoyer.valkyrie.engine.graphics.framebuffer.ColorFramebuffer;
import me.lofienjoyer.valkyrie.engine.graphics.framebuffer.Framebuffer;
import me.lofienjoyer.valkyrie.engine.graphics.loader.Loader;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.QuadMesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.FboShader;
import me.lofienjoyer.valkyrie.engine.input.Input;
import me.lofienjoyer.valkyrie.engine.log.NubladaLogHandler;
import me.lofienjoyer.valkyrie.engine.scene.IScene;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL45.*;

public class Valkyrie {

    public static final Logger LOG = NubladaLogHandler.initLogs();
    public static final Loader LOADER = new Loader();
    public static final EventHandler EVENT_HANDLER = new EventHandler();

    public static float FOV = (float) Math.toRadians(80.0);

    private final Window window;
    private final Input input;
    public static long WINDOW_ID;
    private Framebuffer framebuffer;

    private IScene currentScene;

    public Valkyrie() {
        LOG.setLevel(Level.INFO);

        // FIXME: 09/01/2022 Make this customizable
        this.window = Window.getInstance();
        window.setSize(1280, 720);
        window.setTitle("Nublada");

        this.input = Input.getInstance();

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
        window.registerResizeCallback((windowId, width, height) -> glViewport(0, 0, width, height));
        window.registerResizeCallback(this::onResize);

        window.show();
    }

    public void loop() {
        long timer = System.nanoTime();
        float delta = 0f;

        framebuffer = new ColorFramebuffer(window.getWidth(), window.getHeight());
        FboShader shader = new FboShader();
        QuadMesh quadMesh = new QuadMesh();

        EVENT_HANDLER.process(new StartupEvent());

        float fixedUpdateTimer = 0f;

        while (window.keepOpen()) {

            fixedUpdateTimer += delta;
            while (fixedUpdateTimer >= 1 / 20f) {
                currentScene.fixedUpdate();
                input.update();
                fixedUpdateTimer -= 1 / 20f;
            }

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

            GLFW.glfwSetWindowTitle(
                    window.getId(),
                    String.format(
                            "Nublada | FPS: %.1f (delta: %.4fs) | Memory usage: %.2f/%.2f MB",
                            1f / delta,
                            delta,
                            (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024f),
                            Runtime.getRuntime().totalMemory() / (1024 * 1024f)
            ));
        }

        currentScene.onClose();
    }

    public void setCurrentScene(IScene scene) {
        if (currentScene != null)
            currentScene.dispose();
        scene.init();
        this.currentScene = scene;
        scene.onResize(window.getWidth(), window.getHeight());
    }

    private void onResize(long windowId, int width, int height) {
        framebuffer.resize(width, height);
        currentScene.onResize(width, height);
    }

    public void dispose() {
        LOADER.dispose();
    }

}
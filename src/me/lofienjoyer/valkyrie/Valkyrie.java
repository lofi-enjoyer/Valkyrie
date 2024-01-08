package me.lofienjoyer.valkyrie;

import me.lofienjoyer.valkyrie.engine.config.Config;
import me.lofienjoyer.valkyrie.engine.events.EventHandler;
import me.lofienjoyer.valkyrie.engine.events.global.StartupEvent;
import me.lofienjoyer.valkyrie.engine.graphics.display.Window;
import me.lofienjoyer.valkyrie.engine.graphics.framebuffer.ColorFramebuffer;
import me.lofienjoyer.valkyrie.engine.graphics.framebuffer.Framebuffer;
import me.lofienjoyer.valkyrie.engine.graphics.loader.Loader;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.QuadMesh;
import me.lofienjoyer.valkyrie.engine.input.Input;
import me.lofienjoyer.valkyrie.engine.log.ValkyrieLogHandler;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
import me.lofienjoyer.valkyrie.engine.scene.IScene;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import org.lwjgl.opengl.GL;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL45.*;

public class Valkyrie {

    public static final Logger LOG = ValkyrieLogHandler.initLogs();
    public static final Loader LOADER = new Loader();
    public static final EventHandler EVENT_HANDLER = new EventHandler();

    private static ScheduledExecutorService meshingService;

    public static long WINDOW_ID;
    public static float FOV = (float) Math.toRadians(80.0);
    public static boolean DEBUG_MODE = false;

    private final Window window;
    private final Input input;
    private final Config config;

    private IScene currentScene;
    private Framebuffer mainFramebuffer;

    public Valkyrie() {
        LOG.setLevel(Level.INFO);

        this.config = Config.getInstance();
        this.input = Input.getInstance();
        this.window = Window.getInstance();
        WINDOW_ID = window.getId();
    }

    /**
     * Sets up the basic resources for the engine
     */
    public void init() {
        // Sets up the meshing service
        var meshingThreadCount = config.get("meshing_thread_count", Integer.class);
        meshingService = new ScheduledThreadPoolExecutor(config.get("meshing_thread_count", Integer.class), r -> {
            Thread thread = new Thread(r, "Meshing Thread");
            thread.setDaemon(true);

            return thread;
        });

        EVENT_HANDLER.registerListener(StartupEvent.class, (event) -> {
            LOG.info("Successful startup!");
            LOG.info("Meshing thread count: " + meshingThreadCount);
        });

        FOV = (float) Math.toRadians(config.get("fov", Double.class));

        GL.createCapabilities();

        this.mainFramebuffer = new ColorFramebuffer(window.getWidth(), window.getHeight());

        // Sets up the block registry
        BlockRegistry.setup();

        // Sets up the window properties and callbacks, and then shows it
        window.setSize(config.get("window_width", Integer.class), config.get("window_height", Integer.class));
        window.setTitle("Valkyrie");
        window.setClearColor(0.45f, 0.71f, 1.00f, 1f);
        window.registerResizeCallback((windowId, width, height) -> glViewport(0, 0, width, height));
        window.registerResizeCallback(this::onResize);
        window.show();

        // Sends an startup event
        EVENT_HANDLER.process(new StartupEvent());
    }

    /**
     * Starts the engine main loop
     */
    public void loop() {
        long lastFrame = System.nanoTime();
        float delta = 0f;

        var fboShader = ResourceLoader.loadShader("FBO Shader", "res/shaders/fbo/fbo_vert.glsl", "res/shaders/fbo/fbo_frag.glsl");
        var quadMesh = new QuadMesh();

        while (window.keepOpen()) {
            // Render current scene to the framebuffer
            mainFramebuffer.bind();
            glClearColor(0.125f, 0f, 1.0f, 0.5f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glEnable(GL_DEPTH_TEST);
            glViewport(0, 0, mainFramebuffer.getWidth(), mainFramebuffer.getHeight());

            if (currentScene != null)
                currentScene.render(delta);

            mainFramebuffer.unbind();
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Draw the framebuffer to the window
            fboShader.bind();
            glViewport(0, 0, window.getWidth(), window.getHeight());
            glBindVertexArray(quadMesh.getVaoId());
            glDisable(GL_DEPTH_TEST);
            glBindTexture(GL_TEXTURE_2D, mainFramebuffer.getColorTextureId());
            glDrawArrays(GL_TRIANGLES, 0, 6);

            // Updates the input handler and window
            // IMPORTANT: the window must be always updated after the input handler,
            // so it registers properly all the key and button updates
            input.update();
            window.update();

            delta = (System.nanoTime() - lastFrame) / 1000000000f;
            lastFrame = System.nanoTime();
        }
    }

    /**
     * Disposes the current scene, sets up the new one and calls its {@code onResize} method once
     * @param scene New scene
     */
    public void setCurrentScene(IScene scene) {
        if (currentScene != null)
            currentScene.dispose();
        scene.init();
        this.currentScene = scene;
        scene.onResize(window.getWidth(), window.getHeight());
    }

    private void onResize(long windowId, int width, int height) {
        if (currentScene != null)
            currentScene.onResize(width, height);

        mainFramebuffer.resize(width, height);
    }

    public void dispose() {
        if (currentScene != null)
            currentScene.dispose();

        LOADER.dispose();
    }

    public static ScheduledExecutorService getMeshingService() {
        return meshingService;
    }

}

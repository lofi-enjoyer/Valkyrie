package me.lofienjoyer.valkyrie;

import me.lofienjoyer.valkyrie.engine.config.Config;
import me.lofienjoyer.valkyrie.engine.events.EventHandler;
import me.lofienjoyer.valkyrie.engine.events.global.StartupEvent;
import me.lofienjoyer.valkyrie.engine.graphics.display.Window;
import me.lofienjoyer.valkyrie.engine.graphics.framebuffer.ColorFramebuffer;
import me.lofienjoyer.valkyrie.engine.graphics.framebuffer.Framebuffer;
import me.lofienjoyer.valkyrie.engine.graphics.loader.Loader;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.QuadMesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.FboShader;
import me.lofienjoyer.valkyrie.engine.input.Input;
import me.lofienjoyer.valkyrie.engine.log.ValkyrieLogHandler;
import me.lofienjoyer.valkyrie.engine.scene.IScene;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import org.lwjgl.opengl.GL;

import java.util.Timer;
import java.util.TimerTask;
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

    public static float FOV = (float) Math.toRadians(80.0);
    public static boolean DEBUG_MODE = false;

    private final Window window;
    private final Input input;
    private final Config config;
    public static long WINDOW_ID;
    private Framebuffer framebuffer;

    private IScene currentScene;

    public Valkyrie() {
        LOG.setLevel(Level.INFO);

        this.config = Config.getInstance();

        this.window = Window.getInstance();
        window.setSize(config.get("window_width", Integer.class), config.get("window_height", Integer.class));
        window.setTitle("Valkyrie");
        FOV = (float) Math.toRadians(config.get("fov", Double.class));

        this.input = Input.getInstance();

        int meshingThreadCount = config.get("meshing_thread_count", Integer.class);

        meshingService = new ScheduledThreadPoolExecutor(config.get("meshing_thread_count", Integer.class), r -> {
            Thread thread = new Thread(r, "Meshing Thread");
            thread.setDaemon(true);

            return thread;
        });

        WINDOW_ID = window.getId();
        EVENT_HANDLER.registerListener(StartupEvent.class, (event) -> {
            LOG.info("Successful startup!");
            LOG.info("Meshing thread count: " + meshingThreadCount);
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
        long lastFrame = System.nanoTime();
        float delta = 0f;

        framebuffer = new ColorFramebuffer(window.getWidth(), window.getHeight());
        FboShader shader = new FboShader();
        QuadMesh quadMesh = new QuadMesh();

        EVENT_HANDLER.process(new StartupEvent());

        while (window.keepOpen()) {

            // Render current scene to the framebuffer
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

            // Draw the framebuffer to the window
            shader.start();
            glViewport(0, 0, window.getWidth(), window.getHeight());
            glBindVertexArray(quadMesh.getVaoId());
            glDisable(GL_DEPTH_TEST);
            glBindTexture(GL_TEXTURE_2D, framebuffer.getColorTextureId());
            glDrawArrays(GL_TRIANGLES, 0, 6);

            input.update();
            window.update();

            delta = (System.nanoTime() - lastFrame) / 1000000000f;
            lastFrame = System.nanoTime();
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

    public static ScheduledExecutorService getMeshingService() {
        return meshingService;
    }

}

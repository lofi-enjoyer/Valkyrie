package me.aurgiyalgo.nublada;

import me.aurgiyalgo.nublada.engine.graphics.shaders.FboShader;
import me.aurgiyalgo.nublada.engine.scene.IScene;
import me.aurgiyalgo.nublada.engine.graphics.display.Window;
import me.aurgiyalgo.nublada.engine.graphics.loader.Loader;
import me.aurgiyalgo.nublada.engine.log.NubladaLogHandler;
import me.aurgiyalgo.nublada.engine.utils.Timings;
import me.aurgiyalgo.nublada.engine.world.BlockRegistry;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

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
        this.window = new Window(1280, 720, "Nublada");

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

        int framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);

        int textureColorBuffer = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureColorBuffer);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGB, 640, 360, 0, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE, 0);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, textureColorBuffer, 0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);

        int rbo = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, 640, 360);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            Nublada.LOG.severe("Error while creating framebuffer!");
        }

        float[] quadVertices = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
                // positions   // texCoords
                -1.0f,  1.0f,  0.0f, 1.0f,
                -1.0f, -1.0f,  0.0f, 0.0f,
                1.0f, -1.0f,  1.0f, 0.0f,

                -1.0f,  1.0f,  0.0f, 1.0f,
                1.0f, -1.0f,  1.0f, 0.0f,
                1.0f,  1.0f,  1.0f, 1.0f
        };

        FboShader shader = new FboShader();

        int quadVAO, quadVBO;
        quadVAO = GL30.glGenVertexArrays();
        quadVBO = GL30.glGenBuffers();
        GL30.glBindVertexArray(quadVAO);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, quadVBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, quadVertices, GL30.GL_STATIC_DRAW);
        GL30.glEnableVertexAttribArray(0);
        GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        while (window.keepOpen()) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            GL30.glClearColor(0.125f, 0f, 1.0f, 0.5f);
            GL30.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL30.glEnable(GL11.GL_DEPTH_TEST);
            GL30.glViewport(0, 0, 640, 360);

            if (currentScene != null)
                currentScene.render(delta);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            GL30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);

            shader.start();

            GL30.glViewport(0, 0, window.getWidth(), window.getHeight());
            GL30.glBindVertexArray(quadVAO);
            GL30.glDisable(GL11.GL_DEPTH_TEST);
            GL30.glBindTexture(GL11.GL_TEXTURE_2D, textureColorBuffer);
            GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

            window.update();

            delta = (System.nanoTime() - timer) / 1000000000f;
            timer = System.nanoTime();

            GLFW.glfwSetWindowTitle(window.getId(), "Nublada | FPS: " + (int) (1f / delta) + " (delta: " + delta + "s)");

            Timings.flushTimings();
        }

        currentScene.onClose();
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

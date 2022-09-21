package me.lofienjoyer.nublada.engine.graphics.display;

import me.lofienjoyer.nublada.Nublada;
import me.lofienjoyer.nublada.engine.graphics.render.WorldRenderer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.*;
import java.nio.*;
import java.util.function.BiConsumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    public static long id;

    private int width;
    private int height;
    private final String title;

    private boolean wireframe;
    private boolean faceCulling;
    private boolean vsync = true;

    // TODO: 09/02/2022 Make this a singleton
    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            Nublada.LOG.severe("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 8);

        id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (id == NULL)
            Nublada.LOG.severe("Error while creating the window");

        glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
                wireframe = !wireframe;
                GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);
            }
            if (key == GLFW_KEY_T && action == GLFW_RELEASE) {
                WorldRenderer.VIEW_DISTANCE--;
            }
            if (key == GLFW_KEY_Y && action == GLFW_RELEASE) {
                WorldRenderer.VIEW_DISTANCE++;
            }
            if (key == GLFW_KEY_H && action == GLFW_RELEASE) {
                vsync = !vsync;
                glfwSwapInterval(vsync ? 1 : 0);
            }
        });

        glfwSetWindowSizeCallback(id, (window, newwidth, newheight) -> {
            glViewport(0, 0, newwidth, newheight);
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(id, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    id,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        Nublada.LOG.info("Window succesfully created");

        glfwMakeContextCurrent(id);
        Nublada.LOG.info("OpenGL Context set");
        glfwSwapInterval(vsync ? 1 : 0);
    }

    public void show() {
        glfwShowWindow(id);
    }

    public void clearBuffers() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void update() {
        glfwSwapBuffers(id);
        glfwPollEvents();
    }

    public void setResizeCallback(BiConsumer<Integer, Integer> action) {
        glfwSetWindowSizeCallback(id, (window, newWidth, newHeight) -> {
            this.width = newWidth;
            this.height = newHeight;
            action.accept(newWidth, newHeight);
        });
    }

    public void setClearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public void setClearColor(Color color) {
        setClearColor(color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f, color.getAlpha() / 256f);
    }

    public boolean keepOpen() {
        return !glfwWindowShouldClose(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getId() {
        return id;
    }
}

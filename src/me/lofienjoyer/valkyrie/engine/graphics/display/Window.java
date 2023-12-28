package me.lofienjoyer.valkyrie.engine.graphics.display;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.render.WorldRenderer;
import me.lofienjoyer.valkyrie.engine.world.World;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private static Window instance;

    public static long id;

    private int width;
    private int height;

    private boolean wireframe;
    private boolean vsync = true;

    private final List<GLFWWindowSizeCallbackI> resizeCallbacks;
    private final List<GLFWKeyCallbackI> keyCallbacks;
    private final List<GLFWMouseButtonCallbackI> buttonCallbacks;
    private final List<GLFWCursorPosCallbackI> cursorPosCallbacks;

    private Window() {
        this.width = 800;
        this.height = 600;
        String title = "Nublada";

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            Valkyrie.LOG.severe("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (id == NULL)
            Valkyrie.LOG.severe("Error while creating the window");

        this.resizeCallbacks = new ArrayList<>();
        glfwSetWindowSizeCallback(id, (id, width, height) -> {
            this.width = width;
            this.height = height;
            resizeCallbacks.forEach(callback -> callback.invoke(id, width, height));
        });

        this.keyCallbacks = new ArrayList<>();
        glfwSetKeyCallback(id, (id, key, scancode, action, mods) -> {
            keyCallbacks.forEach(callback -> callback.invoke(id, key, scancode, action, mods));
        });
        keyCallbacks.add((id, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
                wireframe = !wireframe;
                GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);
            }
            if (key == GLFW_KEY_F3 && action == GLFW_RELEASE) {
                Valkyrie.DEBUG_MODE = !Valkyrie.DEBUG_MODE;
            }
            if (key == GLFW_KEY_H && action == GLFW_RELEASE) {
                vsync = !vsync;
                glfwSwapInterval(vsync ? 1 : 0);
            }
            if (key == GLFW_KEY_P && action == GLFW_RELEASE) {
                System.gc();
            }
        });

        this.buttonCallbacks = new ArrayList<>();
        glfwSetMouseButtonCallback(id, (id, button, action, mods) -> {
            buttonCallbacks.forEach(callback -> callback.invoke(id, button, action, mods));
        });

        this.cursorPosCallbacks = new ArrayList<>();
        glfwSetCursorPosCallback(id, (id, width, height) -> {
            cursorPosCallbacks.forEach(callback -> callback.invoke(id, width, height));
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

        Valkyrie.LOG.info("Window succesfully created");

        glfwMakeContextCurrent(id);
        Valkyrie.LOG.info("OpenGL Context set");
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

    public void registerResizeCallback(GLFWWindowSizeCallbackI callback) {
        resizeCallbacks.add(callback);
    }

    public void unregisterResizeCallback(GLFWWindowSizeCallbackI callback) {
        resizeCallbacks.remove(callback);
    }

    public void registerKeyCallback(GLFWKeyCallbackI callback) {
        keyCallbacks.add(callback);
    }

    public void unregisterKeyCallback(GLFWKeyCallbackI callback) {
        keyCallbacks.remove(callback);
    }

    public void registerButtonCallback(GLFWMouseButtonCallbackI callback) {
        buttonCallbacks.add(callback);
    }

    public void unregisterButtonCallback(GLFWMouseButtonCallbackI callback) {
        buttonCallbacks.remove(callback);
    }

    public void registerCursorPosCallback(GLFWCursorPosCallbackI callback) {
        cursorPosCallbacks.add(callback);
    }

    public void unregisterCursorPosCallback(GLFWCursorPosCallbackI callback) {
        cursorPosCallbacks.remove(callback);
    }

    public void setSize(int width, int height) {
        glfwSetWindowSize(id, width, height);
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(id, title);
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

    public static Window getInstance() {
        if (instance == null)
            instance = new Window();

        return instance;
    }

}

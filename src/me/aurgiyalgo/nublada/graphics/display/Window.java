package me.aurgiyalgo.nublada.graphics.display;

import me.aurgiyalgo.nublada.Nublada;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private long id;

    private int width;
    private int height;
    private String title;

    private boolean wireframe;
    private boolean faceCulling;

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

        id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (id == NULL)
            Nublada.LOG.severe("Error while creating the window");

        glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
                wireframe = !wireframe;
                GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);
            }
            if (key == GLFW_KEY_T && action == GLFW_RELEASE) {
                faceCulling = !faceCulling;
                if (faceCulling) {
                    glEnable(GL_CULL_FACE);
                    glCullFace(GL_BACK);
                } else {
                    glDisable(GL_CULL_FACE);
                }
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

        glfwMakeContextCurrent(id);
        glfwSwapInterval(0);
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

    public void setClearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public boolean keepOpen() {
        return !glfwWindowShouldClose(id);
    }

    public long getId() {
        return id;
    }
}

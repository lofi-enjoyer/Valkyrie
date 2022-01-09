package me.aurgiyalgo.nublada.graphics.camera;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private Vector3f position;
    private float rotationY, rotationX, roll;

    boolean mouseLocked = false;
    double newX = 400;
    double newY = 300;

    double prevX = 0;
    double prevY = 0;

    boolean rotX = false;
    boolean rotY = false;

    private Vector3f direction;

    private float speed = 16f;

    public Camera() {
        this.position = new Vector3f(0, 64, 0);
        this.direction = new Vector3f();
        rotationX = 180;
        rotationY = 30;
        updateDirection();
    }

    public void updateDirection() {
        float yaw = (float) Math.toRadians(rotationX + 90);
        float pitch = (float) Math.toRadians(rotationY);
//        direction.z = -(float) (Math.cos(Math.toRadians(rotationX)) * Math.cos(Math.toRadians(rotationY)));
        direction.x = (float) (Math.cos(yaw) * Math.cos(pitch));
        direction.y = (float) Math.sin(pitch);
        direction.z = (float) (Math.cos(pitch) * Math.sin(yaw));
//        direction.x = -(float) Math.sin(Math.sin(Math.toRadians(rotationX)) * Math.cos(Math.toRadians(rotationY)));

        direction.normalize().mul(-1);
    }

    public void update(long window, float delta) {
//        if (GLFW.glfwGetKey(window, GLFW_KEY_ESCAPE) == 1) {
//            mouseLocked = !mouseLocked;
//            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, mouseLocked ? GLFW.GLFW_CURSOR_HIDDEN : GLFW_CURSOR_NORMAL);
//            if (mouseLocked)
//                GLFW.glfwSetCursorPos(window, 320, 180);
//        }

        if (GLFW.glfwGetKey(window, GLFW_KEY_W) == 1) {
            this.position.z -= Math.cos(Math.toRadians(rotationX)) * speed * delta;
            this.position.x += Math.sin(Math.toRadians(rotationX)) * speed * delta;
        }

        if (GLFW.glfwGetKey(window, GLFW_KEY_S) == 1) {
            this.position.z += Math.cos(Math.toRadians(rotationX)) * speed * delta;
            this.position.x -= Math.sin(Math.toRadians(rotationX)) * speed * delta;
        }

        if (GLFW.glfwGetKey(window, GLFW_KEY_A) == 1) {
            this.position.x -= Math.cos(Math.toRadians(rotationX)) * speed * delta;
            this.position.z -= Math.sin(Math.toRadians(rotationX)) * speed * delta;
        }

        if (GLFW.glfwGetKey(window, GLFW_KEY_D) == 1) {
            this.position.x += Math.cos(Math.toRadians(rotationX)) * speed * delta;
            this.position.z += Math.sin(Math.toRadians(rotationX)) * speed * delta;
        }

        if (GLFW.glfwGetKey(window, GLFW_KEY_SPACE) == 1) {
            this.position.y += 1 * speed * delta;
        }

        if (GLFW.glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == 1) {
            this.position.y -= 1 * speed * delta;
        }

        if (GLFW.glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
            GLFW.glfwSetCursorPos(window, 320, 180);
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);

            mouseLocked = true;
        }

        if (mouseLocked){
            DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

            GLFW.glfwGetCursorPos(window, x, y);
            x.rewind();
            y.rewind();

            newX = x.get();
            newY = y.get();

            double deltaX = newX - 320;
            double deltaY = newY - 180;

            rotX = newX != prevX;
            rotY = newY != prevY;

            prevX = newX;
            prevY = newY;

            GLFW.glfwSetCursorPos(window, 320, 180);

            rotationX += (float) deltaX * 0.05f;
            rotationY += (float) deltaY * 0.05f;

            rotationX = rotationX % 360;
            rotationY = Math.min(Math.max(-90, rotationY), 90);
        }

        updateDirection();
    }

    public void move(float x, float y, float z) {
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRoll() {
        return roll;
    }

    public Vector3f getDirection() {
        return direction;
    }
}

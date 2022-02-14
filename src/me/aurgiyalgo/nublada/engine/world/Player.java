package me.aurgiyalgo.nublada.engine.world;

import me.aurgiyalgo.nublada.engine.graphics.camera.Camera;
import me.aurgiyalgo.nublada.engine.graphics.display.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

// TODO: 11/02/2022 Temporary class to test collisions
public class Player {

    public Camera camera;
    public Vector3f position;
    public World world;
    private float verticalSpeed = 0.0f;

    public Player() {
        this.position = new Vector3f(0, 256, 0);
    }

    public void update(float delta) {
        position.x = camera.getPosition().x;
        position.z = camera.getPosition().z;
        if (world.getBlock((int)(Math.floor(position.x)), (int)(position.y + 0.5f), (int)(Math.floor(position.z))) != 0) {
            position.y++;
        }
        if (world.getBlock((int)(Math.floor(position.x)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z))) == 0) {
            verticalSpeed += -1.5f * delta;
        } else {
            verticalSpeed = 0.0f;
        }
        if (verticalSpeed == 0) {
            if (GLFW.glfwGetKey(Window.id, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
                verticalSpeed = 0.4f;
            }
        }

        position.y += verticalSpeed;

        camera.getPosition().y = position.y + 1.8f;
    }

}

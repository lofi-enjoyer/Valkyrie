package me.aurgiyalgo.nublada.engine.world;

import me.aurgiyalgo.nublada.engine.graphics.camera.Camera;
import me.aurgiyalgo.nublada.engine.graphics.display.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

// TODO: 11/02/2022 Temporary class to test collisions
public class Player {

    private static final float WIDTH = 0.5f;

    public Camera camera;
    public Vector3f position;
    public World world;
    private float verticalSpeed = 0.0f;
    private boolean contact;

    public Player() {
        this.position = new Vector3f(0, 256, 0);
    }

    public void update(float delta) {
        position.x = camera.getPosition().x;
        position.z = camera.getPosition().z;
        if (world.getBlock((int)(Math.floor(position.x)), (int)(position.y + 0.5f), (int)(Math.floor(position.z))) != 0) {
            position.y++;
        }
        if (!isColliding(delta)) {
            verticalSpeed += -0.75f * delta;
            contact = false;
        } else {
            verticalSpeed = 0.0f;
            contact = true;
        }
        if (contact) {
            if (GLFW.glfwGetKey(Window.id, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
                verticalSpeed = 0.18f;
            }
        }

        position.y += verticalSpeed;

        camera.getPosition().y = position.y + 1.8f;
    }

    private boolean isColliding(float delta) {
        if (world.getBlock((int)(Math.floor(position.x - WIDTH / 2f)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z - WIDTH / 2f))) != 0) {
            return true;
        } else if (world.getBlock((int)(Math.floor(position.x + WIDTH / 2f)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z - WIDTH / 2f))) != 0) {
            return true;
        } else if (world.getBlock((int)(Math.floor(position.x - WIDTH / 2f)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z + WIDTH / 2f))) != 0) {
            return true;
        } else if (world.getBlock((int)(Math.floor(position.x + WIDTH / 2f)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z + WIDTH / 2f))) != 0) {
            return true;
        }
        return false;
    }

}

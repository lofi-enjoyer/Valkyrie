package me.aurgiyalgo.nublada.engine.world;

import me.aurgiyalgo.nublada.engine.graphics.camera.Camera;
import me.aurgiyalgo.nublada.engine.graphics.display.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static me.aurgiyalgo.nublada.engine.world.World.CHUNK_WIDTH;

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
        if (world.getChunk((int) Math.floor(position.x / CHUNK_WIDTH), (int) Math.floor(position.z / CHUNK_WIDTH)) == null)
            return;

        position.x = camera.getPosition().x;
        position.z = camera.getPosition().z;
        int currentBlock = world.getBlock((int)(Math.floor(position.x)), (int)(position.y + 0.5f), (int)(Math.floor(position.z)));
        int blockUnder = world.getBlock((int)(Math.floor(position.x)), (int)(position.y - 0.5f), (int)(Math.floor(position.z)));
        if (currentBlock != 0 && currentBlock != 7) {
            position.y++;
        }
        if (!isColliding(delta)) {
            verticalSpeed += -0.75f * delta * (currentBlock == 7 ? 0.25f : 1f);
            contact = false;
        } else {
            verticalSpeed = 0.0f;
            contact = true;
        }
        if (contact || currentBlock == 7) {
            if (GLFW.glfwGetKey(Window.id, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
                verticalSpeed = currentBlock != 7 ? 0.18f : 0.055f;
            }
        }

        position.y += verticalSpeed;

        camera.getPosition().y = position.y + 1.8f;
    }

    private boolean isColliding(float delta) {
        int[] blocks = new int[4];
        blocks[0] = world.getBlock((int)(Math.floor(position.x - WIDTH / 2f)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z - WIDTH / 2f)));
        blocks[1] = world.getBlock((int)(Math.floor(position.x + WIDTH / 2f)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z - WIDTH / 2f)));
        blocks[2] = world.getBlock((int)(Math.floor(position.x - WIDTH / 2f)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z + WIDTH / 2f)));
        blocks[3] = world.getBlock((int)(Math.floor(position.x + WIDTH / 2f)), (int)(position.y - verticalSpeed * delta), (int)(Math.floor(position.z + WIDTH / 2f)));
        if (blocks[0] != 0 && blocks[0] != 7) {
            return true;
        } else if (blocks[1] != 0 && blocks[1] != 7) {
            return true;
        } else if (blocks[2] != 0 && blocks[2] != 7) {
            return true;
        } else if (blocks[3] != 0 && blocks[3] != 7) {
            return true;
        }
        return false;
    }

}

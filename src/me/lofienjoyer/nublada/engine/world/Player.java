package me.lofienjoyer.nublada.engine.world;

import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.graphics.display.Window;
import me.lofienjoyer.nublada.engine.input.Input;
import org.joml.RayAabIntersection;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static me.lofienjoyer.nublada.engine.world.World.CHUNK_WIDTH;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

// TODO: 11/02/2022 Temporary class to test collisions
public class Player {

    private static final float SPEED = 3f;

    private static final float WIDTH = 0.5f;

    private final Vector3f position;
    private final World world;
    private final Vector3f rotation;
    private final Vector3f movement;
    private float verticalSpeed = 0.0f;
    private boolean contact;

    public Player(World world) {
        this.position = new Vector3f(0, 60, 0);
        this.rotation = new Vector3f();
        this.movement = new Vector3f();
        this.world = world;
    }

    public void update(float delta) {
        var currentChunk = world.getChunk((int) Math.floor(position.x / CHUNK_WIDTH), (int) Math.floor(position.z / CHUNK_WIDTH));
        if (currentChunk == null || currentChunk.getVoxels() == null)
            return;

        movement.x *= 1.5f * delta;
        movement.z *= 1.5f * delta;

        if (Input.isKeyPressed(GLFW_KEY_W)) {
            movement.z -= Math.cos(Math.toRadians(rotation.x)) * SPEED * delta;
            movement.x += Math.sin(Math.toRadians(rotation.x)) * SPEED * delta;
        }

        if (Input.isKeyPressed(GLFW_KEY_S)) {
            movement.z += Math.cos(Math.toRadians(rotation.x)) * SPEED * delta;
            movement.x -= Math.sin(Math.toRadians(rotation.x)) * SPEED * delta;
        }

        if (Input.isKeyPressed(GLFW_KEY_A)) {
            movement.x -= Math.cos(Math.toRadians(rotation.x)) * SPEED * delta;
            movement.z -= Math.sin(Math.toRadians(rotation.x)) * SPEED * delta;
        }

        if (Input.isKeyPressed(GLFW_KEY_D)) {
            movement.x += Math.cos(Math.toRadians(rotation.x)) * SPEED * delta;
            movement.z += Math.sin(Math.toRadians(rotation.x)) * SPEED * delta;
        }

        int currentBlock = world.getBlock((int)(Math.floor(position.x)), (int)(position.y + 0.5f), (int)(Math.floor(position.z)));

        if (!isColliding(delta)) {
            movement.y += -0.375f * delta * (currentBlock == 7 ? 0.05f : 1f);
            contact = false;
        } else {
            movement.y = 0.0f;
            contact = true;
        }

        if ((movement.y == 0f || currentBlock == 7) && Input.isKeyPressed(GLFW_KEY_SPACE)) {
            movement.y = 0.25f;
        }

        position.x += movement.x;
        position.y += movement.y;
        position.z += movement.z;

        if (currentBlock != 0 && currentBlock != 7) {
            position.y++;
        }

//        if (GLFW.glfwGetKey(Window.id, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
//            verticalSpeed = currentBlock != 7 ? 0.75f : 0.15f;
//        }
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

    public Vector3f getPosition() {
        return position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.x = rotation.x;
        this.rotation.y = rotation.y;
        this.rotation.z = rotation.z;
    }

}

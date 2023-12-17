package me.lofienjoyer.valkyrie.engine.world;

import me.lofienjoyer.valkyrie.engine.input.Input;
import org.joml.Vector3f;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

// TODO: 11/02/2022 Temporary class to test collisions
public class Player {

    private static final float SPEED = 6f;
    private static final Vector3f dimensions = new Vector3f(0.3f, 1.5f, 0.3f);

    private static final float WIDTH = 0.5f;
    private static final float HEIGHT = 1.75f;

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

        movement.x = 0;
        movement.z = 0;

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

        movement.y += -1 / 64f;

        position.x += movement.x;
        checkCollisions(new Vector3f(movement.x, 0, 0));

        position.z += movement.z;
        checkCollisions(new Vector3f(0, 0, movement.z));

        position.y += movement.y;
        checkCollisions(new Vector3f(0, movement.y, 0));

        if ((movement.y == 0f) && Input.isKeyPressed(GLFW_KEY_SPACE)) {
            movement.y = 0.25f;
        }
    }

    private void checkCollisions(Vector3f vel) {
        for (float x = position.x - dimensions.x; x <= position.x + dimensions.x; x += dimensions.x) {
            for (float y = position.y; y <= position.y + dimensions.y; y += dimensions.y) {
                for (float z = position.z - dimensions.z; z <= position.z + dimensions.z; z += dimensions.z) {
                    var voxel = world.getBlock(x, y, z);

                    if (voxel != 0) {
                        if (vel.y > 0) {
                            position.y = (int)y - dimensions.y - 1 / 128f;
                            movement.y = 0;
                        }
                        else if (vel.y < 0) {
                            contact = true;
                            position.y = (int)y + 1;
                            movement.y = 0;
                        }

                        if (vel.x > 0) {
                            position.x = (int)Math.floor(x) - dimensions.x - 1 / 128f;
                        }
                        else if (vel.x < 0) {
                            position.x = (int)Math.floor(x) + dimensions.x + 1 + 1 / 128f;
                        }

                        if (vel.z > 0) {
                            position.z = (int)Math.floor(z) - dimensions.z - 1 / 128f;
                        }
                        else if (vel.z < 0) {
                            position.z = (int)Math.floor(z) + dimensions.z + 1 + 1 / 128f;
                        }
                    }
                }
            }
        }
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

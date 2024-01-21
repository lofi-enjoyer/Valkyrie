package me.lofienjoyer.valkyrie.engine.world;

import me.lofienjoyer.valkyrie.engine.input.Input;
import me.lofienjoyer.valkyrie.engine.input.KeyMapping;
import org.joml.Vector3f;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

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
        this.position = new Vector3f(-110, 125, -50);
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

        if (Input.isKeyPressed(KeyMapping.MOVE_FORWARD)) {
            movement.z -= Math.cos(Math.toRadians(rotation.x)) * SPEED * delta;
            movement.x += Math.sin(Math.toRadians(rotation.x)) * SPEED * delta;
        }

        if (Input.isKeyPressed(KeyMapping.MOVE_BACKWARDS)) {
            movement.z += Math.cos(Math.toRadians(rotation.x)) * SPEED * delta;
            movement.x -= Math.sin(Math.toRadians(rotation.x)) * SPEED * delta;
        }

        if (Input.isKeyPressed(KeyMapping.MOVE_LEFT)) {
            movement.x -= Math.cos(Math.toRadians(rotation.x)) * SPEED * delta;
            movement.z -= Math.sin(Math.toRadians(rotation.x)) * SPEED * delta;
        }

        if (Input.isKeyPressed(KeyMapping.MOVE_RIGHT)) {
            movement.x += Math.cos(Math.toRadians(rotation.x)) * SPEED * delta;
            movement.z += Math.sin(Math.toRadians(rotation.x)) * SPEED * delta;
        }

        movement.y += -1 * delta * 0.875f;

        var highestMovementResistance = getCollidingBlockWithHighestMovementResistance();

        movement.mul(1 - highestMovementResistance);

        position.x += movement.x;
        checkCollisions(new Vector3f(movement.x, 0, 0));

        position.z += movement.z;
        checkCollisions(new Vector3f(0, 0, movement.z));

        position.y += movement.y;
        checkCollisions(new Vector3f(0, movement.y, 0));

        if ((contact || highestMovementResistance != 0) && Input.isKeyPressed(KeyMapping.JUMP)) {
            movement.y = 15f * (1 - highestMovementResistance) * delta;
            contact = false;
        }
    }

    private void checkCollisions(Vector3f vel) {
        for (float x = position.x - dimensions.x; x <= position.x + dimensions.x; x += dimensions.x) {
            for (float y = position.y; y <= position.y + dimensions.y; y += dimensions.y / 2f) {
                for (float z = position.z - dimensions.z; z <= position.z + dimensions.z; z += dimensions.z) {
                    var voxel = world.getBlock(x, y, z);

                    if (voxel != 0 && BlockRegistry.getBlock(voxel).hasCollision()) {
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

    private float getCollidingBlockWithHighestMovementResistance() {
        var highestFriction = 0f;

        for (float x = position.x - dimensions.x; x <= position.x + dimensions.x; x += dimensions.x) {
            for (float y = position.y; y <= position.y + dimensions.y; y += dimensions.y / 2f) {
                for (float z = position.z - dimensions.z; z <= position.z + dimensions.z; z += dimensions.z) {
                    var voxel = world.getBlock(x, y, z);

                    if (voxel == 0)
                        continue;

                    var movementResistance = BlockRegistry.getBlock(voxel).getMovementResistance();
                    if (movementResistance > highestFriction)
                        highestFriction = movementResistance;
                }
            }
        }

        return highestFriction;
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

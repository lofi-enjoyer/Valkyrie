package me.aurgiyalgo.nublada.entities;

import me.aurgiyalgo.nublada.graphics.mesh.Mesh;
import org.joml.Vector3f;

public class Entity {

    private Mesh mesh;
    private Vector3f position;
    private Vector3f rotation;

    public Entity(Mesh mesh, Vector3f position, Vector3f rotation) {
        this.mesh = mesh;
        this.position = position;
        this.rotation = rotation;
    }

    public Mesh getModel() {
        return mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
}

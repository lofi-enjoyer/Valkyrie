package me.aurgiyalgo.nublada.entities;

import me.aurgiyalgo.nublada.graphics.model.Model;
import org.joml.Vector3f;

public class Entity {

    private Model model;
    private Vector3f position;
    private Vector3f rotation;

    public Entity(Model model, Vector3f position, Vector3f rotation) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
    }

    public Model getModel() {
        return model;
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

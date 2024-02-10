package me.lofienjoyer.valkyrie.engine.events.mesh;

import me.lofienjoyer.valkyrie.engine.events.Event;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.MeshBundle;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class MeshGenerationEvent implements Event {

    private final MeshBundle meshBundle;
    private final Vector2i position;

    public MeshGenerationEvent(Vector2i position, MeshBundle meshBundle) {
        this.position = position;
        this.meshBundle = meshBundle;
    }

    public Vector2i getPosition() {
        return position;
    }

    public MeshBundle getMeshBundle() {
        return meshBundle;
    }

}

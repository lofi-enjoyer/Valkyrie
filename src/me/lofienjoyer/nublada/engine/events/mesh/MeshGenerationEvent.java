package me.lofienjoyer.nublada.engine.events.mesh;

import me.lofienjoyer.nublada.engine.events.Event;
import me.lofienjoyer.nublada.engine.graphics.mesh.MeshBundle;
import org.joml.Vector2i;

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

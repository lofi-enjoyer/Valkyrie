package me.lofienjoyer.nublada.engine.events.mesh;

import me.lofienjoyer.nublada.engine.events.Event;
import me.lofienjoyer.nublada.engine.graphics.mesh.MeshBundle;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class MeshGenerationEvent implements Event {

    private final MeshBundle meshBundle;
    private final Vector3i position;

    public MeshGenerationEvent(Vector3i position, MeshBundle meshBundle) {
        this.position = position;
        this.meshBundle = meshBundle;
    }

    public Vector3i getPosition() {
        return position;
    }

    public MeshBundle getMeshBundle() {
        return meshBundle;
    }

}

package me.lofienjoyer.valkyrie.engine.events.mesh;

import me.lofienjoyer.valkyrie.engine.events.Event;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.MeshBundle;
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

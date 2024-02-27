package me.lofienjoyer.valkyrie.engine.graphics.mesh;

import java.util.ArrayList;
import java.util.List;

public class BlockMesh extends Mesh {

    private final List<Float> positions;
    private final List<Integer> indices;

    public BlockMesh(float[] positions, int[] indices) {
        super(positions, indices);

        this.positions = new ArrayList<>(positions.length);
        for (float position : positions) {
            this.positions.add(position);
        }

        this.indices = new ArrayList<>(indices.length);
        for (int index : indices) {
            this.indices.add(index);
        }
    }

    public List<Float> getPositions() {
        return positions;
    }

    public List<Integer> getIndices() {
        return indices;
    }

}

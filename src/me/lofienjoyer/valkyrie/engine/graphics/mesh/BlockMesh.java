package me.lofienjoyer.valkyrie.engine.graphics.mesh;

import java.util.ArrayList;
import java.util.List;

public class BlockMesh extends Mesh {

    private final List<Float> positions;
    private final List<Integer> indices;
    private final List<Float> uvs;

    public BlockMesh(float[] positions, int[] indices, float[] uvs) {
        super(positions, indices, uvs);

        this.positions = new ArrayList<>(positions.length);
        for (float position : positions) {
            this.positions.add(position);
        }

        this.indices = new ArrayList<>(indices.length);
        for (int index : indices) {
            this.indices.add(index);
        }

        this.uvs = new ArrayList<>(uvs.length);
        for (float uv : uvs) {
            this.uvs.add(uv);
        }
    }

    public List<Float> getPositions() {
        return positions;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public List<Float> getUvs() {
        return uvs;
    }

}

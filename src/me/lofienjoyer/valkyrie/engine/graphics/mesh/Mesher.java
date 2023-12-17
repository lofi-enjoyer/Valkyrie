package me.lofienjoyer.valkyrie.engine.graphics.mesh;

public interface Mesher {

    Mesher compute();

    Mesh loadToGpu(Mesh mesh);

}

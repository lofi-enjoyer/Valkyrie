package me.lofienjoyer.nublada.engine.graphics.mesh;

public interface Mesher {

    Mesher compute();

    Mesh loadToGpu(Mesh mesh);

}

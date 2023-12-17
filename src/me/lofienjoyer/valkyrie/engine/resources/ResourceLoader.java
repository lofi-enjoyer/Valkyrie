package me.lofienjoyer.valkyrie.engine.resources;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceLoader {

    public static Shader loadShader(String name, String vertexFile, String fragmentFile) {
        String vertexSource, fragmentSource;

        try {
            vertexSource = Files.readString(Paths.get(vertexFile));
            fragmentSource = Files.readString(Paths.get(fragmentFile));
        } catch (IOException e) {
            Valkyrie.LOG.severe(e.getMessage());
            return null;
        }

        return new Shader(name, vertexSource, fragmentSource);
    }

    public static String loadStringFromFile(String fileName) {
        try {
            return Files.readString(Paths.get(fileName));
        } catch (IOException e) {
            Valkyrie.LOG.severe(e.getMessage());
            return null;
        }
    }

}

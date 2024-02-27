package me.lofienjoyer.valkyrie.engine.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.function.Consumer;

public class YamlLoader {

    private final Yaml yaml;
    private Map<String, Object> data;

    public YamlLoader() {
        this.yaml = new Yaml();
    }

    public void loadFile(File file) {
        try {
            this.data = yaml.load(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T get(String key, Class<T> type) {
        return get(key, type, null);
    }

    public <T> T get(String key, Class<T> type, T defaultValue) {
        return (T) data.getOrDefault(key, defaultValue);
    }

    public <T> void ifDataPresent(String key, Class<T> type, Consumer<T> action) {
        var value = get(key, type);
        if (value != null)
            action.accept(value);
    }

}

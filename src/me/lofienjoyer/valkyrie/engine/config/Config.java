package me.lofienjoyer.valkyrie.engine.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private static final String CONFIG_FILE = "./config/conf.txt";

    private static Config instance;

    private Map<String, Object> data;

    private Config() {
        var configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            throw new RuntimeException("./config/conf.txt file not found!");
        }

        loadData(configFile);
    }

    private void loadData(File file) {
        var yaml = new Yaml();
        try {
            this.data = yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getData() {
        return data;
    }

    public <T> T get(String key, Class<T> type) {
        return (T) data.get(key);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();

        return instance;
    }
}

package me.lofienjoyer.nublada.engine.world;

import me.lofienjoyer.nublada.Nublada;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BlockRegistry {

    private static Block[] BLOCKS;
    public static int TILESET_ID;
    private static List<String> texturesList;

    public static void setup() {
        Nublada.LOG.info("Setting up block registry...");
        long timer = System.nanoTime();

        List<Block> blocksToLoad = new ArrayList<>();
        texturesList = new ArrayList<>();

        File blocksFolder = new File("res/blocks");
        if (!blocksFolder.exists()) {
            return;
        }

        Yaml yaml = new Yaml();
        int maxId = 0;

        for (File blockFile : blocksFolder.listFiles()) {
            if (!blockFile.getName().endsWith(".yml")) continue;

            Map<String, Object> data;
            try {
                data = yaml.load(new FileReader(blockFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            int id = (int) data.get("id");
            if (maxId < id) maxId = id;

            String texture = (String) data.get("texture");

            Block block = new Block(id, getTextureId(texture), getTextureId(texture));

            String northTexture = (String) data.get("northTexture");
            if (northTexture != null)
                block.setNorthTexture(getTextureId(northTexture));

            String southTexture = (String) data.get("southTexture");
            if (southTexture != null)
                block.setSouthTexture(getTextureId(southTexture));

            String westTexture = (String) data.get("westTexture");
            if (westTexture != null)
                block.setWestTexture(getTextureId(westTexture));

            String eastTexture = (String) data.get("eastTexture");
            if (eastTexture != null)
                block.setEastTexture(getTextureId(eastTexture));

            String topTexture = (String) data.get("topTexture");
            if (topTexture != null)
                block.setTopTexture(getTextureId(topTexture));

            String bottomTexture = (String) data.get("bottomTexture");
            if (bottomTexture != null)
                block.setBottomTexture(getTextureId(bottomTexture));

            Boolean isTransparent = (Boolean) data.get("transparent");
            if (isTransparent != null) {
                block.setTransparent(isTransparent);
            }

            blocksToLoad.add(block);
        }

        blocksToLoad.sort(Comparator.comparingInt(Block::getId));

        TILESET_ID = Nublada.LOADER.loadTileset(texturesList.toArray(new String[0]));

        blocksToLoad.forEach(Block::setupMesh);

        BLOCKS = new Block[maxId + 1];
        for (Block block : blocksToLoad) {
            BLOCKS[block.getId()] = block;
        }

        Nublada.LOG.info("Block registry has been setup (" + ((System.nanoTime() - timer) / 1000000f) + "ms)");
    }

    public static Block getBLock(int id) {
        return BLOCKS[id];
    }

    private static int getTextureId(String textureName) {
        int textureId = texturesList.indexOf("res/textures/" + textureName + ".png");

        if (textureId == -1) {
            texturesList.add("res/textures/" + textureName + ".png");
            textureId = texturesList.size() - 1;
        }

        return textureId;
    }

    public static int getBlockCount() {
        return BLOCKS.length;
    }

}

package me.lofienjoyer.valkyrie.engine.world;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMeshType;
import me.lofienjoyer.valkyrie.engine.utils.YamlLoader;
import me.lofienjoyer.valkyrie.engine.world.registry.BlockBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class BlockRegistry {

    private static Block[] BLOCKS;
    public static int TILESET_TEXTURE_ID;
    private static List<String> texturesList;

    public static void setup() {
        Valkyrie.LOG.info("Setting up block registry...");
        long timer = System.nanoTime();

        texturesList = new ArrayList<>();

        loadTextures();

        loadBlocks();

        Valkyrie.LOG.info("Block registry has been setup (" + ((System.nanoTime() - timer) / 1000000f) + "ms)");
    }

    public static Block getBlock(int id) {
        return BLOCKS[id];
    }

    public static Block getBlock(String name) {
        for (Block block : BLOCKS) {
            if (block != null && block.getName().equals(name))
                return block;
        }
        return null;
    }

    private static void loadBlocks() {
        File blocksFolder = new File("res/blocks");
        if (!blocksFolder.exists()) {
            throw new RuntimeException("res/blocks folder not found!");
        }

        var blocksToLoad = new ArrayList<BlockBuilder>();

        var loader = new YamlLoader();

        var blockFiles = Arrays.stream(blocksFolder.listFiles()).collect(Collectors.toList());

        for (int i = 0; i < blockFiles.size(); i++) {
            var blockFile = blockFiles.get(i);

            if (!blockFile.getName().endsWith(".yml"))
                continue;

            loader.loadFile(blockFile);

            var builder = new BlockBuilder();

            String name = loader.get("name", String.class);
            int texture = texturesList.indexOf(loader.get("texture", String.class));

            builder.setName(name);
            builder.setTexture(texture);

            loader.ifDataPresent("northTexture", String.class, (value) -> builder.setNorthTexture(texturesList.indexOf(value)));
            loader.ifDataPresent("southTexture", String.class, (value) -> builder.setSouthTexture(texturesList.indexOf(value)));
            loader.ifDataPresent("westTexture", String.class, (value) -> builder.setWestTexture(texturesList.indexOf(value)));
            loader.ifDataPresent("eastTexture", String.class, (value) -> builder.setEastTexture(texturesList.indexOf(value)));
            loader.ifDataPresent("topTexture", String.class, (value) -> builder.setTopTexture(texturesList.indexOf(value)));
            loader.ifDataPresent("bottomTexture", String.class, (value) -> builder.setBottomTexture(texturesList.indexOf(value)));

            builder.setTransparent(loader.get("transparent", Boolean.class, false));
            builder.setShouldDrawBetween(loader.get("drawBetween", Boolean.class, false));
            builder.setHasCollision(loader.get("collision", Boolean.class, true));

            loader.ifDataPresent("movementResistance", Double.class, (value) -> {
                builder.setMovementResistance((float) (double)value);
            });

            loader.ifDataPresent("model", String.class, (value) -> {
                switch (value) {
                    case "x":
                        builder.setMeshType(BlockMeshType.X);
                        builder.setCustomModel(true);
                        break;
                    case "block":
                    default:
                        builder.setMeshType(BlockMeshType.FULL);
                }
            });

            blocksToLoad.add(builder);
        }

        BLOCKS = new Block[blocksToLoad.size() + 1];
        for (int i = 0; i < blocksToLoad.size(); i++) {
            BLOCKS[i + 1] = blocksToLoad.get(i).toBlock(i + 1);
        }
    }

    private static void loadTextures() {
        var texturesFolder = new File("res/textures/blocks");
        if (!texturesFolder.exists()) {
            throw new RuntimeException("res/textures folder not found!");
        }

        for (var textureFile : texturesFolder.listFiles()) {
            if (!textureFile.getName().endsWith(".png")) {
                Valkyrie.LOG.info("Non-texture file found while loading textures: " + textureFile.getName());
                continue;
            }

            texturesList.add(textureFile.getName().replace(".png", ""));
        }

        TILESET_TEXTURE_ID = Valkyrie.LOADER.loadTileset(texturesList.toArray(new String[0]));
    }

    private static int getTextureId(String textureName) {
        int textureId = texturesList.indexOf("res/textures/blocks/" + textureName + ".png");

        if (textureId == -1) {
            texturesList.add("res/textures/blocks/" + textureName + ".png");
            textureId = texturesList.size() - 1;
        }

        return textureId;
    }

    public static int getBlockCount() {
        return BLOCKS.length;
    }

}

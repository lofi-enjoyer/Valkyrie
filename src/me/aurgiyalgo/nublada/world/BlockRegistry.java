package me.aurgiyalgo.nublada.world;

import me.aurgiyalgo.nublada.Nublada;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BlockRegistry {

    private static List<Block> BLOCKS;
    public static int TEXTURE_ARRAY_ID;
    private static List<String> texturesList;

    private static class SortById implements Comparator<Block> {

        @Override
        public int compare(Block block1, Block block2) {
            return block1.getId() - block2.getId();
        }

    }

    public static void setup() {
        BLOCKS = new ArrayList<>();
        texturesList = new ArrayList<>();

        File blocksFolder = new File("res/blocks");
        if (!blocksFolder.exists()) {
            // TODO: 26/01/2022 no se
            return;
        }

        Yaml yaml = new Yaml();

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
            String topTexture = (String) data.get("topTexture");
            String sideTexture = (String) data.get("sideTexture");

            Block block = new Block(id, getTextureId(topTexture), getTextureId(sideTexture));

            BLOCKS.add(block);
        }

        BLOCKS.sort(new SortById());

        TEXTURE_ARRAY_ID = Nublada.LOADER.loadTextureArray(texturesList.toArray(new String[0]));
    }

    public static Block getBLock(int id) {
        return BLOCKS.get(id - 1);
    }

    private static int getTextureId(String textureName) {
        int textureId = texturesList.indexOf(textureName);

        if (textureId == -1) {
            texturesList.add("res/textures/" + textureName);
            textureId = texturesList.size() - 1;
        }

        return textureId;
    }

}

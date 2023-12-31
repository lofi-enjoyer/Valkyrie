package me.lofienjoyer.valkyrie.engine.world;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.events.world.ChunkUpdateEvent;
import me.lofienjoyer.valkyrie.engine.world.populator.Populator;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static me.lofienjoyer.valkyrie.engine.world.World.*;

/**
 * Handles the data and meshing tasks of a single chunk
 */
public class Chunk {

    private static final int NORTH = 0;
    private static final int SOUTH = 2;
    private static final int EAST = 1;
    private static final int WEST = 3;

    protected static final Vector2i[] NEIGHBOR_VECTORS = {
            new Vector2i( 0, -1),
            new Vector2i(-1,  0),
            new Vector2i( 0,  1),
            new Vector2i( 1,  0),
    };

    protected short[] voxels;
    private final Vector2i position;
    protected byte[] compressedData;

    private final World world;

    private final Chunk[] neighbors;

    protected ChunkState state;

    public Chunk(Vector2i position, World world) {
        this.position = position;
        this.world = world;

        this.neighbors = new Chunk[4];
        this.state = ChunkState.UNLOADED;
    }

    // TODO: 24/01/2022 Implement generators for World Generation
    // Temporary generation code
    public void loadChunk(World world) {
        voxels = new short[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];

        for (Populator populator : world.getPopulators()) {
            populator.populate(this);
        }

        this.compressedData = compress(voxels);
    }

    private boolean loadFromFile() {
        try {
            // TODO: 24/12/2022 Support multiple worlds
            File file = new File("world//" + position.x + "_" + position.y + ".dat");
            if (!file.exists())
                return false;

            DataInputStream input = new DataInputStream(new FileInputStream(file));
            this.compressedData = input.readAllBytes();
            input.close();
            this.voxels = decompress(compressedData);

        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    // FIXME 09/01/2023
    public void saveToFile() {
        try {
            if (voxels == null)
                return;

            File file = new File("world//" + position.x + "_" + position.y + ".dat");

            DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new DeflaterOutputStream(output);

            dos.write(compress(voxels));

            dos.flush();
            dos.close();

            output.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    protected short[] decompress(byte[] data) {
        try {
            InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(data));

            byte[] intermediateByteData = iis.readAllBytes();
            short[] decompressedData = new short[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];

            for (int i = 0; i < decompressedData.length; i++) {
                decompressedData[i] = (short) ((intermediateByteData[i * 2 + 1] << 4) | intermediateByteData[i * 2]);
            }

            iis.close();
            return decompressedData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected byte[] compress(short[] data) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new DeflaterOutputStream(outputStream);

            byte[] intermediateData = new byte[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH * 2];
            for (int i = 0; i < data.length; i++) {
                intermediateData[i * 2] = (byte) (data[i] & 0xff);
                intermediateData[i * 2 + 1] = (byte) ((data[i] >> 8) & 0xff);
            }
            dos.write(intermediateData);
            dos.flush();
            dos.close();

            outputStream.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void cacheNeighbors() {
        for (int i = 0; i < 4; i++) {
            neighbors[i] = world.getChunk(position.x + NEIGHBOR_VECTORS[i].x, position.y + NEIGHBOR_VECTORS[i].y);
        }
    }

    public synchronized int getBlock(int x, int y, int z) {
        if (voxels == null) return 0;
        if (y < 0 || y > CHUNK_HEIGHT - 1) return 0;
        if (x < 0) {
            return neighbors[EAST] != null ? neighbors[EAST].getBlock(x + CHUNK_WIDTH, y, z) : 0;
        }
        if (x > CHUNK_WIDTH - 1) {
            return neighbors[WEST] != null ? neighbors[WEST].getBlock(x - CHUNK_WIDTH, y, z) : 0;
        }
        if (z < 0) {
            return neighbors[NORTH] != null ? neighbors[NORTH].getBlock(x, y, z + CHUNK_WIDTH) : 0;
        }
        if (z > CHUNK_WIDTH - 1) {
            return neighbors[SOUTH] != null ? neighbors[SOUTH].getBlock(x, y, z - CHUNK_WIDTH) : 0;
        }
        return voxels[x | y << 5 | z << 13];
    }

    public void setBlock(int voxel, int x, int y, int z) {
        setBlock(voxel, x, y, z, true);
    }

    public synchronized void setBlock(int voxel, int x, int y, int z, boolean updateChunk) {
        // Check if the block is inside the chunk
        if (y < 0 || y > CHUNK_HEIGHT - 1)
            return;

        if (x < 0 || z < 0 || x > CHUNK_WIDTH - 1 || z > CHUNK_WIDTH - 1) {
            world.setBlock(voxel, position.x * CHUNK_WIDTH + x, y, position.y * CHUNK_WIDTH + z, false);
            return;
        }

        voxels[x | y << 5 | z << 13] = (short) voxel;

        if (!updateChunk)
            return;

        var blockPosition = new Vector3i(x, y, z);
        Valkyrie.EVENT_HANDLER.process(new ChunkUpdateEvent(this, blockPosition));

        if (x == 0) Valkyrie.EVENT_HANDLER.process(new ChunkUpdateEvent(world.getChunk(position.x - 1, position.y), blockPosition));
        if (x == CHUNK_WIDTH - 1) Valkyrie.EVENT_HANDLER.process(new ChunkUpdateEvent(world.getChunk(position.x + 1, position.y), blockPosition));
        if (z == 0) Valkyrie.EVENT_HANDLER.process(new ChunkUpdateEvent(world.getChunk(position.x, position.y - 1), blockPosition));
        if (z == CHUNK_WIDTH - 1) Valkyrie.EVENT_HANDLER.process(new ChunkUpdateEvent(world.getChunk(position.x, position.y + 1), blockPosition));
        if (y % CHUNK_SECTION_HEIGHT == 0) Valkyrie.EVENT_HANDLER.process(new ChunkUpdateEvent(world.getChunk(position.x, position.y), new Vector3i(blockPosition.x, blockPosition.y - 1, blockPosition.z)));
        if ((y + 1) % CHUNK_SECTION_HEIGHT == 0) Valkyrie.EVENT_HANDLER.process(new ChunkUpdateEvent(world.getChunk(position.x, position.y), new Vector3i(blockPosition.x, blockPosition.y + 1, blockPosition.z)));
    }

    public void onDestroy() {
        // Unbinds the adjacent chunks from itself and marks them to update
        for (int i = 0; i < 4; i++) {
            Chunk neighbor = neighbors[i];
            if (neighbor == null)
                continue;

            neighbor.neighbors[(i + 2) % 4] = null;
            for (int j = 0; j < 8; j++) {
                Valkyrie.EVENT_HANDLER.process(new ChunkUpdateEvent(neighbor, new Vector3i(0, j * CHUNK_SECTION_HEIGHT, 0)));
            }
        }
    }

    public short[] getVoxels() {
        return voxels;
    }

    public ChunkState getState() {
        return state;
    }

    public void setState(ChunkState state) {
        this.state = state;
    }

    public byte[] getCompressedData() {
        return compressedData;
    }

    protected Chunk[] getNeighbors() {
        return neighbors;
    }

    public Vector2i getPosition() {
        return position;
    }

}

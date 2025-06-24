package me.lofienjoyer.valkyrie;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.*;

public class AudioEngine {

    private long device;
    private long context;

    public void init() {
        device = alcOpenDevice((ByteBuffer) null);
        if (device == 0) {
            throw new IllegalStateException("Failed to open OpenAL device.");
        }

        context = alcCreateContext(device, (IntBuffer) null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));
    }

    public void cleanup() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

}

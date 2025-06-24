package me.lofienjoyer.valkyrie;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;

public class SoundLoader {

    public static int loadSound(String filePath) {
        int buffer = alGenBuffers();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer error = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            IntBuffer sampleRate = stack.mallocInt(1);

            ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(filePath, channels, sampleRate);

            if (rawAudioBuffer == null) {
                throw new RuntimeException("Failed to load sound: " + filePath);
            }

            int format = channels.get(0) == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;

            alBufferData(buffer, format, rawAudioBuffer, sampleRate.get(0));
        }
        return buffer;
    }

}

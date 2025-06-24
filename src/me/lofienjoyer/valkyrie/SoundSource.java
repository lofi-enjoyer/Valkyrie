package me.lofienjoyer.valkyrie;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {

    private final int soundBuffer;
    private final int sourceId;

    public SoundSource(int soundBuffer) {
        this.soundBuffer = soundBuffer;
        sourceId = alGenSources();
        alSourcef(sourceId, AL_GAIN, 0.5f);
        alSourcef(sourceId, AL_PITCH, 1.0f);
        alSource3f(sourceId, AL_POSITION, 0, 0, 0);
    }

    public void play(float pitch) {
        alSourceStop(sourceId);
        alSourcei(sourceId, AL_BUFFER, soundBuffer);
        alSourcef(sourceId, AL_PITCH, pitch);
        alSourcePlay(sourceId);
    }

    public void cleanup() {
        alSourceStop(sourceId);
        alDeleteSources(sourceId);
    }

}

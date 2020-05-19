package com.morlunk.jumble.audio.encoder;

import android.util.Log;

import com.googlecode.javacpp.IntPointer;
import com.morlunk.jumble.audio.javacpp.RNNoise;
import com.morlunk.jumble.audio.javacpp.Speex;
import com.morlunk.jumble.exception.NativeAudioException;
import com.morlunk.jumble.net.PacketBuffer;

import org.bytedeco.javacpp.Pointer;

import java.nio.BufferUnderflowException;

public class RNNoiseEncoder implements IEncoder {
    private static final int SAMPLE_RATE = 48000;
    private static final int FRAME_SIZE = 480;

    private IEncoder mEncoder;
    private Pointer mPreprocessor;

    public RNNoiseEncoder(IEncoder encoder, int frameSize, int sampleRate) {
        // These are requirements by RNNoise.
        assert(sampleRate == SAMPLE_RATE);
        assert(frameSize == FRAME_SIZE);
        mEncoder = encoder;
        mPreprocessor = RNNoise.rnnoise_create();
    }

    @Override
    public int encode(short[] input, int inputSize) throws NativeAudioException {
        float[] denoiseFrames = new float[FRAME_SIZE];
        for (int i = 0; i < FRAME_SIZE; i++) {
            denoiseFrames[i] = input[i];
        }
        RNNoise.rnnoise_process_frame(mPreprocessor, denoiseFrames, denoiseFrames);
        for (int i = 0; i < FRAME_SIZE; i++) {
            input[i] = (short) denoiseFrames[i];
        }
        return mEncoder.encode(input, inputSize);
    }

    @Override
    public int getBufferedFrames() {
        return mEncoder.getBufferedFrames();
    }

    @Override
    public boolean isReady() {
        return mEncoder.isReady();
    }

    @Override
    public void getEncodedData(PacketBuffer packetBuffer) throws BufferUnderflowException {
        mEncoder.getEncodedData(packetBuffer);
    }

    @Override
    public void terminate() throws NativeAudioException {
        mEncoder.terminate();
    }

    public void setEncoder(IEncoder encoder) {
        if(mEncoder != null) mEncoder.destroy();
        mEncoder = encoder;
    }

    @Override
    public void destroy() {
        RNNoise.rnnoise_destroy(mPreprocessor);
        mEncoder.destroy();
        mPreprocessor = null;
        mEncoder = null;
    }
}

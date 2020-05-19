package com.morlunk.jumble.audio.javacpp;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Platform;

@Platform(library="jnirnnoise", cinclude={"<rnnoise.h>"})
public class RNNoise {
    static { Loader.load(); }

    public static native int rnnoise_get_size();
    public static native int rnnoise_init(@Cast("DenoiseState *") Pointer st);
    public static native Pointer rnnoise_create();
    public static native void rnnoise_destroy(@Cast("DenoiseState *") Pointer st);
    public static native float rnnoise_process_frame(@Cast("DenoiseState *") Pointer st, @Cast("float *") float[] out, @Cast("const float *") float[] in);
}

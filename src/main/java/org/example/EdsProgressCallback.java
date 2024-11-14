package org.example;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface EdsProgressCallback extends Callback {
    int invoke(int inPercent, Pointer inContext, int inStatus);
}

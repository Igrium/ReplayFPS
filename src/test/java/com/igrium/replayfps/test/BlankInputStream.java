package com.igrium.replayfps.test;

import java.io.InputStream;

/**
 * An input stream that always returns 0.
 */
public class BlankInputStream extends InputStream {

    @Override
    public int read() {
        return 0;
    }
    
}

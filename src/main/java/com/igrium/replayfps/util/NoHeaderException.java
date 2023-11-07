package com.igrium.replayfps.util;

public class NoHeaderException extends IllegalStateException {
    public NoHeaderException() {
        super("The header has not been initialized.");
    }

    public NoHeaderException(String message) {
        super(message);
    }
}

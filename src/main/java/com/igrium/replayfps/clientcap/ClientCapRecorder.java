package com.igrium.replayfps.clientcap;

import java.io.IOException;
import java.io.OutputStream;

public class ClientCapRecorder {
    protected final ClientCapFile file = new ClientCapFile();
    private final OutputStream outputStream;
    
    public ClientCapRecorder(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public ClientCapFile getFile() {
        return file;
    }

    public void writeHeader() throws IOException {
        file.writeHeader(outputStream);
    }
}

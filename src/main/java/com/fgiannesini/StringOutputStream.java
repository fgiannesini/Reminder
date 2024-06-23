package com.fgiannesini;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream {

    private final OutputStream outputStream;

    public StringOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeWithLineBreak(String toWrite) throws IOException {
        outputStream.write((toWrite + "\r\n").getBytes());
    }
}

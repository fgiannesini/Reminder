package com.fgiannesini;

import java.io.IOException;
import java.io.OutputStream;

public record StringOutputStream(OutputStream outputStream) {

    public void writeWithLineBreak(String toWrite) throws IOException {
        outputStream.write((toWrite + "\r\n").getBytes());
    }
}

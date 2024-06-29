package com.fgiannesini;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface StorageHandler {
    List<Word> load(Path csvFilePath) throws IOException;
}

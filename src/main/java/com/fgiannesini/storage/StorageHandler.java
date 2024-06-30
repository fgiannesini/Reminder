package com.fgiannesini.storage;

import com.fgiannesini.Word;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface StorageHandler {
    List<Word> load(Path csvFilePath) throws IOException;
}

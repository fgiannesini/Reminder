package com.fgiannesini;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface StorageHandler {
    List<Word> load(Path csvFilePath) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException;
}

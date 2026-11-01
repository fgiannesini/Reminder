package com.fgiannesini.web;

import com.fgiannesini.original.CsvOriginalWord;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.jspecify.annotations.Nullable;

@Configuration
@ImportRuntimeHints(NativeHints.Registrar.class)
public class NativeHints {

    static class Registrar implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
            hints.resources().registerPattern("dictionary.csv");
            hints.resources().registerPattern("static/*");
            hints.resources().registerResourceBundle("opencsv");
            hints.reflection().registerType(CsvOriginalWord.class, MemberCategory.values());
        }
    }
}

package com.fgiannesini.console;

import com.fgiannesini.Dictionary;
import com.fgiannesini.MemoryStorageHandler;
import com.fgiannesini.NextGenerator;
import com.fgiannesini.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class ReminderConsoleTest {

    private final Dictionary dictionary;

    ReminderConsoleTest() throws IOException {
        dictionary = new Dictionary(
                new NextGenerator(),
                new MemoryStorageHandler(
                        new Word("desligar", "éteindre", 0, null),
                        new Word("acender", "allumer", 0, null),
                        new Word("negar", "nier")
                )
        );
        dictionary.load(List.of(
                new Word("desligar", "éteindre", 0, null),
                new Word("acender", "allumer", 0, null),
                new Word("negar", "nier")
        ));
    }

    private static ByteArrayInputStream getInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void Should_validate_two_translations() throws IOException {
        var outputStream = new MockedOutputStream();
        ReminderConsole reminderConsole = new ReminderConsole(getInputStream("""
                eteindre
                allumer
                quit"""), outputStream);
        reminderConsole.run(dictionary);
        Assertions.assertEquals("""
                Reminder
                desligar
                CLOSED (éteindre)
                
                acender
                OK (allumer)
                
                negar
                Bye
                """, outputStream.getWrittenText());
    }

    @Test
    public void Should_reject_a_translation() throws IOException {
        var outputStream = new MockedOutputStream();
        ReminderConsole reminderConsole = new ReminderConsole(getInputStream("""
                allumer
                quit"""), outputStream);
        reminderConsole.run(dictionary);
        Assertions.assertEquals("""
                Reminder
                desligar
                KO (éteindre)
                
                acender
                Bye
                """, outputStream.getWrittenText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"quit", "exit", "Quit", "Exit"})
    public void Should_quit(String quitCommand) throws IOException {
        var outputStream = new MockedOutputStream();
        ReminderConsole reminderConsole = new ReminderConsole(getInputStream(quitCommand), outputStream);
        reminderConsole.run(dictionary);
        Assertions.assertEquals("""
                Reminder
                desligar
                Bye
                """, outputStream.getWrittenText());
    }

    @Test
    public void Should_learn_a_word() throws IOException {
        var storageHandler = new MemoryStorageHandler(
                new Word("desligar", "éteindre", 2, null),
                new Word("acender", "allumer")
        );
        Dictionary dictionary1 = new Dictionary(
                new NextGenerator(),
                storageHandler
        );
        dictionary1.load(List.of(
                new Word("desligar", "éteindre", 0, null),
                new Word("acender", "allumer", 0, null)
        ));
        var outputStream = new MockedOutputStream();
        ReminderConsole reminderConsole = new ReminderConsole(getInputStream("""
                éteindre
                quit"""), outputStream);

        reminderConsole.run(dictionary1);
        Assertions.assertEquals("""
                Reminder
                desligar
                OK (éteindre)
                
                Translation 'desligar -> éteindre' learned
                
                acender
                Bye
                """, outputStream.getWrittenText());
    }

    private static class MockedOutputStream extends OutputStream {

        private final List<String> written = new ArrayList<>();

        @Override
        public void write(int i) {

        }

        @Override
        public void write(byte[] b) {
            written.add(new String(b, StandardCharsets.UTF_8));
        }

        public String getWrittenText() {
            return String.join("", written);
        }
    }
}
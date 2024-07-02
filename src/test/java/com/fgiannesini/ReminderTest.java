package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class ReminderTest {

    private final Dictionary dictionary;

    ReminderTest() throws IOException {
        dictionary = new Dictionary(
                new NextGenerator(),
                new MemoryStorageHandler(
                        new Word("desligar", "éteindre"),
                        new Word("acender", "allumer"),
                        new Word("negar", "nier")
                )
        );
    }

    private static ByteArrayInputStream getInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void Should_validate_two_translations() throws IOException {
        var outputStream = new MockedOutputStream();
        Reminder reminder = new Reminder(getInputStream("""
                eteindre
                allumer
                quit"""), outputStream);
        reminder.run(dictionary);
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
        Reminder reminder = new Reminder(getInputStream("""
                allumer
                quit"""), outputStream);
        reminder.run(dictionary);
        Assertions.assertEquals("""
                Reminder
                desligar
                KO (éteindre)
                                
                acender
                Bye
                """, outputStream.getWrittenText());
    }

    @Test
    public void Should_quit() throws IOException {
        var outputStream = new MockedOutputStream();
        Reminder reminder = new Reminder(getInputStream("quit"), outputStream);
        reminder.run(dictionary);
        Assertions.assertEquals(outputStream.getWrittenText(), """
                Reminder
                desligar
                Bye
                """);
    }

    @Test
    public void Should_learn_a_word() throws IOException {
        var storageHandler = new MemoryStorageHandler(
                new Word("desligar", "éteindre", 4, null),
                new Word("acender", "allumer"),
                new Word("negar", "nier")
        );

        var outputStream = new MockedOutputStream();
        Reminder reminder = new Reminder(getInputStream("""
                éteindre
                quit"""), outputStream);
        reminder.run(new Dictionary(
                new NextGenerator(),
                storageHandler
        ));
        Assertions.assertEquals("""
                Reminder
                desligar
                OK (éteindre)
                                
                Translation 'desligar -> éteindre' learned
                                
                negar
                Bye
                """, outputStream.getWrittenText());
        Assertions.assertEquals(storageHandler.saveCallsCount(), 1);
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
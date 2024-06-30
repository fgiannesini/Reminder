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

    private final Dictionary dictionary = new Dictionary(new NextGenerator(), List.of(new Word("desligar", "éteindre"), new Word("acender", "allumer"), new Word("negar", "nier")));

    private static ByteArrayInputStream getInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes());
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
                OK
                                
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
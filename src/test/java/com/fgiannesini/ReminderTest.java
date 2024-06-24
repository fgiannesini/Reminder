package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

class ReminderTest {


    private final Words dictionary = new Words(new NextGenerator(), new Word("desligar", "éteindre"), new Word("acender", "allumer"));

    private static ByteArrayInputStream getInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes());
    }

    @Test
    public void Should_validate_a_translation_from_portugues_to_french() throws IOException {
        var outputStream = new MockedOutputStream();
        Reminder reminder = new Reminder(getInputStream("""
                éteindre
                quit"""), outputStream);
        reminder.run(dictionary);
        Assertions.assertEquals(outputStream.getWrittenText(), """
                Reminder
                desligar
                OK
                acender
                Bye
                """);
    }

    @Test
    public void Should_validate_two_translations_from_portugues_to_french() throws IOException {
        var outputStream = new MockedOutputStream();
        Reminder reminder = new Reminder(getInputStream("""
                éteindre
                allumer
                quit"""), outputStream);
        reminder.run(dictionary);
        Assertions.assertEquals(outputStream.getWrittenText(), """
                Reminder
                desligar
                OK
                acender
                OK
                """);
    }

    @Test
    public void Should_reject_a_translation_from_portugues_to_french() throws IOException {
        var outputStream = new MockedOutputStream();
        Reminder reminder = new Reminder(getInputStream("""
                allumer
                quit"""), outputStream);
        reminder.run(dictionary);
        Assertions.assertEquals(outputStream.getWrittenText(), """
                Reminder
                desligar
                KO (éteindre)
                acender
                Bye
                """);
    }

    @Test
    public void Should_quit_if_asked() throws IOException {
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
            written.add(new String(b));
        }

        public String getWrittenText() {
            return String.join("", written);
        }
    }
}
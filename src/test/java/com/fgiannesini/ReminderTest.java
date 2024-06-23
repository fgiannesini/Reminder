package com.fgiannesini;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;

class ReminderTest {

    @Test
    public void Should_validate_a_translation_from_portugues_to_french() throws IOException {
        var outputStream = Mockito.mock(StringOutputStream.class);
        Reminder reminder = new Reminder(getInputStream("""
                éteindre
                quit"""), outputStream);
        reminder.run();
        Mockito.verify(outputStream).writeWithLineBreak("Reminder");
        Mockito.verify(outputStream).writeWithLineBreak("desligar");
        Mockito.verify(outputStream).writeWithLineBreak("OK");
    }

    private static ByteArrayInputStream getInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes());
    }

    @Test
    public void Should_validate_two_translations_from_portugues_to_french() throws IOException {
        var outputStream = Mockito.mock(StringOutputStream.class);
        Reminder reminder = new Reminder(getInputStream("""
                éteindre
                allumer
                quit"""), outputStream);
        reminder.run();
        Mockito.verify(outputStream).writeWithLineBreak("Reminder");
        Mockito.verify(outputStream).writeWithLineBreak("desligar");
        Mockito.verify(outputStream, Mockito.times(2)).writeWithLineBreak("OK");
        Mockito.verify(outputStream).writeWithLineBreak("acender");
    }

    @Test
    public void Should_reject_a_translation_from_portugues_to_french() throws IOException {
        var outputStream = Mockito.mock(StringOutputStream.class);
        Reminder reminder = new Reminder(getInputStream("""
                allumer
                quit"""), outputStream);
        reminder.run();
        Mockito.verify(outputStream).writeWithLineBreak("Reminder");
        Mockito.verify(outputStream).writeWithLineBreak("desligar");
        Mockito.verify(outputStream).writeWithLineBreak("KO (éteindre)");
    }

    @Test
    public void Should_quit_if_asked() throws IOException {
        var outputStream = Mockito.mock(StringOutputStream.class);
        Reminder reminder = new Reminder(getInputStream("quit"), outputStream);
        reminder.run();
        Mockito.verify(outputStream).writeWithLineBreak("Reminder");
        Mockito.verify(outputStream).writeWithLineBreak("desligar");
        Mockito.verify(outputStream).writeWithLineBreak("Bye");
    }
}
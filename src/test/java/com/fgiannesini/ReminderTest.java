package com.fgiannesini;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

class ReminderTest {

    @Test
    public void Should_validate_a_translation_from_portugues_to_french() throws IOException {
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Reminder reminder = new Reminder(new ByteArrayInputStream("éteindre\r\nquit".getBytes()), outputStream);
        reminder.run();
        Mockito.verify(outputStream).write("Reminder\r\n".getBytes());
        Mockito.verify(outputStream).write("desligar\r\n".getBytes());
        Mockito.verify(outputStream).write("OK\r\n".getBytes());
    }

    @Test
    public void Should_validate_two_translations_from_portugues_to_french() throws IOException {
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Reminder reminder = new Reminder(new ByteArrayInputStream("éteindre\r\nallumer\r\nquit".getBytes()), outputStream);
        reminder.run();
        Mockito.verify(outputStream).write("Reminder\r\n".getBytes());
        Mockito.verify(outputStream).write("desligar\r\n".getBytes());
        Mockito.verify(outputStream, Mockito.times(2)).write("OK\r\n".getBytes());
        Mockito.verify(outputStream).write("acender\r\n".getBytes());
    }

    @Test
    public void Should_reject_a_translation_from_portugues_to_french() throws IOException {
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Reminder reminder = new Reminder(new ByteArrayInputStream("allumer\r\nquit".getBytes()), outputStream);
        reminder.run();
        Mockito.verify(outputStream).write("Reminder\r\n".getBytes());
        Mockito.verify(outputStream).write("desligar\r\n".getBytes());
        Mockito.verify(outputStream).write("KO (éteindre)\r\n".getBytes());
    }

    @Test
    public void Should_quit_if_asked() throws IOException {
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Reminder reminder = new Reminder(new ByteArrayInputStream("quit".getBytes()), outputStream);
        reminder.run();
        Mockito.verify(outputStream).write("Reminder\r\n".getBytes());
        Mockito.verify(outputStream).write("desligar\r\n".getBytes());
        Mockito.verify(outputStream).write("Bye\r\n".getBytes());
    }
}
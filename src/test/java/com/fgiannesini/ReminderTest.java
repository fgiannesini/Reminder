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
        new Reminder(new ByteArrayInputStream("éteindre".getBytes()), outputStream);
        Mockito.verify(outputStream).write("Reminder\r\n".getBytes());
        Mockito.verify(outputStream).write("desligar\r\n".getBytes());
        Mockito.verify(outputStream).write("OK\r\n".getBytes());
    }

    @Test
    public void Should_reject_a_translation_from_portugues_to_french() throws IOException {
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Reminder reminder = new Reminder(new ByteArrayInputStream("allumer".getBytes()), outputStream);
        Mockito.verify(outputStream).write("Reminder\r\n".getBytes());
        Mockito.verify(outputStream).write("desligar\r\n".getBytes());
        Mockito.verify(outputStream).write("KO (éteindre)\r\n".getBytes());
    }
}
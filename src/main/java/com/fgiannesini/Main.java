package com.fgiannesini;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException {
        Reminder reminder = new Reminder(System.in, System.out);
        reminder.run(new Words(
                new SecureRandom(new Date().toString().getBytes()),
                new Word("desligar", "éteindre"),
                new Word("acender", "allumer"),
                new Word("gestar", "dépenser"),
                new Word("poupar", "économiser"),
                new Word("reparar", "réparer"),
                new Word("arrumar", "ranger"),
                new Word("empurrar", "pousser"),
                new Word("conseguir", "réussir"),
                new Word("ou seja", "c'est à dire"),
                new Word("pertencer", "appartenir")
        ));
    }
}
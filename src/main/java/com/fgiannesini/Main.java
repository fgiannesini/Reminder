package com.fgiannesini;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException {
        Reminder reminder = new Reminder(System.in, System.out);
        reminder.run(new Words(
                new SecureRandom(new Date().toString().getBytes()),
                new Word("eteindre", "desligar"),
                new Word("allumer", "acender"),
                new Word("depenser", "gastar"),
                new Word("economiser", "poupar"),
                new Word("reparar", "reparer"),
                new Word("arrumar", "ranger"),
                new Word("empurrar", "pousser"),
                new Word("vencer,ganhar,derrotar", "vaincre"),
                new Word("saltar,pular", "sauter"),
                new Word("conseguir", "reussir"),
                new Word("ao inves, em vez de", "au lieu de"),
                new Word("ou seja", "c'est à dire"),
                new Word("pertencer", "appartenir"),
                new Word("gozar", "se moquer"),
                new Word("brincar", "jouer"),
                new Word("boneca", "poupée"),
                new Word("tesora", "ciseaux"),
                new Word("aguentar", "tenir"),
                new Word("eu nao aguento mais", "je n'en peux plus"),
                new Word("estacionar", "garer"),
                new Word("dirigir", "conduire"),
                new Word("apoiar, sustentar", "soutenir"),
                new Word("acampar", "camper"),
                new Word("assar", "cuire"),
                new Word("fritar", "faire cuire"),
                new Word("ferver", "bouillir"),
                new Word("panela", "casserole"),
                new Word("secar", "secher"),
                new Word("cruzar", "traverser"),
                new Word("explorar", "exploiter"),
                new Word("merecer", "meriter"),
                new Word("eu costumo", "j'ai l'habitude"),
                new Word("recusar", "refuser"),
                new Word("girar, virar", "tourner"),
                new Word("descansar", "se reposer"),
                new Word("morar", "habiter"),
                new Word("aumentar", "augmenter"),
                new Word("conferir", "confirmer, verifier"),
                new Word("arrumar", "ranger"),
                new Word("concertar", "reparer"),
                new Word("negar", "nier"),
                new Word("desenvolver", "developper"),
                new Word("somente", "seulement"),
                new Word("ralada", "râpé")
        ));
    }
}
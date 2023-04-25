package com.astrog.shootergame.common.messaging;

import javafx.util.Pair;

public class MessageFormatter {

    public static String formatMessage(String event, String args) {
        return event + ':' + args;
    }

    public static String formatMessage(String event) {
        return event + ": ";
    }

    public static Pair<String, String> reformatMessage(String message) {
        String[] strings = message.split(":", 2);
        return new Pair<>(strings[0], strings[1]);
    }
}

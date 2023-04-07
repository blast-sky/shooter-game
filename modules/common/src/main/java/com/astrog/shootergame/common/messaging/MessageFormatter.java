package com.astrog.shootergame.common.messaging;

public class MessageFormatter {

    public static String formatMessage(String event, String args) {
        return event + ':' + args;
    }

    public static String formatMessage(String event) {
        return event;
    }
}

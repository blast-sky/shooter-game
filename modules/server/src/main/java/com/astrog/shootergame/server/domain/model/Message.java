package com.astrog.shootergame.server.domain.model;

import com.astrog.shootergame.common.socket.Client;

public record Message(
    Client from,
    String event,
    String args
) {
    public Message(Client from, String event) {
        this(from, event, "");
    }
}

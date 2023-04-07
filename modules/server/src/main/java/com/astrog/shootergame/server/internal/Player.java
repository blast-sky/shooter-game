package com.astrog.shootergame.server.internal;

import com.astrog.shootergame.common.socket.Client;

public record Player(
    Client client,
    String name
) {
}

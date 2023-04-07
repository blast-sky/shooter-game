package com.astrog.shootergame.server.domain.messaging;

import com.astrog.shootergame.common.socket.Client;
import com.astrog.shootergame.server.domain.model.Message;

public class MessageSerializer {

    public static Message deserializeRequest(Client client, String message) {
        String[] split = message.split(":");
        String event = split[0];
        if (split.length < 2) {
            return new Message(client, event, "");
        }
        String args = split[1];
        return new Message(client, event, args);
    }
}

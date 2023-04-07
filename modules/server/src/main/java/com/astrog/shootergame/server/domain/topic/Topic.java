package com.astrog.shootergame.server.domain.topic;

import com.astrog.shootergame.common.socket.Client;

import java.util.ArrayList;
import java.util.List;

public class Topic {

    private final List<Client> listeners = new ArrayList<>();
    private String lastMessage = null;

    public void addListener(Client client) {
        listeners.add(client);
    }

    public void broadcast(String message) {
        lastMessage = message;
        for (Client listener : listeners) {
            listener.printMessage(message);
        }
    }
}

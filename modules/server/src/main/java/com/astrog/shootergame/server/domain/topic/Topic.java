package com.astrog.shootergame.server.domain.topic;

import com.astrog.shootergame.common.socket.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Topic {

    private final List<Client> listeners = new ArrayList<>();
    private String lastMessage = null;

    public void addListener(Client client) {
        listeners.add(client);
        Optional.ofNullable(lastMessage)
            .ifPresent(client::print);
    }

    public void broadcast(String message) {
        lastMessage = message;
        for (Client listener : listeners) {
            listener.print(message);
        }
    }
}

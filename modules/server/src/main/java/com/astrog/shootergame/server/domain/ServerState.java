package com.astrog.shootergame.server.domain;

import com.astrog.shootergame.common.socket.Client;

public interface ServerState {

    default void onConnection(Client client) {
    }

    default void onLogin(Client client, String name) {
    }

    default void onShoot(Client client) {
    }

    default void onTakePause(Client client) {
    }

    default void onDisconnection(Client client) {
    }

    default void onStartGame(Client client) {
    }

    default void onStart() {
    }

    default void onEnd() {
    }

    boolean isEnded();

    ServerState getNextState();
}

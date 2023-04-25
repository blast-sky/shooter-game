package com.astrog.shootergame.server.internal.state;

import com.astrog.shootergame.common.socket.Client;
import com.astrog.shootergame.server.domain.ServerState;
import com.astrog.shootergame.server.internal.Player;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.astrog.shootergame.common.Constants.MAX_CLIENT_IN_PARTY_COUNT;
import static com.astrog.shootergame.common.messaging.CustomEvent.RESPONSE_LOGIN;
import static com.astrog.shootergame.common.messaging.CustomEvent.PARTY_COMPLETE;
import static com.astrog.shootergame.common.messaging.MessageFormatter.formatMessage;
import static com.astrog.shootergame.common.messaging.serialization.ObjectToStringSerializer.serialize;

@RequiredArgsConstructor
public class InvitePlayersToPartyServerState implements ServerState {

    private final List<Player> players;

    public InvitePlayersToPartyServerState() {
        players = new ArrayList<>();
    }

    @Override
    public void onLogin(Client client, String name) {
        if (players.stream().anyMatch(player -> player.name().equals(name))) {
            client.print(formatMessage(RESPONSE_LOGIN.name(), serialize(false)));
            return;
        }

        System.out.println("LOGIN:" + name + ':' + (players.size() + 1) + '/' + MAX_CLIENT_IN_PARTY_COUNT);

        players.add(new Player(client, name));
        client.print(formatMessage(RESPONSE_LOGIN.name(), serialize(true)));
    }

    @Override
    public void onEnd() {
        players.forEach(player -> player.client().print(formatMessage(PARTY_COMPLETE.name())));
    }

    @Override
    public boolean isEnded() {
        return players.size() >= MAX_CLIENT_IN_PARTY_COUNT;
    }

    @Override
    public ServerState getNextState() {
        return new GamePlayServerState(players);
    }
}

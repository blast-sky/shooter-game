package com.astrog.shootergame.server.internal.state;

import com.astrog.shootergame.common.socket.Client;
import com.astrog.shootergame.server.domain.ServerState;
import com.astrog.shootergame.server.internal.Player;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.astrog.shootergame.common.Constants.MAX_CLIENT_IN_PARTY_COUNT;
import static com.astrog.shootergame.common.Constants.ServerMessages.LOGIN_SUCCESS;
import static com.astrog.shootergame.common.Constants.ServerMessages.NAME_ALREADY_EXIST;
import static com.astrog.shootergame.common.Constants.ServerMessages.PARTY_COMPLETE;

@RequiredArgsConstructor
public class InvitePlayersToPartyServerState implements ServerState {

    private final List<Player> players;

    public InvitePlayersToPartyServerState() {
        players = new ArrayList<>();
    }

    @Override
    public void onLogin(Client client, String name) {
        if (players.stream().anyMatch(player -> player.name().equals(name))) {
            client.printMessage(NAME_ALREADY_EXIST.name());
            client.disconnect();
            return;
        }

        System.out.println("LOGIN:" + name + ':' + (players.size() + 1) + '/' + MAX_CLIENT_IN_PARTY_COUNT);

        players.add(new Player(client, name));
        client.printMessage(LOGIN_SUCCESS.name());
    }

    @Override
    public void onEnd() {
        players.forEach(player -> player.client().printMessage(PARTY_COMPLETE.name()));
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

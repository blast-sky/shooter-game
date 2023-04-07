package com.astrog.shootergame.client.connection;

import com.astrog.shootergame.common.socket.Client;
import com.astrog.shootergame.common.Constants.ServerMessages;
import com.astrog.shootergame.common.messaging.CustomEvent;
import com.astrog.shootergame.common.messaging.MessageFormatter;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.Socket;
import java.util.List;
import java.util.Optional;

import static com.astrog.shootergame.common.messaging.ObjectToStringSerializer.deserialize;

@RequiredArgsConstructor
public class ShooterGameClient {

    private final String ip;
    private final int port;
    @Getter
    private Client client = null;

    public void startGame() {
        Optional.ofNullable(client)
            .ifPresent(cli -> cli.printMessage(CustomEvent.START_GAME.name()));
    }

    public void takePause() {
        Optional.ofNullable(client)
            .ifPresent(cli -> cli.printMessage(CustomEvent.TAKE_PAUSE.name()));
    }

    public void makeShoot() {
        Optional.ofNullable(client)
            .ifPresent(cli -> cli.printMessage(CustomEvent.SHOOT.name()));
    }

    @SneakyThrows
    public boolean connectAndTryLogin(String name) {
        client = new Client(new Socket(ip, port));
        client.printMessage(MessageFormatter.formatMessage(CustomEvent.LOGIN.name(), name));
        String response = client.getNextMessage();
        return response.equals(ServerMessages.LOGIN_SUCCESS.name());
    }

    public List<String> getAllPlayerNames() {
        String message = client.getNextMessage();
        return deserialize(message, new TypeToken<List<String>>(){}.getType());
    }
}

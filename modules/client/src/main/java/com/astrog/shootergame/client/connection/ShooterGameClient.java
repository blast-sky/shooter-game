package com.astrog.shootergame.client.connection;

import com.astrog.shootergame.common.lambda.LambdaStringParameter;
import com.astrog.shootergame.common.messaging.CustomEvent;
import com.astrog.shootergame.common.socket.Client;
import javafx.util.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.astrog.shootergame.common.messaging.MessageFormatter.formatMessage;
import static com.astrog.shootergame.common.messaging.MessageFormatter.reformatMessage;

@RequiredArgsConstructor
public class ShooterGameClient extends SubscriptionResolver<CustomEvent> {

    private final String ip;
    private final int port;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService messageDispatcher = Executors.newSingleThreadScheduledExecutor();
    @Getter
    private volatile Client client;

    {
        messageDispatcher.scheduleWithFixedDelay(this::receiveAndDispatchMessages, 5, 5, TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    private void receiveAndDispatchMessages() {
        try {
            if (client != null && client.isOpened()) {
                String nextMessage = client.getNextMessage();
                Pair<String, String> message = reformatMessage(nextMessage);
                if (!handleEvent(CustomEvent.valueOf(message.getKey()), message.getValue())) {
                    messageQueue.put(nextMessage);
                }
            } else {
                Thread.sleep(1000);
            }
        } catch (SocketTimeoutException ignore) {
        }
    }

    public void requestStartGame() {
        Optional.ofNullable(client).ifPresent(cli -> cli.print(CustomEvent.START_GAME.name()));
    }

    public void requestTakePause() {
        Optional.ofNullable(client).ifPresent(cli -> cli.print(CustomEvent.TAKE_PAUSE.name()));
    }

    public void requestMakeShoot() {
        Optional.ofNullable(client).ifPresent(cli -> cli.print(CustomEvent.SHOOT.name()));
    }

    @SneakyThrows
    public void connectIfNotAlreadyConnected() {
        if (!isConnected()) {
            client = new Client(new Socket(ip, port));
            client.setSoTimeout(2000);
        }
    }

    public boolean isConnected() {
        return client != null && client.isOpened();
    }

    @SneakyThrows
    public void requestLogin(String name) {
        client.print(formatMessage(CustomEvent.REQUEST_LOGIN.name(), name));
    }

    public void requestLeaders() {
        client.print(CustomEvent.GET_LEADER_TABLE.name());
    }

    @SneakyThrows
    public void subscribeOnEvent(CustomEvent event, LambdaStringParameter lambda) {
        super.subscribeOnEvent(event, lambda);
        runLambdaIfEventInMessageQueue(event);
    }

    private void runLambdaIfEventInMessageQueue(CustomEvent event) {
        messageQueue.stream()
            .filter(message -> message.contains(event.name()))
            .findFirst()
            .ifPresent(message -> {
                messageQueue.remove(message);
                handleEvent(event, reformatMessage(message).getValue());
            });
    }

    @SneakyThrows
    public void stopListening() {
        messageDispatcher.shutdownNow();
        messageDispatcher.awaitTermination(100, TimeUnit.SECONDS);
    }
}

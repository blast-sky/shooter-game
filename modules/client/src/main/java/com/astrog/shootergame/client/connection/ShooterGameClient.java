package com.astrog.shootergame.client.connection;

import com.astrog.shootergame.common.lambda.LambdaStringParameter;
import com.astrog.shootergame.common.messaging.CustomEvent;
import com.astrog.shootergame.common.socket.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.astrog.shootergame.common.messaging.MessageFormatter.formatMessage;
import static com.astrog.shootergame.common.messaging.MessageFormatter.reformatMessage;

@RequiredArgsConstructor
public class ShooterGameClient {

    private final String ip;
    private final int port;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final Map<String, LambdaStringParameter> subscribers = new ConcurrentHashMap<>();
    private final Set<String> onceSubscribers = new HashSet<>();
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
                var message = reformatMessage(nextMessage);
                var subscriber = subscribers.get(message.getKey());
                if (subscriber != null) {
                    if (onceSubscribers.contains(message.getKey())) {
                        onceSubscribers.remove(message.getKey());
                        subscribers.remove(message.getKey());
                    }
                    subscriber.run(message.getValue());
                } else {
                    messageQueue.put(nextMessage);
                }
            } else {
                Thread.sleep(1000);
            }
        } catch (SocketTimeoutException exception) {
            System.out.println("Reader timeout");
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
        if(!isConnected()) {
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

    public synchronized void removeSubscription(CustomEvent event) {
        subscribers.remove(event.name());
    }

    public void subscribeOnEventOnce(CustomEvent event, LambdaStringParameter lambda) {
        onceSubscribers.add(event.name());
        subscribeOnEvent(event, lambda);
    }

    @SneakyThrows
    public void subscribeOnEvent(CustomEvent event, LambdaStringParameter lambda) {
        synchronized (this) {
            subscribers.put(event.name(), lambda);
        }
        runLambdaIfEventInMessageQueue(event, lambda);
    }

    private void runLambdaIfEventInMessageQueue(CustomEvent event, LambdaStringParameter lambda) {
        messageQueue.stream()
            .filter(message -> message.contains(event.name()))
            .findFirst()
            .ifPresent(message -> {
                messageQueue.remove(message);
                onceSubscribers.remove(event.name());
                lambda.run(reformatMessage(message).getValue());
            });
    }

    @SneakyThrows
    public void stopListening() {
        messageDispatcher.shutdownNow();
        messageDispatcher.awaitTermination(100, TimeUnit.SECONDS);
    }
}

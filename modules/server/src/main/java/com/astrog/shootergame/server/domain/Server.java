package com.astrog.shootergame.server.domain;

import com.astrog.shootergame.common.messaging.Event;
import com.astrog.shootergame.common.socket.Client;
import com.astrog.shootergame.server.domain.messaging.MessageQueue;
import com.astrog.shootergame.server.domain.messaging.MessageSerializer;
import com.astrog.shootergame.server.domain.model.Message;
import com.astrog.shootergame.server.internal.ShooterGameRestController;
import com.astrog.shootergame.server.internal.state.InvitePlayersToPartyServerState;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.ServerSocket;

final public class Server {

    private final ServerSocket serverSocket;
    private final MessageQueue messageQueue;
    private final RestController controller;

    @SneakyThrows
    public Server(int port) {
        serverSocket = new ServerSocket(port);
        messageQueue = new MessageQueue();
        ShooterGameRestController.currentController =
            new ShooterGameRestController(new InvitePlayersToPartyServerState());
        controller = ShooterGameRestController.currentController;
    }

    public void run() {
        System.out.println("Server starting to listen connections...");
        Thread waitConnections = new Thread(this::waitConnectionsEndlessly);
        waitConnections.start();
        System.out.println("Server starting to process messages...");
        Thread processMessageQueue = new Thread(this::processMessageQueueEndlessly);
        processMessageQueue.start();

        try {
            waitConnections.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        try {
            processMessageQueue.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private void processMessageQueueEndlessly() {
        while (!serverSocket.isClosed()) {
            Message request = messageQueue.takeBlocking();
            controller.dispatch(request);
        }
    }

    @SneakyThrows
    private void waitConnectionsEndlessly() {
        while (!serverSocket.isClosed()) {
            Client client = new Client(serverSocket.accept());
            startHandleClientMessageInThread(client);
        }
    }

    private void startHandleClientMessageInThread(Client client) {
        new Thread(() -> {
            messageQueue.put(new Message(client, Event.CONNECTION.name()));
            try {
                while (client.isOpened()) {
                    String line = client.getNextMessage();
                    messageQueue.put(MessageSerializer.deserializeRequest(client, line));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            messageQueue.put(new Message(client, Event.DISCONNECTION.name()));
        }).start();
    }
}

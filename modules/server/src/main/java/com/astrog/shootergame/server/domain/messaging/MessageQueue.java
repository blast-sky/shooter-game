package com.astrog.shootergame.server.domain.messaging;

import com.astrog.shootergame.server.domain.model.Message;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

final public class MessageQueue {

    private final BlockingDeque<Message> messageQueue = new LinkedBlockingDeque<>();

    @SneakyThrows
    public void put(Message message) {
        messageQueue.putLast(message);
    }

    @SneakyThrows
    public Message takeBlocking() {
        return messageQueue.take();
    }

    public boolean isEmpty() {
        return messageQueue.isEmpty();
    }

    @SneakyThrows
    private Queue<Message> takeFirstNMessages(int n) {
        Queue<Message> messagesPart = new LinkedList<>();
        do {
            messagesPart.add(messageQueue.take());
        } while (messagesPart.size() < n && !messageQueue.isEmpty());
        return messagesPart;
    }
}

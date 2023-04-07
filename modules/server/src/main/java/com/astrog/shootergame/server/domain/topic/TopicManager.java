package com.astrog.shootergame.server.domain.topic;

import com.astrog.shootergame.common.socket.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TopicManager {

    private final Map<String, Topic> topics = new HashMap<>();

    public void createTopic(String endpoint, List<Client> listeners) {
        Topic topic = new Topic();
        topics.put(endpoint, topic);
        listeners.forEach(topic::addListener);
    }

    public void pushMessage(String endpoint, String message) {
        var topic = Optional.ofNullable(topics.get(endpoint));
        topic.ifPresent(top -> top.broadcast(message));
    }

    public void removeTopic(String endpoint) {
        topics.remove(endpoint);
    }
}

package com.astrog.shootergame.client.connection;

import com.astrog.shootergame.common.lambda.LambdaStringParameter;
import com.astrog.shootergame.common.messaging.CustomEvent;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class SubscriptionResolver<Event> {
    private final Map<Event, LambdaStringParameter> subscribers = new ConcurrentHashMap<>();
    private final Set<Event> subscriberNamesToRemoveAfterEvent = new CopyOnWriteArraySet<>();

    public void subscribeOnEventOnce(Event event, LambdaStringParameter lambda) {
        subscriberNamesToRemoveAfterEvent.add(event);
        subscribeOnEvent(event, lambda);
    }

    public void removeSubscription(Event event) {
        subscribers.remove(event);
        subscriberNamesToRemoveAfterEvent.remove(event);
    }

    @SneakyThrows
    public void subscribeOnEvent(Event event, LambdaStringParameter lambda) {
        subscribers.put(event, lambda);
    }

    /***
     * @return true if almost one subscriber is invoked
     */
    public boolean handleEvent(Event event, String args) {
        LambdaStringParameter subscriber = subscribers.get(event);
        if (subscriber != null) {
            if (subscriberNamesToRemoveAfterEvent.contains(event)) {
                subscriberNamesToRemoveAfterEvent.remove(event);
                subscribers.remove(event);
            }
            subscriber.run(args);
        }
        return subscriber != null;
    }
}

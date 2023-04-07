package com.astrog.shootergame.server.domain;

import com.astrog.shootergame.server.domain.model.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Dispatcher {

    @NonNull
    private final String dispatchedEvent;

    /***
     * @return boolean that mark need to continue dispatching.
     * If true - next dispatchers will process this message.
     */
    protected abstract boolean dispatch(Message message);

    public final boolean dispatchEvent(Message message) {
        if (!dispatchedEvent.equals(message.event()))
            return true;

        return dispatch(message);
    }
}

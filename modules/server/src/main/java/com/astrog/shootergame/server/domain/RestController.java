package com.astrog.shootergame.server.domain;

import com.astrog.shootergame.server.domain.model.Message;

import java.util.List;

public abstract class RestController {

    protected abstract List<Dispatcher> getDispatchers();

    public void dispatch(Message message) {
        for (var dispatcher : getDispatchers()) {
            boolean needToContinue = dispatcher.dispatchEvent(message);
            if (!needToContinue) break;
        }
    }
}

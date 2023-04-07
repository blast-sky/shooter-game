package com.astrog.shootergame.server.domain.messaging;

import com.astrog.shootergame.server.domain.model.Message;

public interface MessageAppender {
    void append(Message message);
}

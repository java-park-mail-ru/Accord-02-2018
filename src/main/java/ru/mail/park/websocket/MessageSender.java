package ru.mail.park.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.io.IOException;


public class MessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);
    private final ObjectMapper mapper;

    public MessageSender() {
        mapper = new ObjectMapper();
    }

    private <T> TextMessage getMessage(T messageForSend) {
        try {
            final String result = mapper.writeValueAsString(messageForSend);
            return (new TextMessage(result));
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    public <T> boolean send(@Nullable WebSocketSession session, @NotNull T messageForSend) {
        try {
            if (session == null) {
                LOGGER.info("Abort sending message to null session");
                return false;
            }

            session.sendMessage(getMessage(messageForSend));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

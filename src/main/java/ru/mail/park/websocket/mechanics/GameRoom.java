package ru.mail.park.websocket.mechanics;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import ru.mail.park.dao.UserDAO;
import ru.mail.park.websocket.Config;
import ru.mail.park.websocket.models.ClientRequestData;
import ru.mail.park.websocket.MessageSender;
import ru.mail.park.websocket.models.Homer;
import ru.mail.park.websocket.models.Player;


import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class GameRoom {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameRoom.class);

    private final @NotNull Player first;
    private final @NotNull Player second;
    private final @NotNull Homer homer;
    @SuppressWarnings("FieldCanBeLocal")
    private final MessageSender sender;
    private final GameLoop gameLoop;
    private final Thread thread;


    public GameRoom(WebSocketSession session1, WebSocketSession session2, UserDAO userService) {
        this.first = new Player((Long) session1.getAttributes().get("UserID"), session1, true);
        this.second = new Player((Long) session2.getAttributes().get("UserID"), session2, false);
        this.homer = new Homer();

        sender = new MessageSender();
        gameLoop = new GameLoop(this);
        thread = new Thread(gameLoop);
        thread.start();
    }

    public @NotNull Player getFirstPlayer() {
        return first;
    }

    public @NotNull Player getSecondPlayer() {
        return second;
    }

    public @NotNull List<Player> getPlayers() {
        return Arrays.asList(first, second);
    }

    public @NotNull Player getEnemy(@NotNull long userId) {
        if (userId == first.getUserID()) {
            return second;
        }

        if (userId == second.getUserID()) {
            return first;
        }

        throw new IllegalArgumentException("Requested enemy for game but user not participant");
    }


    public <T> void sendChanges(T message, WebSocketSession session) {
        sender.send(session, message);
    }

    public void updatePlayersState(ClientRequestData newState, Long userID) {
        if (userID.equals(first.getUserID())) {
            updatePlayerState(first, newState);

            // отправим данные для отображения
            // второму игроку
            sendChanges(first.toJSON().toString(), second.getSession());
        } else {
            updatePlayerState(second, newState);

            // отправим данные для отображения
            // первому игроку
            sendChanges(second.toJSON().toString(), first.getSession());
        }
    }

    public void updateHomerState(long time) {
        TaskRunner.updateHomerState(homer, time);
    }

    public void sendHomerState() {
        sendChanges(homer.toJSON().toString(), first.getSession());
        sendChanges(homer.toJSON().toString(), second.getSession());
    }

    public boolean tryFinishGame() {
        if (first.getScore() >= Config.SCORES_TO_WIN || second.getScore() >= Config.SCORES_TO_WIN) {
            return true;
        }

        return false;
    }

    public void stopGame() {
        gameLoop.stop();

        notePlayers();

        // закрываем сессию
        // первому игроку
        try {
            if (first.getSession().isOpen()) {
                first.getSession().close();
            }
        } catch (IOException e) {
            LOGGER.warn("Error closing session of first player", e);
        }

        // закрываем сессию
        // второму игроку
        try {
            if (second.getSession().isOpen()) {
                second.getSession().close();
            }
        } catch (IOException e) {
            LOGGER.warn("Error closing session of second player", e);
        }
    }

    public void notePlayers() {
        if (first.getScore() == second.getScore()) {
            sender.send(first.getSession(), createMessage("It is draw", first.getScore()).toString());
            sender.send(second.getSession(), createMessage("It is draw", second.getScore()).toString());
            return;
        }

        if (first.getScore() > second.getScore()) {
            sender.send(first.getSession(), createMessage("You are win", first.getScore()).toString());
            sender.send(second.getSession(), createMessage("You are lose", second.getScore()).toString());
        } else {
            sender.send(second.getSession(), createMessage("You are win", second.getScore()).toString());
            sender.send(first.getSession(), createMessage("You are lose", first.getScore()).toString());
        }
    }

    public boolean checkPlayersConnection() {
        return first.getSession().isOpen() && second.getSession().isOpen();
    }

    private void updatePlayerState(Player player, ClientRequestData newState) {
        if (newState.getPosition() == null) {
            LOGGER.warn("Error json from client");
            return;
        }

        // обновляем позицию игрока
        player.setPosition(newState.getPosition());

        // обновляем данные пончика
        // запускаем рассчет его конечной позиции
        if (newState.isShoot()) {
            player.getDonut().setStartPosition(newState.getPosition());
            player.getDonut().setVelocity(newState.getVelocity());
            player.getDonut().setAngle(newState.getAngle());

            TaskRunner.solveDonutEndPosition(player.getDonut(), homer, player.getPositionX() < Config.MIDDLE_OF_BOARD);
        }
    }

    private static JSONObject createMessage(String message, Integer score) {
        final JSONObject json = new JSONObject();
        json.put("message", message);
        json.put("score", score);

        return json;
    }
}

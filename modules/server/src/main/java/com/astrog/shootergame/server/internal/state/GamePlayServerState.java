package com.astrog.shootergame.server.internal.state;

import com.astrog.shootergame.common.gamecore.ArcherGameContext;
import com.astrog.shootergame.common.gamecore.Looper;
import com.astrog.shootergame.common.messaging.CustomEvent;
import com.astrog.shootergame.common.messaging.TransferGameData;
import com.astrog.shootergame.common.socket.Client;
import com.astrog.shootergame.server.domain.ServerState;
import com.astrog.shootergame.server.domain.topic.Topic;
import com.astrog.shootergame.server.internal.Player;
import com.astrog.shootergame.server.internal.ShooterGameRestController;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.astrog.shootergame.common.Constants.GAME_PANE_HEIGHT;
import static com.astrog.shootergame.common.Constants.GAME_PANE_WIDTH;
import static com.astrog.shootergame.common.Constants.ServerMessages.PARTY_COMPLETE;
import static com.astrog.shootergame.common.messaging.ObjectToStringSerializer.serialize;
import static java.lang.Thread.sleep;

public class GamePlayServerState implements ServerState {

    private final List<Player> players;
    private final ArcherGameContext gameContext;
    private final Looper looper;
    private final Topic topic = new Topic();
    private final Map<Client, Boolean> isReady;
    private Client pauseTaker = null;

    public GamePlayServerState(List<Player> players) {
        this.players = players;
        List<String> playersNames = players.stream().map(Player::name).toList();
        this.gameContext = new ArcherGameContext(playersNames, new Point2D(GAME_PANE_WIDTH, GAME_PANE_HEIGHT));
        this.isReady = new HashMap<>();
        this.looper = new Looper(() -> {
            if (gameContext.isOver()) {
                ShooterGameRestController.tryUpdateStateIfEnded();
                throw new InterruptedException();
            }
            gameContext.update();
            TransferGameData transferGameData = createTransferDataFromContext();
            topic.broadcast(serialize(transferGameData));
            sleep(100);
        });
    }

    private TransferGameData createTransferDataFromContext() {
        return new TransferGameData(
            new ArrayList<>(gameContext.getTargets()),
            new ArrayList<>(gameContext.getArrows()),
            new HashMap<>(gameContext.getPlayerScoreMap())
        );
    }

    @Override
    public void onStart() {
        players.forEach(player -> {
            List<String> names = players.stream().map(Player::name).toList();
            player.client().printMessage(serialize(names));
        });

        players.stream().map(Player::client).forEach(topic::addListener);
        topic.broadcast(serialize(gameContext));
    }

    @Override
    public void onTakePause(Client client) {
        if (pauseTaker == null) {
            looper.pause();
            pauseTaker = client;
            return;
        }

        if (pauseTaker == client) {
            pauseTaker = null;
            looper.runLoop();
        }
    }

    @Override
    public void onStartGame(Client client) {
        isReady.put(client, true);
        if (isReady.size() == players.size()) {
            looper.runLoop();
        }
    }

    @Override
    public void onShoot(Client client) {
        players.stream()
            .filter(player -> player.client().equals(client))
            .findFirst()
            .ifPresent(player -> gameContext.spawnArrow(player.name()));
    }

    @Override
    public void onEnd() {
        topic.broadcast(CustomEvent.GAME_OVER.name());
    }

    @Override
    public boolean isEnded() {
        return gameContext.isOver();
    }

    @Override
    public ServerState getNextState() {
        return new InvitePlayersToPartyServerState();
    }
}

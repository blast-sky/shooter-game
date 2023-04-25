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
import com.astrog.shootergame.server.internal.database.ScoreRepository;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.astrog.shootergame.common.Constants.GAME_PANE_HEIGHT;
import static com.astrog.shootergame.common.Constants.GAME_PANE_WIDTH;
import static com.astrog.shootergame.common.messaging.CustomEvent.ALL_PLAYERS;
import static com.astrog.shootergame.common.messaging.CustomEvent.GAME_CONTEXT;
import static com.astrog.shootergame.common.messaging.MessageFormatter.formatMessage;
import static com.astrog.shootergame.common.messaging.serialization.ObjectToStringSerializer.serialize;
import static java.lang.Thread.sleep;

public class GamePlayServerState implements ServerState {

    private final ScoreRepository scoreRepository = new ScoreRepository();
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
                String winner = gameContext.getWinner();
                scoreRepository.increaseScoreToPlayerOrCreateAndIncrease(winner);
                ShooterGameRestController.tryUpdateStateIfEnded();
                throw new InterruptedException();
            }
            gameContext.update();
            TransferGameData transferGameData = createTransferDataFromContext();
            topic.broadcast(formatMessage(GAME_CONTEXT.name(), serialize(transferGameData)));
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
        List<String> names = players.stream().map(Player::name).toList();
        players.forEach(player -> player.client().print(formatMessage(ALL_PLAYERS.name(), serialize(names))));

        players.stream().map(Player::client).forEach(topic::addListener);
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
        topic.broadcast(formatMessage(CustomEvent.GAME_OVER.name()));
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

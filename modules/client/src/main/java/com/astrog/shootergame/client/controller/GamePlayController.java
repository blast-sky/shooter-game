package com.astrog.shootergame.client.controller;

import com.astrog.shootergame.client.ContextDrawer;
import com.astrog.shootergame.client.connection.ShooterGameClient;
import com.astrog.shootergame.common.messaging.CustomEvent;
import com.astrog.shootergame.common.messaging.TransferGameData;
import com.astrog.shootergame.common.socket.Client;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.astrog.shootergame.common.messaging.ObjectToStringSerializer.deserialize;

public class GamePlayController {

    @Getter
    private final ScheduledExecutorService gameLoopExecutor = Executors.newSingleThreadScheduledExecutor();
    public Runnable makeShoot;
    public Runnable takePause;
    public Runnable startGame;
    public Runnable toLoginScreen;
    public Client client;
    @FXML
    public ContextDrawer drawer;
    private final Runnable gameLoopAction = () -> {
        try {
            String message = client.getNextMessage();
            if (message.contains(CustomEvent.GAME_OVER.name())) {
                toLoginScreen.run();
                gameLoopExecutor.shutdownNow();
                return;
            }
            TransferGameData data = deserialize(message, TransferGameData.class);
            drawer.setData(data);
            drawer.drawGameObjects();
        } catch (Exception exception) {
            System.out.println("Game loop interrupted");
        }
    };
    @FXML
    public Pane scorePane;
    @FXML
    public AnchorPane gamePane;
    @FXML
    public AnchorPane playerPane;

    @FXML
    private void onStartClick() {
        gameLoopExecutor.scheduleWithFixedDelay(gameLoopAction, 0, 10, TimeUnit.MILLISECONDS);
        startGame.run();
    }

    @FXML
    public void onPauseClick() {
        takePause.run();
    }

    @FXML
    private void onShootClick() {
        makeShoot.run();
    }

    public void initDrawer() {
        drawer = new ContextDrawer(gamePane, scorePane, playerPane);
    }

    public void drawPlayers(List<String> players, String myName) {
        drawer.drawPlayers(players, myName);
    }

    public void init(ShooterGameClient client) {
        this.client = client.getClient();
        this.takePause = client::takePause;
        this.makeShoot = client::makeShoot;
        this.startGame = client::startGame;
    }
}
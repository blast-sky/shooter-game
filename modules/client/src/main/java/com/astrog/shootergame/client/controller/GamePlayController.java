package com.astrog.shootergame.client.controller;

import com.astrog.shootergame.client.ContextDrawer;
import com.astrog.shootergame.client.connection.ShooterGameClient;
import com.astrog.shootergame.common.messaging.CustomEvent;
import com.astrog.shootergame.common.messaging.TransferGameData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.List;

import static com.astrog.shootergame.client.ShooterGameClientApplication.createGlobalPlayersScoreWindow;
import static com.astrog.shootergame.common.messaging.serialization.ObjectToStringSerializer.deserialize;
import static com.astrog.shootergame.common.messaging.serialization.SerializationTypes.stringListType;

public class GamePlayController {

    public Runnable toLoginScreen;
    public ShooterGameClient client;
    @FXML
    public ContextDrawer drawer;

    @FXML
    public Pane scorePane;
    @FXML
    public AnchorPane gamePane;
    @FXML
    public AnchorPane playerPane;

    @FXML
    private void onStartClick() {
        client.subscribeOnEvent(CustomEvent.GAME_CONTEXT, context -> {
            TransferGameData data = deserialize(context, TransferGameData.class);
            drawer.setData(data);
            drawer.drawGameObjects();
        });

        client.subscribeOnEventOnce(CustomEvent.GAME_OVER, message -> {
            client.removeSubscription(CustomEvent.GAME_CONTEXT);
            toLoginScreen.run();
        });

        client.requestStartGame();
    }

    @FXML
    public void onPauseClick() {
        client.requestTakePause();
    }

    @FXML
    private void onShootClick() {
        client.requestMakeShoot();
    }

    @FXML
    private void onGetLeadersClick() {
        client.subscribeOnEvent(CustomEvent.RESPONSE_LEADERS_TABLE, leaders -> {
            client.removeSubscription(CustomEvent.RESPONSE_LEADERS_TABLE);
            List<String> leadersList = deserialize(leaders, stringListType);
            Platform.runLater(() -> createGlobalPlayersScoreWindow(leadersList));
        });
        client.requestLeaders();
    }

    public void initDrawer() {
        drawer = new ContextDrawer(gamePane, scorePane, playerPane);
    }

    public void drawPlayers(List<String> players, String myName) {
        drawer.drawPlayers(players, myName);
    }

    public void init(ShooterGameClient client) {
        this.client = client;
    }
}
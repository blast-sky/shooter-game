package com.astrog.shootergame.client.controller;

import com.astrog.shootergame.client.connection.ShooterGameClient;
import com.astrog.shootergame.client.lambda.StringParamLambda;
import com.astrog.shootergame.common.messaging.CustomEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.List;

import static com.astrog.shootergame.client.ShooterGameClientApplication.createGlobalPlayersScoreWindow;
import static com.astrog.shootergame.common.messaging.serialization.ObjectToStringSerializer.deserialize;
import static com.astrog.shootergame.common.messaging.serialization.SerializationTypes.stringListType;

public class LoginController {

    @FXML
    public TextField nameField;
    public StringParamLambda showNextScene;
    private ShooterGameClient client;

    @FXML
    public void onLoginClick() {
        client.connectIfNotAlreadyConnected();

        String name = nameField.getText();

        if (name.isBlank())
            return;

        client.subscribeOnEvent(CustomEvent.RESPONSE_LOGIN, message -> {
            client.removeSubscription(CustomEvent.RESPONSE_LOGIN);
            boolean success = deserialize(message, Boolean.class);
            if (success) {
                Platform.runLater(() -> showNextScene.run(name));
            }
        });
        client.requestLogin(name);
    }

    public void init(ShooterGameClient client) {
        this.client = client;
    }

    @FXML
    public void onLeaderBoardClick() {
        client.connectIfNotAlreadyConnected();

        client.subscribeOnEventOnce(CustomEvent.RESPONSE_LEADERS_TABLE, leaders -> {
            List<String> leadersList = deserialize(leaders, stringListType);
            Platform.runLater(() -> createGlobalPlayersScoreWindow(leadersList));
        });
        client.requestLeaders();
    }
}

package com.astrog.shootergame.client;

import com.astrog.shootergame.client.connection.ShooterGameClient;
import com.astrog.shootergame.client.controller.GamePlayController;
import com.astrog.shootergame.client.controller.LoginController;
import com.astrog.shootergame.common.messaging.CustomEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.util.List;

import static com.astrog.shootergame.common.Constants.HOST_IP;
import static com.astrog.shootergame.common.Constants.PORT;
import static com.astrog.shootergame.common.messaging.serialization.ObjectToStringSerializer.deserialize;
import static com.astrog.shootergame.common.messaging.serialization.SerializationTypes.stringListType;

public class ShooterGameClientApplication extends Application {

    private final ShooterGameClient client = new ShooterGameClient(HOST_IP, PORT);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        setLoginSceneAndConfigureController(stage);
        stage.setResizable(false);
        stage.show();
    }

    @SneakyThrows
    private void setLoginSceneAndConfigureController(Stage stage) {
        stage.setTitle("Archer - Login");
        FXMLLoader fxmlLoader = new FXMLLoader(ShooterGameClientApplication.class.getResource("screen/login-view.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));

        LoginController controller = fxmlLoader.getController();
        controller.init(client);
        controller.showNextScene = loginName -> setWaitScene(stage, loginName);
    }

    @SneakyThrows
    private void setGameSceneAndConfigureController(Stage stage, String name) {
        stage.setTitle("Archer - Game - " + name);
        FXMLLoader mainLoader = new FXMLLoader(ShooterGameClientApplication.class.getResource("screen/main-view.fxml"));
        stage.setScene(new Scene(mainLoader.load()));

        GamePlayController controller = mainLoader.getController();
        controller.toLoginScreen = () -> Platform.runLater(() -> setLoginSceneAndConfigureController(stage));
        controller.init(client);
        controller.initDrawer();
        client.subscribeOnEvent(CustomEvent.ALL_PLAYERS, message -> {
            client.removeSubscription(CustomEvent.ALL_PLAYERS);
            List<String> allPlayerNames = deserialize(message, stringListType);
            Platform.runLater(() -> controller.drawPlayers(allPlayerNames, name));
        });
    }

    @SneakyThrows
    private void setWaitScene(Stage stage, String name) {
        stage.setTitle("Archer - Wait");
        FXMLLoader fxmlLoader = new FXMLLoader(ShooterGameClientApplication.class.getResource("screen/wait-view.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        client.subscribeOnEventOnce(CustomEvent.PARTY_COMPLETE, message ->
            Platform.runLater(() -> setGameSceneAndConfigureController(stage, name)));
    }

    @SneakyThrows
    public static  void createGlobalPlayersScoreWindow(List<String> playersScore) {
        Stage stage = new Stage();
        stage.setTitle("Leaders");

        FXMLLoader fxmlLoader = new FXMLLoader(ShooterGameClientApplication.class.getResource("screen/leaders-view.fxml"));
        VBox pane =  fxmlLoader.load();

        var children = pane.getChildren();
        for(var score : playersScore) {
            children.add(new Label(score));
        }

        stage.setScene(new Scene(pane));
        stage.show();
    }

    @Override
    public void stop() {
        client.stopListening();
    }
}

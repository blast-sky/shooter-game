package com.astrog.shootergame.client;

import com.astrog.shootergame.client.connection.ShooterGameClient;
import com.astrog.shootergame.client.controller.GamePlayController;
import com.astrog.shootergame.client.controller.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.astrog.shootergame.common.Constants.HOST_IP;
import static com.astrog.shootergame.common.Constants.PORT;

public class ShooterGameClientApplication extends Application {

    private final ShooterGameClient client = new ShooterGameClient(HOST_IP, PORT);
    private ExecutorService executorServiceToStop = null;

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
        controller.showNextScene = loginName -> setWaitScene(stage, loginName);
        controller.tryLogin = client::connectAndTryLogin;
        executorServiceToStop = null;
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
        List<String> allPlayerNames = client.getAllPlayerNames();
        controller.drawPlayers(allPlayerNames, name);

        executorServiceToStop = controller.getGameLoopExecutor();
    }

    @SneakyThrows
    private void setWaitScene(Stage stage, String name) {
        stage.setTitle("Archer - Wait");
        FXMLLoader fxmlLoader = new FXMLLoader(ShooterGameClientApplication.class.getResource("screen/wait-view.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        executorServiceToStop = Executors.newSingleThreadExecutor();
        executorServiceToStop.submit(() -> {
            client.getClient().getNextMessage();
            Platform.runLater(() -> setGameSceneAndConfigureController(stage, name));
        });
    }

    @Override
    public void stop() {
        Optional.ofNullable(executorServiceToStop)
            .ifPresent(ExecutorService::shutdownNow);
    }
}

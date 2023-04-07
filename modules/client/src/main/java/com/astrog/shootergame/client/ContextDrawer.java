package com.astrog.shootergame.client;

import com.astrog.shootergame.client.view.ArrowView;
import com.astrog.shootergame.client.view.EnemyView;
import com.astrog.shootergame.client.view.PlayerScoreView;
import com.astrog.shootergame.client.view.PlayerView;
import com.astrog.shootergame.client.view.TargetView;
import com.astrog.shootergame.common.messaging.TransferGameData;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.astrog.shootergame.common.Constants.GAME_PANE_HEIGHT;

@RequiredArgsConstructor
public class ContextDrawer {

    private final Pane gameScene;
    private final Pane scoreScene;
    private final Pane playerScene;
    @Setter
    private TransferGameData data = null;

    public void drawGameObjects() {
        Platform.runLater(() -> {
            gameScene.getChildren().clear();
            scoreScene.getChildren().clear();
            data.arrows().forEach(arrow -> new ArrowView(arrow).draw(gameScene));
            data.targets().forEach(target -> new TargetView(target).draw(gameScene));
            data.playerScoreMap().forEach((name, score) -> new PlayerScoreView(name, score).draw(scoreScene));
        });
    }

    public void drawPlayers(List<String> allPlayerNames, String myName) {
        for (int i = 0; i < allPlayerNames.size(); i++) {
            String name = allPlayerNames.get(i);
            double height = GAME_PANE_HEIGHT / (allPlayerNames.size() + 1) * (i + 1);
            Platform.runLater(() -> {
                if (name.equals(myName)) {
                    new PlayerView(height).draw(playerScene);
                } else {
                    new EnemyView(height).draw(playerScene);
                }
            });
        }
    }
}

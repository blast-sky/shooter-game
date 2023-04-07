package com.astrog.shootergame.client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class EnemyView  implements FxDrawer {

    @SneakyThrows
    private static HBox getView() {
        return new FXMLLoader(EnemyView.class.getResource("enemy-view.fxml")).load();
    }

    private final double y;

    @Override
    public void draw(Pane scene) {
        HBox view = getView();
        view.setLayoutX(0);
        view.setLayoutY(y);
        scene.getChildren().add(view);
    }
}
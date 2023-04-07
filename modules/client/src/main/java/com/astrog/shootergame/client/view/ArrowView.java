package com.astrog.shootergame.client.view;

import com.astrog.shootergame.common.gamecore.Arrow;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class ArrowView implements FxDrawer {

    private final Arrow arrow;

    @SneakyThrows
    private static HBox getView() {
        return new FXMLLoader(ArrowView.class.getResource("arrow-view.fxml")).load();
    }

    @Override
    public void draw(Pane scene) {
        HBox view = getView();
        view.setLayoutX(arrow.getPosition().getX() - arrow.getWidth());
        view.setLayoutY(arrow.getPosition().getY() - arrow.getHeight() * 3 / 2 - 5);
        scene.getChildren().add(view);
    }
}

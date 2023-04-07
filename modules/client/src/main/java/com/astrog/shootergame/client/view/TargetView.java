package com.astrog.shootergame.client.view;

import com.astrog.shootergame.common.gamecore.Target;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TargetView implements FxDrawer {

    private final Target target;

    @Override
    public void draw(Pane scene) {
        Circle circle = new Circle(target.getRadius(), Color.GRAY);
        circle.setCenterX(target.getPosition().getX());
        circle.setCenterY(target.getPosition().getY());
        scene.getChildren().add(circle);
    }
}

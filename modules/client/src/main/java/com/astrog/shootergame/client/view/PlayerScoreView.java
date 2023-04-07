package com.astrog.shootergame.client.view;

import com.astrog.shootergame.common.gamecore.PlayerScore;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.FontWeight;
import lombok.RequiredArgsConstructor;

import java.awt.Font;
import java.util.List;

import static javafx.scene.text.Font.font;

@RequiredArgsConstructor
public class PlayerScoreView implements FxDrawer{

    private final String name;
    private final PlayerScore playerScore;

    @Override
    public void draw(Pane scene) {
        Label nameLabel = new Label("Имя: " + name);
        nameLabel.setFont(font("Verdana", FontWeight.BOLD, 16));
        Label scoreLabel = new Label("Счет: " + playerScore.score());
        Label shootCountLabel = new Label("Выстрелы: " + playerScore.shootCount());
        Pane space = new Pane();
        space.setPrefSize(10, 30);
        scene.getChildren().addAll(List.of(nameLabel, scoreLabel, shootCountLabel, space));
    }
}

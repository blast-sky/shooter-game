package com.astrog.shootergame.common.gamecore;

import com.astrog.shootergame.common.gamecore.rule.DefaultWinnerRule;
import com.astrog.shootergame.common.gamecore.rule.WinnerRule;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.astrog.shootergame.common.Constants.GAME_PANE_HEIGHT;

public class ArcherGameContext {

    private final List<String> players;
    private final BoundingBox fieldBox;
    @Getter
    private final List<Target> targets = new ArrayList<>();
    @Getter
    private final List<Arrow> arrows = new ArrayList<>();
    @Getter
    private final Map<String, PlayerScore> playerScoreMap = new HashMap<>();
    private final WinnerRule rule = new DefaultWinnerRule();

    public ArcherGameContext(List<String> players, Point2D fieldSize) {
        this.players = players;
        this.fieldBox = new BoundingBox(0, 0, 0, fieldSize.getX(), fieldSize.getY(), 0);
        targets.add(new Target(new Point2D(510, GAME_PANE_HEIGHT / 2), new Point2D(0, 5), 50, 1));
        targets.add(new Target(new Point2D(610, GAME_PANE_HEIGHT / 2), new Point2D(0, 10), 25, 2));
        initPlayerScoreMap(players);
    }

    private void initPlayerScoreMap(List<String> players) {
        for (String name : players) {
            playerScoreMap.put(name, new PlayerScore(0, 0));
        }
    }

    private double getArrowSpawnHeightForPlayer(String player) {
        int id = players.indexOf(player);
        double partHeight = fieldBox.getHeight() / (players.size() + 1);
        return partHeight * (id + 1);
    }

    public synchronized void update() {
        checkCollisionAndInFieldBoundary();
        targets.forEach(Target::update);
        arrows.forEach(Arrow::update);
    }

    public boolean isOver() {
        return rule.isGameOver(playerScoreMap);
    }

    private void checkCollisionAndInFieldBoundary() {
        for (Target target : targets) {
            Bounds targetBounds = target.getBounds();

            arrows.removeIf(arrow -> {
                Bounds arrowBounds = arrow.getBounds();
                if (targetBounds.intersects(arrowBounds)) {
                    increasePlayerScore(arrow.owner, target.scorePerShoot, 0);
                    return true;
                }
                return false;
            });

            if (!fieldBox.contains(targetBounds)) target.inverseVelocity();
        }
        arrows.removeIf(arrow -> !fieldBox.intersects(arrow.getBounds()));
    }

    private void increasePlayerScore(String owner, long score, long shootCount) {
        PlayerScore old = playerScoreMap.get(owner);
        playerScoreMap.put(owner, new PlayerScore(old.score() + score, old.shootCount() + shootCount));
    }

    public synchronized void spawnArrow(String owner) {
        double spawnHeight = getArrowSpawnHeightForPlayer(owner);
        Arrow arrow = new Arrow(new Point2D(0, spawnHeight), new Point2D(10, 0), owner);
        increasePlayerScore(owner, 0, 1);
        arrows.add(arrow);
    }
}

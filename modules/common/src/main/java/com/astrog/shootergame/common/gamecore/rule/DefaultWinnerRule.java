package com.astrog.shootergame.common.gamecore.rule;

import com.astrog.shootergame.common.gamecore.PlayerScore;

import java.util.Map;

import static com.astrog.shootergame.common.Constants.SCORE_TO_WIN;

public class DefaultWinnerRule implements WinnerRule {

    private static final int winnerScore = SCORE_TO_WIN;

    @Override
    public boolean isGameOver(Map<String, PlayerScore> playerScores) {
        return playerScores.entrySet().stream()
            .anyMatch(entry -> entry.getValue().score() >= winnerScore);
    }

    @Override
    public String getWinner(Map<String, PlayerScore> playerScores) {
        return playerScores.entrySet().stream()
            .filter(set -> set.getValue().score() >= winnerScore)
            .findFirst()
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}

package com.astrog.shootergame.common.gamecore.rule;

import com.astrog.shootergame.common.gamecore.PlayerScore;

import java.util.Map;

public interface WinnerRule {

    boolean isGameOver(Map<String, PlayerScore> playerScores);

    String getWinner(Map<String, PlayerScore> playerScores);
}

package com.astrog.shootergame.common.messaging;

import com.astrog.shootergame.common.gamecore.Arrow;
import com.astrog.shootergame.common.gamecore.PlayerScore;
import com.astrog.shootergame.common.gamecore.Target;

import java.util.List;
import java.util.Map;

public record TransferGameData(
    List<Target> targets,
    List<Arrow> arrows,
    Map<String, PlayerScore> playerScoreMap
) {
}

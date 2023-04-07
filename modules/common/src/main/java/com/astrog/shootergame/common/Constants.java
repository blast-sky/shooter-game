package com.astrog.shootergame.common;

public class Constants {
    public static final int PORT = 8080;

    public static final int SCORE_TO_WIN = 100;

    public static final int MAX_CLIENT_IN_PARTY_COUNT = 3;

    public static final String HOST_IP = "localhost";

    public static final double GAME_PANE_HEIGHT = 470;

    public static final double GAME_PANE_WIDTH = 700;

    public enum ServerMessages {
        NAME_ALREADY_EXIST,
        LOGIN_SUCCESS,
        MAX_CLIENT_COUNT_MESSAGE,
        CLIENT_HANDLED,
        PARTY_COMPLETE,
    }
}

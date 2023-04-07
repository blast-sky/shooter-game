package com.astrog.shootergame.server;

import com.astrog.shootergame.server.domain.Server;

import static com.astrog.shootergame.common.Constants.PORT;

public class ShooterGameServerApplication {

    public static void main(String[] args) {
        System.out.println("Server created.");
        Server shooterGameServer = new Server(PORT);
        shooterGameServer.run();
    }
}

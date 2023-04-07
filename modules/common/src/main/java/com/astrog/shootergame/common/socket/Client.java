package com.astrog.shootergame.common.socket;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    @SneakyThrows
    public Client(Socket socket) {
        this.socket = socket;
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @SneakyThrows
    public boolean hasIncomingMessages() {
        return reader.ready();
    }

    @SneakyThrows
    public String getNextMessage() {
        return reader.readLine();
    }

    @SneakyThrows
    public void printMessage(String message) {
        writer.println(message);
    }

    @SneakyThrows
    public void disconnect() {
        socket.close();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}

package com.server;

// GameServer.java
import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 5917;
    private static final BlockingQueue<Socket> waitingClients = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket);
            waitingClients.add(clientSocket);

            // Start a game when two clients are available
            if (waitingClients.size() >= 2) {
                try {
                    Socket client1 = waitingClients.take();
                    Socket client2 = waitingClients.take();
                    GameSession game = new GameSession(client1, client2);
                    new Thread(game).start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
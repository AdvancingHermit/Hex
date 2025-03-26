package com.server;

import java.io.*;
import java.net.*;
import java.util.Random;

public class GameSession implements Runnable {
    private final Socket client1, client2;
    private ObjectInputStream in1, in2;
    private ObjectOutputStream out1, out2;

    private Board board;
    private int rows = 11;
    private int cols = 11;
    private GameState gameState;

    public GameSession(Socket client1, Socket client2) {
        this.client1 = client1;
        this.client2 = client2;
    }

    @Override
    public void run() {
        try {
            out1 = new ObjectOutputStream(client1.getOutputStream());
            in1 = new ObjectInputStream(client1.getInputStream());

            out2 = new ObjectOutputStream(client2.getOutputStream());
            in2 = new ObjectInputStream(client2.getInputStream());

            establishInformation();

            while (!gameState.isGameFinished()) {
                if (gameState.getCurrentClient() == 1) {
                    parseMove(in1, out2);
                } else {
                    parseMove(in2, out1);
                }
                if (board.checkWin(gameState.getCurrentPlayer())) {
                    gameState.setGameFinished(true);
                    sendWinInfo();
                } else {
                    gameState.nextPlayer();
                }
            }

        } catch (IOException e) {
            System.err.println("Game session error: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void establishInformation() throws IOException {
        Random rand = new Random();
        int playerOneClientNum = rand.nextInt(1,3);
        gameState = new GameState(playerOneClientNum);
        board = new Board(rows, cols);
        if (playerOneClientNum == 1) {
            out1.writeObject("1");
            out2.writeObject("2");
        } else {
            out1.writeObject("2");
            out2.writeObject("1");
        }
        
        
    }
    private void sendWinInfo() throws IOException {
        out1.writeObject("w " + gameState.getCurrentPlayer());
        out2.writeObject("w " + gameState.getCurrentClient());
    }

    private void parseMove(ObjectInputStream in, ObjectOutputStream out) {
        try {
            String move;
            if ((move = (String) in.readObject()) != null) {
                String[] coords = move.split(" ");
                if (coords.length == 2) {
                    board.setPiece(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), gameState.getCurrentPlayer());
                    relayMove(out, move);
                } else {
                    System.err.println("Client sent invalid move");
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Client sent invalid move: " + e.getMessage());
        }

    }

    private void relayMove(ObjectOutputStream out, String move) throws IOException {
        String fullMove = move + " " + gameState.getCurrentPlayer();
        out.writeObject(fullMove);
        out.flush();
    }

    private void closeResources() {
        try {
            if (client1 != null) client1.close();
            if (client2 != null) client2.close();
        } catch (IOException e) {
            System.err.println("Error closing sockets: " + e.getMessage());
        }
    }
}
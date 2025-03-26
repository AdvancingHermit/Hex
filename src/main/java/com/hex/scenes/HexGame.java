package com.hex.scenes;
import com.hex.GameState;
import com.hex.HexApp;
import com.hex.components.BoardUI;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.*;

public class HexGame extends BaseScene {
    private GameState gameState = new GameState();
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5917;
    private boolean hasMoved = false;
    private BoardUI hexBoard;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;


    public HexGame(HexApp hexApp) {

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            drawGame();

            getGameInfo();

            new Thread(() -> {
                try {
                    String serverMove;
                    while ((serverMove = (String) in.readObject()) != null ) {
                        String[] parts = serverMove.split(" ");
                        if (parts[0].equals("w")) {
                            System.out.println("Ayo, player " + parts[1] + " won!");
                            if (gameState.getPlayerNum() == Integer.parseInt(parts[1])) {
                                System.out.println("(thats you)");
                            } else {
                                System.out.println("(you lost bitch)");
                            }
                        } else {
                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            int player = Integer.parseInt(parts[2]);

                            Platform.runLater(() -> {
                                hexBoard.getBoard().setPiece(x, y, player);
                                gameState.nextPlayer();
                                hexBoard.getChildren().clear();
                                hexBoard.drawBoard();
                            });
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            }

    }

    public void getGameInfo() {
        try {
            String playerNum;
            if ((playerNum = (String) in.readObject()) != null) {
                gameState.setPlayerNum(Integer.parseInt(playerNum));
                System.out.println(Integer.parseInt(playerNum));
            } else {
                System.out.println("Error here playernum fr");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void drawGame() {
        BorderPane root = new BorderPane();

        StackPane gameWrap = new StackPane();

        // Hex Board and wrapper
        Group boardWrap = new Group();

        hexBoard = new BoardUI(11, 11, 30, gameState,  coords -> {
            try {
                String move = coords[0] + " " + coords[1];
                out.writeObject(move);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hexBoard.drawBoard();
        boardWrap.getChildren().add(hexBoard);

        // Top Info
        Label infoTop = new Label("Hex / Score");
        HBox topBox = new HBox(infoTop);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPrefHeight(50);

        // Right Info
        VBox rightBox = new VBox(new Label("Player Info"), new Label("Other Stats"));
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPrefWidth(100);

        // Add the things to game wrapper
        gameWrap.getChildren().add(boardWrap);
        //topBox.setTranslateY(-boardWrap.getHeight() / 2 - 20);
        //rightBox.setTranslateX(-boardWrap.getWidth() / 2 - 50);
        //gameWrap.getChildren().addAll( topBox, rightBox);

        // Add things to root
        root.setCenter(gameWrap);



        scene = new Scene(root, 800, 600);
    }

}

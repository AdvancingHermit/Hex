package com.hex.scenes;
import com.hex.GameState;
import com.hex.SceneManager.SceneType;
import com.hex.SceneManager;
import com.hex.components.Board;
import com.hex.HexApp;
import com.hex.components.BoardUI;
import com.hex.gamecontroller.OnlineController;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.*;

public class OnlineGame extends BaseScene {
    private GameState gameState = new GameState();
    private BoardUI hexBoard;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Board board = new Board(11, 11);
    private Label turnLabel;

    public OnlineGame(SceneManager sceneManager, Socket socket,
                      ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;

        OnlineController.createOnlineController(board, gameState, this::sendMove);

        drawGame(sceneManager);
        getGameInfo();
        listenForMoves();
    }

    public void listenForMoves() {
        new Thread(() -> {
            try {
                String serverMove;
                while ((serverMove = (String) in.readObject()) != null) {
                    String[] parts = serverMove.split(" ");
                    if (parts[0].equals("w")) {
                        handleGameEnd(parts);
                    } else {
                        handleReceivedMove(parts);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getGameInfo() {
        try {
            String playerNum;
            if ((playerNum = (String) in.readObject()) != null) {
                gameState.setPlayerNum(Integer.parseInt(playerNum));
                updateTurnLabel("You are Player " + gameState.getPlayerNum());
            } else {
                System.out.println("Error getting player number");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTurnLabel(String text) {
        if (turnLabel != null) {
            Platform.runLater(() -> turnLabel.setText(text));
        }
    }

    public void handleReceivedMove(String[] parts) {
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int player = Integer.parseInt(parts[2]);

        Platform.runLater(() -> {
            if (board.getPiece(x, y) != 0) {
                hexBoard.getBoard().setPiece(x, y, 0);
                hexBoard.getBoard().setPiece(y, x, player);
            } else {
                hexBoard.getBoard().setPiece(x, y, player);
            }

            gameState.nextPlayer();
            hexBoard.getChildren().clear();
            hexBoard.drawBoard();

            updateTurnLabel("Your turn ");
        });
    }

    public void handleGameEnd(String[] parts) {
        int winner = Integer.parseInt(parts[1]);
        String msg = "Player " + winner + " won!";

        if (gameState.getPlayerNum() == winner) {
            msg += " (That's you)";
        } else {
            msg += " (You lost)";
        }

        final String finalMsg = msg;

        Platform.runLater(() -> {
            Label gameFinishLabel = new Label(finalMsg);
            gameFinishLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            BorderPane root = (BorderPane) scene.getRoot();
            VBox centerBox = new VBox(20);
            centerBox.setAlignment(Pos.CENTER);
            centerBox.getChildren().addAll(gameFinishLabel);
            root.setCenter(centerBox);
        });

        System.out.println(finalMsg);
    }

    public void drawGame(SceneManager sceneManager) {
        BorderPane root = new BorderPane();

        // Hex Board and wrapper
        Group boardWrap = new Group();
        hexBoard = new BoardUI(board, 30, BoardUI.ControllerType.ONLINE, null, false, null);
        hexBoard.drawBoard();
        boardWrap.getChildren().add(hexBoard);

        // Top Info
        Label infoTop = new Label("Hex Game");
        HBox topBox = new HBox(infoTop);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPrefHeight(50);

        // Right Info
        VBox rightBox = new VBox(10);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPrefWidth(150);

        Label playerInfoLabel = new Label("Player Info");
        turnLabel = new Label("Connecting...");

        Button backButton = new Button("Go back");
        backButton.setOnAction(e -> {
            closeConnection();
            sceneManager.clearConnection();
            sceneManager.switchScene(SceneType.MAIN_MENU);
        });

        rightBox.getChildren().addAll(playerInfoLabel, turnLabel, backButton);

        root.setTop(topBox);
        root.setRight(rightBox);
        root.setCenter(boardWrap);

        scene = new Scene(root, sceneManager.getSceneWidth() + 200, sceneManager.getSceneHeight());
    }

    public void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Client disconnected gracefully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendMove(int[] coords) {
        try {
            String move = coords[0] + " " + coords[1];
            out.writeObject(move);
            out.flush();
            updateTurnLabel("Opponents turn");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
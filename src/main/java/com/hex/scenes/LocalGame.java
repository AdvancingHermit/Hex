package com.hex.scenes;
import com.hex.GameMode;
import com.hex.GameState;
import com.hex.SceneManager;
import com.hex.algorithms.Algorithm;

import com.hex.algorithms.minimax.ConnectionPlayer;

import com.hex.algorithms.montecarlo.MCTS;
import com.hex.algorithms.montecarlo.MCTSDouble;

import com.hex.components.Board;
import com.hex.components.BoardUI;
import com.hex.gamecontroller.DoublePieceController;
import com.hex.gamecontroller.GameController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class LocalGame extends BaseScene {
    private GameState gameState = new GameState();
    @Getter
    private Label playerInfo;
    @Getter
    private GameMode mode;

    public LocalGame(int boardSize, Algorithm algorithm1, boolean algoStart, int algoIterations, GameMode mode, SceneManager sceneManager) {
        BorderPane root = new BorderPane();
        this.mode = mode;

        StackPane gameWrap = new StackPane();


        // Hex Board and wrapper
        Group boardWrap = new Group();

        Board board = new Board(boardSize,boardSize);
        switch (mode) {
            case NORMAL ->  GameController.createGameController(board, gameState, algorithm1, algoStart,
                    algoIterations, false);
            case SWAP ->  GameController.createGameController(board, gameState, algorithm1, algoStart,
                    algoIterations, true);
            case DOUBLE ->  DoublePieceController.createDoublePieceController(board, gameState, algorithm1, algoStart,
                    algoIterations, false);
        }
        boolean singlePlayer = algorithm1 == null;
        if (algoStart && !singlePlayer) {
            playerInfo = new Label("Blue Player's Turn");
            playerInfo.setStyle("-fx-text-fill: Blue; -fx-font-size: 20px;");
        } else {
            playerInfo = new Label("Red Player's Turn");
            playerInfo.setStyle("-fx-text-fill: Red; -fx-font-size: 20px;");
        }
        BoardUI hexBoard = new BoardUI(board, 30, BoardUI.ControllerType.LOCAL_GAME, mode, singlePlayer, this::updateLabel);


        hexBoard.drawBoard();
        boardWrap.getChildren().add(hexBoard);

        // Top Info

        HBox topBox = new HBox(playerInfo);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPrefHeight(50);



        // Right Info
        VBox rightBox = new VBox(new Label("Player Info"), new Label("Other Stats"));
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPrefWidth(100);

        //backbutton
        Button backButton = new Button("← Back");
        backButton.setOnAction(e -> sceneManager.switchScene(SceneManager.SceneType.MAIN_MENU));
        backButton.setStyle("-fx-font-size: 14px;");

        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(10));
        backButtonBox.setPickOnBounds(false);
        boardWrap.setPickOnBounds(false);
        topBox.setPickOnBounds(false);

        // Add the things to game wrapper
        gameWrap.getChildren().addAll(boardWrap, backButtonBox, topBox);
        //topBox.setTranslateY(-boardWrap.getHeight() / 2 - 20);
        //rightBox.setTranslateX(-boardWrap.getWidth() / 2 - 50);
        //gameWrap.getChildren().addAll( topBox, rightBox);

        // Add things to root
        root.setCenter(gameWrap);



        scene = new Scene(root, 800, 600);

    }

    private void updateLabel(){
        Platform.runLater(() -> {
        Label info = getPlayerInfo();
        if (gameState.isGameFinished()){
            int loser = gameState.getBoardPieces() % 2 == 0 ? 2 : 1;
            if (!(getMode() == GameMode.DOUBLE)){
                loser = 2;
            }
            String winner = gameState.getCurrentPlayer() == loser ? "Blue Player Won" : "Red Player Won";
            info.setText(winner);
            if (winner.equals("Blue Player Won")) {
                info.setStyle("-fx-text-fill: Blue; -fx-font-size: 20px;");
            } else {
                info.setStyle("-fx-text-fill: Red; -fx-font-size: 20px;");
            }
        } else {
            String player = gameState.getCurrentPlayer() == 1 ? "Blue Player's Turn" : "Red Player's Turn";
            info.setText(player);
            if (player.equals("Blue Player's Turn")) {
                info.setStyle("-fx-text-fill: Blue; -fx-font-size: 20px;");
            } else {
                info.setStyle("-fx-text-fill: Red; -fx-font-size: 20px;");
            }
        }
        });
    }


}
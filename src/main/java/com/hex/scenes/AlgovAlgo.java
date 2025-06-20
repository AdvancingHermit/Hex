package com.hex.scenes;

import com.hex.GameMode;
import com.hex.GameState;
import com.hex.SceneManager;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.minimax.ConnectionPlayer;
import com.hex.algorithms.montecarlo.MCTS;
import com.hex.components.Board;
import com.hex.components.BoardUI;
import com.hex.gamecontroller.AlgorithmTesterController;
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

public class AlgovAlgo extends BaseScene {
    private GameState gameState = new GameState();
    @Getter
    private Label playerInfo;
    @Getter
    GameMode mode;
    @Getter
    String algo1;
    @Getter
    String algo2;

    public AlgovAlgo(int boardSize, Algorithm startingAlgorithm, Algorithm secondAlgorithm, int startingIterations, int secondIterations, GameMode mode, SceneManager sceneManager, String algo1, String algo2) {
        BorderPane root = new BorderPane();
        this.mode = mode;
        this.algo1 = algo1;
        this.algo2 = algo2;

        StackPane gameWrap = new StackPane();

        // Hex Board and wrapper
        Group boardWrap = new Group();
        Board board = new Board(boardSize,boardSize);
        AlgorithmTesterController.createGameController(board, gameState, startingAlgorithm, secondAlgorithm, startingIterations, secondIterations, this::updateLabel);
        BoardUI hexBoard = new BoardUI(board, 30, BoardUI.ControllerType.ALGORITHM_TESTER, GameMode.NORMAL, false, null);
        playerInfo = new Label(algo1 + "1's Turn");
        playerInfo.setStyle("-fx-text-fill: Red; -fx-font-size: 20px;");
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

        gameWrap.getChildren().addAll(boardWrap, backButtonBox, topBox);
        root.setCenter(gameWrap);
        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());
    }
    private void updateLabel(){
        Platform.runLater(() -> {
            Label info = getPlayerInfo();
            if (gameState.isGameFinished()){
                int loser = gameState.getBoardPieces() % 2 == 0 ? 2 : 1;
                if (!(getMode() == GameMode.DOUBLE)){
                    loser = 2;
                }
                String winner = gameState.getCurrentPlayer() == loser ? algo2 + "2 Won" : algo1 +"1 Won";
                info.setText(winner);
                if (winner.equals(algo1 +"1 Won")) {
                    info.setStyle("-fx-text-fill: Red; -fx-font-size: 20px;");
                } else {
                    info.setStyle("-fx-text-fill: Blue; -fx-font-size: 20px;");
                }
            } else {
                String player = gameState.getCurrentPlayer() == 2 ? algo2 + "2's Turn" : algo1 +"1's Turn";
                info.setText(player);
                if (player.equals(algo2 + "2's Turn")) {
                    info.setStyle("-fx-text-fill: Blue; -fx-font-size: 20px;");
                } else {
                    info.setStyle("-fx-text-fill: Red; -fx-font-size: 20px;");
                }
            }
        });
    }
}

package com.hex.scenes;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.minimax.ConnectionPlayer;
import com.hex.algorithms.montecarlo.MCTS;
import com.hex.components.Board;
import com.hex.components.BoardUI;
import com.hex.gamecontroller.AlgorithmTesterController;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AlgovAlgo extends BaseScene {
    private GameState gameState = new GameState();

    public AlgovAlgo() {
        BorderPane root = new BorderPane();

        StackPane gameWrap = new StackPane();

        //Algorithm
        Algorithm algorithm = new ConnectionPlayer();
        Algorithm otherAlgo = new MCTS();

        // Hex Board and wrapper
        Group boardWrap = new Group();
        Board board = new Board(5,5);
        AlgorithmTesterController.createGameController(board, gameState, algorithm, otherAlgo);
        BoardUI hexBoard = new BoardUI(board, 30, BoardUI.ControllerType.ALGORITHM_TESTER);


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


        gameWrap.getChildren().add(boardWrap);

        root.setCenter(gameWrap);

        scene = new Scene(root, 800, 600);

    }
}

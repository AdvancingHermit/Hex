package com.hex.scenes;
import com.hex.GameMode;
import com.hex.GameState;
import com.hex.algorithms.Algorithm;

import com.hex.algorithms.minimax.ConnectionPlayer;

import com.hex.algorithms.montecarlo.MCTS;
import com.hex.algorithms.montecarlo.MCTSDouble;

import com.hex.components.Board;
import com.hex.components.BoardUI;
import com.hex.gamecontroller.DoublePieceController;
import com.hex.gamecontroller.GameController;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LocalGame extends BaseScene {
    private GameState gameState = new GameState();

    public LocalGame(int boardSize, boolean algoStart, int algoIterations, GameMode mode) {
        BorderPane root = new BorderPane();

        StackPane gameWrap = new StackPane();

        //Algorithm

        Algorithm algorithm = new ConnectionPlayer();
        Algorithm mcts = new MCTS();
        Algorithm mctsDouble = new MCTSDouble();

        // Hex Board and wrapper
        Group boardWrap = new Group();
        Board board = new Board(3,3);
            GameController.createGameController(board, gameState, mcts, false,
                    20*100_000, false);
            // DoublePieceController.createDoublePieceController(board, gameState, mctsDouble, true,
            //       10*100_000, false);

            BoardUI hexBoard = new BoardUI(board, 30, BoardUI.ControllerType.LOCAL_GAME, mode);


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
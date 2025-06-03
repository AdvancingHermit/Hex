package com.hex.scenes;
import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.montecarlo.MCTS;
import com.hex.components.Board;
import com.hex.HexApp;
import com.hex.components.BoardUI;
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

    public LocalGame() {
        BorderPane root = new BorderPane();

        StackPane gameWrap = new StackPane();

        //Algorithm
        Algorithm algorithm = new MCTS();

        // Hex Board and wrapper
        Group boardWrap = new Group();
        Board board = new Board(11,11);
        GameController.createGameController(board, gameState, algorithm);
        BoardUI hexBoard = new BoardUI(board, 30, false);


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
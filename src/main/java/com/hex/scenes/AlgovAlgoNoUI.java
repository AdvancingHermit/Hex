package com.hex.scenes;

import com.hex.GameMode;
import com.hex.GameState;
import com.hex.SceneManager;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardUI;
import com.hex.gamecontroller.AlgorithmNoUIController;
import com.hex.gamecontroller.AlgorithmTesterController;
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
// Christian
public class AlgovAlgoNoUI extends BaseScene {
    private GameState gameState = new GameState();

    public AlgovAlgoNoUI(int boardSize, Algorithm startingAlgorithm, Algorithm secondAlgorithm, int startingIterations, int secondIterations, GameMode mode, SceneManager sceneManager) {
        BorderPane root = new BorderPane();

        StackPane gameWrap = new StackPane();

        // Hex Board and wrapper
        Group boardWrap = new Group();
        Board board = new Board(boardSize,boardSize);
        AlgorithmNoUIController.createGameController(board, gameState, startingAlgorithm, secondAlgorithm, startingIterations, secondIterations);

        // Top Info
        Label infoTop = new Label("Hex / Score");
        HBox topBox = new HBox(infoTop);
        topBox.setAlignment(Pos.CENTER);
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

        gameWrap.getChildren().addAll(boardWrap, backButtonBox);
        root.setCenter(gameWrap);
        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());
    }
}

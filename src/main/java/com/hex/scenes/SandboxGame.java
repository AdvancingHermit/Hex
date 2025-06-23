package com.hex.scenes;
import com.hex.GameMode;
import com.hex.GameState;
import com.hex.SceneManager;
import com.hex.algorithms.Algorithm;

import com.hex.algorithms.minimax.ConnectionPlayer;

import com.hex.algorithms.minimax.Move;
import com.hex.algorithms.minimax.sandboxHelpers.SandboxPosition;
import com.hex.algorithms.montecarlo.MCTS;
import com.hex.algorithms.montecarlo.MCTSDouble;

import com.hex.components.Board;
import com.hex.components.BoardUI;
import com.hex.gamecontroller.DoublePieceController;
import com.hex.gamecontroller.GameController;
import com.hex.gamecontroller.SandboxController;
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
//Made by Oliver

public class SandboxGame extends BaseScene {
    private GameState gameState = new GameState();
    @Getter
    private Label playerInfo;
    @Getter
    private GameMode mode;

    //Scene for the base local game, where the board can be seen
    public SandboxGame(int boardSize, SceneManager sceneManager) {
        BorderPane root = new BorderPane();
        this.mode = mode;

        StackPane gameWrap = new StackPane();


        // Hex Board and wrapper
        Group boardWrap = new Group();

        Board board = new Board(boardSize,boardSize);


        boolean singlePlayer = true;
        if (!singlePlayer) {
            playerInfo = new Label("Blue Player's Turn");
            playerInfo.setStyle("-fx-text-fill: Blue; -fx-font-size: 20px;");
        } else {
            playerInfo = new Label("Red Player's Turn");
            playerInfo.setStyle("-fx-text-fill: Red; -fx-font-size: 20px;");
        }

        SandboxController.createSandboxController(board, gameState, this::updateLabel);

        BoardUI hexBoard = new BoardUI(board, 30, BoardUI.ControllerType.SANDBOX, mode, singlePlayer, this::updateLabel);


        hexBoard.drawBoard();
        boardWrap.getChildren().add(hexBoard);

        // Top Info

        HBox topBox = new HBox(playerInfo);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPrefHeight(50);

        //backbutton
        Button evalButton = new Button("Eval");
        evalButton.setOnAction(e -> evalFunc(board, hexBoard));
        evalButton.setStyle("-fx-font-size: 14px;");

        Button setRed = new Button("Set Player Red");
        setRed.setOnAction(e -> setCurrPlayer(1));
        setRed.setStyle("-fx-font-size: 14px;");

        Button setBlue = new Button("Set Player Blue");
        setBlue.setOnAction(e -> setCurrPlayer(2));
        setBlue.setStyle("-fx-font-size: 14px;");

        Button setZero = new Button("Remove Pieces");
        setZero.setOnAction(e -> setCurrPlayer(0));
        setZero.setStyle("-fx-font-size: 14px;");

        VBox evalButtonBox = new VBox(evalButton, setRed, setBlue, setZero);
        evalButtonBox.setAlignment(Pos.TOP_RIGHT);
        evalButtonBox.setPadding(new Insets(10));
        evalButtonBox.setPickOnBounds(false);



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
        gameWrap.getChildren().addAll(boardWrap, backButtonBox, evalButtonBox, topBox);
        //topBox.setTranslateY(-boardWrap.getHeight() / 2 - 20);
        //rightBox.setTranslateX(-boardWrap.getWidth() / 2 - 50);
        //gameWrap.getChildren().addAll( topBox, rightBox);

        // Add things to root
        root.setCenter(gameWrap);



        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());

    }

    private void setCurrPlayer(int player){
        gameState.setCurrentPlayer(player);
    }

    private void showCarrier(SandboxPosition sandboxPosition, Board board, int nextPlayer){
        if (nextPlayer == 1 && sandboxPosition.bestRedVC == null && sandboxPosition.bestBlueVC == null && sandboxPosition.bestSemiRedVC == null) return;
        if (nextPlayer == 2 && sandboxPosition.bestRedVC == null && sandboxPosition.bestBlueVC == null && sandboxPosition.bestSemiBlueVC == null) return;

        if (nextPlayer == 1) {
            if (sandboxPosition.bestBlueVC != null && (sandboxPosition.bestRedVC == null || sandboxPosition.bestBlueVC.depth > sandboxPosition.bestRedVC.depth)) {
                for (Move move : sandboxPosition.bestBlueVC.carrier) {
                    board.setPiece(move.x - 1, move.y - 1, 3);
                }
            } else if (sandboxPosition.bestBlueVC != null && (sandboxPosition.bestSemiRedVC == null || sandboxPosition.bestBlueVC.depth > sandboxPosition.bestSemiRedVC.depth)) {
                for (Move move : sandboxPosition.bestBlueVC.carrier) {
                    board.setPiece(move.x - 1, move.y - 1, 3);
                }
            } else if (sandboxPosition.bestRedVC != null && (sandboxPosition.bestSemiRedVC == null || sandboxPosition.bestRedVC.depth >= sandboxPosition.bestSemiRedVC.depth)) {
                for (Move move : sandboxPosition.bestRedVC.carrier) {
                    board.setPiece(move.x - 1, move.y - 1, 4);
                }
            } else {
                for (Move move : sandboxPosition.bestSemiRedVC.carrier) {
                    board.setPiece(move.x - 1, move.y - 1, 4);
                }
                board.setPiece(sandboxPosition.bestSemiRedVC.criticalCell.x - 1, sandboxPosition.bestSemiRedVC.criticalCell.y - 1, 5);
            }
        }
        if (nextPlayer == 2){
            if (sandboxPosition.bestRedVC != null && (sandboxPosition.bestBlueVC == null || sandboxPosition.bestRedVC.depth > sandboxPosition.bestBlueVC.depth)) {
                for (Move move : sandboxPosition.bestRedVC.carrier) {
                    board.setPiece(move.x - 1, move.y - 1, 4);
                }
            } else if (sandboxPosition.bestRedVC != null && (sandboxPosition.bestSemiBlueVC == null || sandboxPosition.bestRedVC.depth > sandboxPosition.bestSemiBlueVC.depth)) {
                for (Move move : sandboxPosition.bestRedVC.carrier) {
                    board.setPiece(move.x - 1, move.y - 1, 4);
                }
            } else if (sandboxPosition.bestBlueVC != null && (sandboxPosition.bestSemiBlueVC == null || sandboxPosition.bestBlueVC.depth >= sandboxPosition.bestSemiBlueVC.depth)) {
                for (Move move : sandboxPosition.bestBlueVC.carrier) {
                    board.setPiece(move.x - 1, move.y - 1, 3);
                }
            } else {
                for (Move move : sandboxPosition.bestSemiBlueVC.carrier) {
                    board.setPiece(move.x - 1, move.y - 1, 3);
                }
                board.setPiece(sandboxPosition.bestSemiBlueVC.criticalCell.x - 1, sandboxPosition.bestSemiBlueVC.criticalCell.y - 1, 5);
            }
        }
    }

    private void evalFunc(Board board, BoardUI hexBoard){
        SandboxPosition sandboxPosition = new SandboxPosition(board);
        sandboxPosition.findConnections();
        showCarrier(sandboxPosition, board, gameState.getCurrentPlayer());
        hexBoard.drawBoard();
    }

    private void updateLabel(){
        Platform.runLater(() -> {
            Label info = getPlayerInfo();
            if (gameState.isGameFinished()){
                int loser = gameState.getBoardPieces() % 2 == 0 ? 2 : 1;
                if (!(getMode() == GameMode.DOUBLE)){
                    loser = 2;
                }
                String winner = gameState.getCurrentPlayer() == loser ? "Red Player Won" : "Blue Player Won";
                info.setText(winner);
                if (winner.equals("Red Player Won")) {
                    info.setStyle("-fx-text-fill: Red; -fx-font-size: 20px;");
                } else {
                    info.setStyle("-fx-text-fill: Blue; -fx-font-size: 20px;");
                }
            } else {
                String player = gameState.getCurrentPlayer() == 2 ? "Blue Player's Turn" : "Red Player's Turn";
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
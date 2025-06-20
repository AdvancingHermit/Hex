package com.hex.components;

import com.hex.GameMode;
import com.hex.GameState;
import com.hex.gamecontroller.*;

import com.hex.scenes.LocalGame;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

import static java.lang.Math.sqrt;

public class BoardUI extends Pane {

    private int rows;
    private int cols;
    private double size;
    private GameState gameState;
    @Getter
    @Setter
    private Board board;
    private boolean online;
    private GameMode mode;
    @Getter
    private boolean singlePlayer;
    @Getter
    private Runnable updateLabel;

    public enum ControllerType {
        LOCAL_GAME,
        ONLINE,
        ALGORITHM_TESTER
    }
    ControllerType type;
    //Made by Oliver

    //This class is responsible for drawing the board we can see, and make sure the board can be interacted with

    public BoardUI(Board board, double hexagonSize, ControllerType type, GameMode mode, boolean singlePlayer, Runnable updateLabel){
        this.size = hexagonSize;
        this.setBoard(board);
        this.rows = board.getRows();
        this.cols = board.getCols();
        this.type = type;
        this.mode = mode;
        this.singlePlayer = singlePlayer;
        this.updateLabel = updateLabel;
        if (!singlePlayer) {
            switch (type) {
                case LOCAL_GAME:
                    ;
                    if (mode == GameMode.NORMAL || mode == GameMode.SWAP) {
                        GameController.getInstance().setMoveListener(co -> {
                            int curr = GameController.getInstance().getGameState().getCurrentPlayer();
                            int algoPlayerNum = GameController.getInstance().getAlgoPlayerNum();
                            this.setDisable(curr != algoPlayerNum);
                            Platform.runLater(() -> {
                                this.getChildren().clear();
                                this.drawBoard();
                            });
                        });
                    } else if (mode == GameMode.DOUBLE) {
                        DoublePieceController.getInstance().setMoveListener(co -> {
                            int curr = DoublePieceController.getInstance().getGameState().getCurrentPlayer();
                            int algoPlayerNum = DoublePieceController.getInstance().getAlgoPlayerNum();
                            this.setDisable(DoublePieceController.getInstance().getCounter() == 1);
                            Platform.runLater(() -> {
                                this.getChildren().clear();
                                this.drawBoard();
                            });
                        });
                    }
                    break;
                case ONLINE:
                    OnlineController.getInstance().setMoveListener(co -> {
                        Platform.runLater(() -> {
                            this.getChildren().clear();
                            this.drawBoard();
                        });
                    });
                    break;
                case ALGORITHM_TESTER:
                    AlgorithmTesterController.getInstance().setMoveListener(co -> {
                        AlgorithmTesterController.getInstance().gameIteration(co);
                        Platform.runLater(() -> {
                            this.getChildren().clear();
                            this.drawBoard();
                        });
                    });
                    break;
            }
        }

    }

    public void drawBoard() {

        for (int i = 0; i < cols; i++ ) {
            for (int j = 0; j < rows; j++ ) {

                Piece hex = new Piece( new double[] {i, j},  size);

                //adds a mouse click function to each hex so they can be clicked
                hex.setOnMouseClicked(event -> {
                    BoardCoordinate coord = new BoardCoordinate((int) hex.getGridPosition()[0], (int) hex.getGridPosition()[1]);
                    switch (type){
                        case LOCAL_GAME:
                            if (mode == GameMode.NORMAL || mode == GameMode.SWAP) {
                                GameController.getInstance().gameIteration(coord, updateLabel);
                            } else if (mode == GameMode.DOUBLE){
                                DoublePieceController.getInstance().gameIteration(coord, updateLabel);
                            }
                            break;
                        case ONLINE:
                            OnlineController.getInstance().gameIteration(coord);
                            break;
                        case ALGORITHM_TESTER:
                            AlgorithmTesterController.getInstance().gameIteration(coord);
                            break;
                    }
                    if (singlePlayer){
                        this.getChildren().clear();
                        this.drawBoard();
                    }

                });

                if (getBoard().getPiece(i, j) == 0) {
                    hex.setFill(Color.TRANSPARENT);
                } else if (getBoard().getPiece(i, j) == 1) {
                    hex.setFill(Color.RED);
                } else if (getBoard().getPiece(i, j) == 2) {
                    hex.setFill(Color.BLUE);
                }
                // Set the default stroke for the hexagon
                hex.setStroke(Color.grayRgb(45));

                // Add border highlights
                // Top row red borders
                if (j == 0) {
                    hex.setTopLeftEdge(Color.RED);
                    hex.setTopRightEdge(Color.RED);
                }

                // Bottom row red borders
                if (j == rows - 1) {
                    hex.setBottomLeftEdge(Color.RED);
                    hex.setBottomRightEdge(Color.RED);
                }

                // Left column blue borders
                if (i == 0) {
                    hex.setLeftEdge(Color.BLUE);
                    hex.setBottomLeftEdge(Color.BLUE);
                }

                // Right column blue borders
                if (i == cols - 1) {
                    hex.setTopRightEdge(Color.BLUE);
                    hex.setRightEdge(Color.BLUE);
                }

                getChildren().add(hex);
            }
        }
    }
    @Override
    protected double computePrefWidth(double width) {
        return sqrt(3)*size* cols + rows* sqrt(3)*size / 2;
    }
    @Override
    protected double computePrefHeight(double height) {
        return (3.0/2.0) * size * rows;
    }

}

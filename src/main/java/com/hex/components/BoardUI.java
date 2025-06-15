package com.hex.components;

import com.hex.GameState;
import com.hex.gamecontroller.AlgorithmTesterController;

import com.hex.gamecontroller.AbstractGameController;
import com.hex.gamecontroller.DoublePieceController;

import com.hex.gamecontroller.GameController;
import com.hex.gamecontroller.OnlineController;
import com.hex.scenes.LocalGame;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;

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

    public enum ControllerType {
        LOCAL_GAME,
        ONLINE,
        ALGORITHM_TESTER
    }
    ControllerType type;

    public BoardUI(Board board, double hexagonSize, ControllerType type){

        
        this.size = hexagonSize;
        this.setBoard(board);
        this.rows = board.getRows();
        this.cols = board.getCols();
        this.type = type;

    }

    public void drawBoard() {

        for (int i = 0; i < cols; i++ ) {
            for (int j = 0; j < rows; j++ ) {

                Piece hex = new Piece( new double[] {i, j},  size);

                hex.setOnMouseClicked(event -> {
                    BoardCoordinate coord = new BoardCoordinate((int) hex.getGridPosition()[0], (int) hex.getGridPosition()[1]);

                    switch (type){
                        case LOCAL_GAME:
                            GameController.getInstance().gameIteration(coord);
                            break;
                        case ONLINE:
                            OnlineController.getInstance().gameIteration(coord);
                            break;
                        case ALGORITHM_TESTER:
                            AlgorithmTesterController.getInstance().gameIteration(coord);
                            break;
                    }
                    getChildren().clear();
                    drawBoard();
                });


                if (getBoard().getPiece(i, j) == 0) {
                    hex.setFill(Color.TRANSPARENT);
                } else if (getBoard().getPiece(i, j) == 1) {
                    hex.setFill(Color.BLUE);
                } else if (getBoard().getPiece(i, j) == 2) {
                    hex.setFill(Color.RED);
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

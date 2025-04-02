package com.hex.components;

import com.hex.GameState;
import com.hex.gamecontroller.Controller;
import com.hex.gamecontroller.GameController;
import com.hex.gamecontroller.OnlineController;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import static java.lang.Math.sqrt;

public class BoardUI extends Pane {
    int rows;
    int cols;
    double size;
    GameState gameState;
    Board board;
    boolean online;

    public BoardUI(Board board, double hexagonSize,  boolean online){
        this.size = hexagonSize;
        this.board = board;
        this.rows = board.rows;
        this.cols = board.cols;
        this.online = online;
    }

    public Board getBoard() {
        return board;
    }

    public void drawBoard() {
        

        for (int i = 0; i < cols; i++ ) {
            for (int j = 0; j < rows; j++ ) {

                Piece hex = new Piece( new double[] {i, j},  size);

                hex.setOnMouseClicked(event -> {
                    BoardCoordinate coord = new BoardCoordinate((int) hex.getGridPosition()[0], (int) hex.getGridPosition()[1]);
                    if(online) {
                        OnlineController.gameIteration(coord);
                    } else {
                        GameController.gameIteration(coord);
                    }
                    getChildren().clear();
                    drawBoard();
                });


                if (board.getPiece(i, j) == 0) {
                    hex.setFill(Color.TRANSPARENT);
                } else if (board.getPiece(i, j) == 1) {
                    hex.setFill(Color.BLUE);
                } else if (board.getPiece(i, j) == 2) {
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

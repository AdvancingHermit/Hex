package com.hex.components;

import com.hex.GameState;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import static java.lang.Math.sqrt;

public class BoardDrawer extends Pane {
    int rows;
    int cols;
    double size;
    GameState gameState;

    Board board;

    public BoardDrawer(int rows, int cols, double hexagonSize, GameState gameState){
        this.size = hexagonSize;
        this.rows = rows;
        this.cols = cols;
        this.gameState = gameState;
        board = new Board(rows, cols, gameState);
    }

    public void pieceClicked(Piece hex) {
        System.out.println("Hex clicked! " + hex.gridPosition[0] + " " + hex.gridPosition[1]);
        if (board.getPiece((int) hex.gridPosition[0], (int) hex.gridPosition[1]) == 0){
            board.setPiece((int) hex.gridPosition[0], (int) hex.gridPosition[1], gameState.getCurrentPlayer());
            gameState.nextPlayer();
        }
    }

    public void drawBoard() {
        Line line = new Line(-size, 0, -size + rows* sqrt(3.0)*size / 2.0, computePrefHeight(0) + 10);
        line.setStrokeWidth(10);  // Set thickness
        line.setStroke(Color.BLUE);
        getChildren().add(line);

        for (int i = 0; i < cols; i++ ) {
            for (int j = 0; j < rows; j++ ) {

                Piece hex = new Piece( new double[] {i, j},  size);

                hex.setOnMouseClicked(event -> {
                    pieceClicked(hex);
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

                if ((i == 0 || i == cols-1) && (j == 0 || j == rows-1)) {
                    hex.setStroke(Color.rgb(255, 0, 255));
                }
                else if (i == 0 || i == cols-1) {
                    hex.setStroke(Color.BLUE);
                }
                else if (j == 0 || j == rows-1) {
                    hex.setStroke(Color.RED);
                }
                else {
                    hex.setStroke(Color.grayRgb(45));
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

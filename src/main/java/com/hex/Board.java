package com.hex;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import static java.lang.Math.sqrt;

public class Board extends Pane {
    private int[][] board;
    int rows;
    int cols;
    double size;

    public Board(int rows, int cols, double hexagonSize) {
        this.rows = rows;
        this.cols = cols;
        this.size = hexagonSize;
        this.board = new int[rows][cols];
    }
    public void setPiece(int x, int y, int player) {
        board[x][y] = player;
    }
    public void drawBoard() {
        for (int i = 0; i < cols; i++ ) {
            for (int j = 0; j < rows; j++ ) {

                Piece hex = new Piece( new double[] {i, j},  size);

                hex.setOnMouseClicked(event -> {
                    System.out.println("Hex clicked! " + hex.gridPosition[0] + " " + hex.gridPosition[1]);
                    setPiece((int) hex.gridPosition[0], (int) hex.gridPosition[1], 1);
                    getChildren().clear();
                    drawBoard();
                });

                if (board[i][j] == 0) {
                    hex.setFill(Color.TRANSPARENT);
                } else if (board[i][j] == 1) {
                    hex.setFill(Color.BLUE);
                } else if (board[i][j] == 2) {
                    hex.setFill(Color.RED);
                }

                hex.setStroke(Color.grayRgb(45));
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
        return 3.0/2.0 * size * rows;
    }
}

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
        board = new int[rows][cols];
    }
    public void setPiece(int x, int y, int player) {
        board[x][y] = player;
    }
    public void drawBoard() {
        for (int i = 0; i < cols; i++ ) {
            for (int j = 0; j < rows; j++ ) {
                double horSpacing = sqrt(3)*size;
                double verSpacing = 3.0/2.0 * size;
                double x = i * horSpacing + j * (horSpacing / 2 ) + size;
                double y = j * verSpacing + size;
                Hexagon hex = new Hexagon( new double[] {x, y},  size);
                hex.setFill(Color.TRANSPARENT);
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

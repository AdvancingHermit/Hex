package com.hex.components;

import com.hex.GameState;
import com.hex.gamecontroller.GameController;
import com.hex.gamecontroller.OnlineController;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import static java.lang.Math.sqrt;

public class BoardUI extends Pane {
    private int rows;
    private int cols;
    private double size;
    private GameState gameState;
    private Board board;
    private boolean online;

    public BoardUI(Board board, double hexagonSize,  boolean online){
        this.size = hexagonSize;
        this.setBoard(board);
        this.rows = board.getRows();
        this.cols = board.getCols();
        this.online = online;
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
                    BoardCoordinate coord = new BoardCoordinate((int) hex.getGridPosition()[0], (int) hex.getGridPosition()[1]);
                    if(online) {
                        OnlineController.getInstance().gameIteration(coord);
                    } else {
                        GameController.getInstance().gameIteration(coord);
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

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}

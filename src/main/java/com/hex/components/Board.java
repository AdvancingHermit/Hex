package com.hex.components;

import com.hex.GameState;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class Board extends Pane {
    private int[][] board;
    private GameState gameState;
    int rows;
    int cols;
    double size;

    public Board(int rows, int cols, double hexagonSize, GameState gameState) {
        this.rows = rows;
        this.cols = cols;
        this.size = hexagonSize;
        this.board = new int[rows][cols];
        this.gameState = gameState;
    }
    public void setPiece(int x, int y, int player)  {
        board[x][y] = player;
        System.out.println(checkWin(player));
    }
    public void pieceClicked(Piece hex) {
        System.out.println("Hex clicked! " + hex.gridPosition[0] + " " + hex.gridPosition[1]);
        if (board[(int) hex.gridPosition[0]][(int) hex.gridPosition[1]] == 0){
            setPiece((int) hex.gridPosition[0], (int) hex.gridPosition[1], gameState.getCurrentPlayer());
            gameState.nextPlayer();
        }
        getChildren().clear();
        drawBoard();
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
                });

                if (board[i][j] == 0) {
                    hex.setFill(Color.TRANSPARENT);
                } else if (board[i][j] == 1) {
                    hex.setFill(Color.BLUE);
                } else if (board[i][j] == 2) {
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

    private boolean checkWin(int player) {
        if (player == 1) {
            for (int i = 0; i < rows; i++) {
                if (board[0][i] == 1) {
                    if (depthFirstSearch(0, i, player)) {
                        replacePieces(player);
                        return true;
                    }
                }
            }
        }
        if (player == 2) {
            for (int i = 0; i < cols; i++) {
                if (board[i][0] == 2) {
                    if (depthFirstSearch(i, 0, player)) {
                        replacePieces(player);
                        return true;
                    }
                }
            }
        }
        replacePieces(player);
        return false;
    }

    private void replacePieces(int player) {
        for (int i = 0; i < cols; i++ ) {
            for (int j = 0; j < rows; j++ ) {
                if (board[i][j] == -1) {
                    board[i][j] = player;
                }
            }
        }

    }
    private boolean depthFirstSearch(int x, int y, int player) {
        ArrayList<int[]> neighbours = getNeighbours(x, y, player);
        board[x][y] = -1;
        while (!neighbours.isEmpty()) {
            x = neighbours.get(0)[0];
            y = neighbours.get(0)[1];
            if (player == 1 && x == cols-1){
                return true;
            }
            if (player == 2 && y == rows-1) {
                return true;
            }
            neighbours.remove(0);
            neighbours.addAll(getNeighbours(x, y, player));
            board[x][y] = -1;
        }

        return false;
    }
    private ArrayList<int[]> getNeighbours(int x, int y, int player)  {
        ArrayList<int[]> neighbours = new ArrayList<>();
        if (x >= cols || y >= rows || x < 0 || y < 0) {
            System.out.println("Out of bounds");
            return neighbours;
        }
        if (x+1 < cols && board[x+1][y] == player) {
            neighbours.add(new int[] {x+1, y});
        }
        if (x > 0 && board[x-1][y] == player) {
            neighbours.add(new int[] {x-1, y});
        }
        if (y+1 < rows && board[x][y+1] == player) {
            neighbours.add(new int[] {x, y+1});
        }
        if (y > 0 && board[x][y-1] == player) {
            neighbours.add(new int[] {x, y-1});
        }
        if (x+1 < cols && y > 0 && board[x+1][y-1] == player) {
            neighbours.add(new int[] {x+1, y-1});
        }
        if (y+1 < rows && x > 0 && board[x-1][y+1] == player) {
            neighbours.add(new int[] {x-1, y+1});
        }
        return neighbours;
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

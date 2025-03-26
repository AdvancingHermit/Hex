package com.server;

import com.server.GameState;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class Board {
    private int[][] board;

    int rows;
    int cols;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new int[rows][cols];
    }

    public void setPiece(int x, int y, int player)  {
        board[x][y] = player;
        System.out.println(checkWin(player));
    }

    public boolean checkWin(int player) {
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
                        return false;
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

    public int getPiece(int x, int y){
        return board[x][y];
    }
}

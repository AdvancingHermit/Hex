package com.hex.components;

import com.hex.GameState;
import lombok.Data;

// Made by Oscar
@Data
public class Board {
    protected int[][] board;
    protected GameState gameState;
    protected int rows;
    protected int cols;
    protected int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, -1}, {-1, 1}
    };


    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new int[rows][cols];
    }

    public Board(int rows, int cols, GameState gameState) {
        this.rows = rows;
        this.cols = cols;
        this.board = new int[rows][cols];
        this.gameState = gameState;
    }

    public Board(Board other) {
        this(other.getRows(), other.getCols(), other.getGameState());

        for (int i = 0; i < other.getRows(); i++) {
            System.arraycopy(other.getBoard()[i], 0, this.getBoard()[i], 0, other.getCols());
        }
    }

    public void setPiece(int x, int y, int player)  {
        board[x][y] = player;
        //System.out.println(checkWin(player));
    }

    public boolean checkWin(int player) {
        int limit = (player == 2) ? rows : cols;

        for (int i = 0; i < limit; i++) {
            int row = (player == 2) ? 0 : i;
            int col = (player == 1) ? 0 : i;

            if (board[row][col] == player && depthFirstSearch(row, col, player)) {
                replacePieces(player);
                return true;
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
        board[x][y] = -1;

        if (player == 2 && x == cols - 1 || player == 1 && y == rows - 1) {
            return true;
        }

        return visitNeighbours(x, y, player);
    }

    private boolean visitNeighbours(int x, int y, int player)  {
        boolean finished = false;
        for (int[] dir : directions) {
            int newX = x + dir[0], newY = y + dir[1];
            if (newX >= 0 && newX < cols && newY >= 0 && newY < rows && board[newX][newY] == player) {
               finished = depthFirstSearch(newX, newY, player) || finished;
            }
        }
        return finished;
    }

    public int getPiece(int x, int y){
        return board[x][y];
    }

    public boolean swapAvailable() {
        int counter = 0;
        for (int i = 0; i < cols; i++ ) {
            for (int j = 0; j < rows; j++ ) {
                if (board[i][j] != 0) {
                    counter++;
                }
            }
        }
        return counter == 1;
    }
}

package com.hex.components;

import com.hex.GameState;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Board {
    private int[][] board;
    private GameState gameState;
    private int rows;
    private int cols;

    public Board(int rows, int cols) {
        setRows(rows);
        setCols(cols);
        setBoard(new int[rows][cols]);
    }

    public Board(int rows, int cols, GameState gameState) {
        setRows(rows);
        setCols(cols);
        setBoard(new int[rows][cols]);
        setGameState(gameState);
    }

    public void setPiece(int x, int y, int player)  {
        getBoard()[x][y] = player;
        //System.out.println(checkWin(player));
    }

    public boolean checkWin(int player) {
        if (player == 1) {
            for (int i = 0; i < getRows(); i++) {
                if (getBoard()[0][i] == 1) {
                    if (depthFirstSearch(0, i, player)) {
                        replacePieces(player);
                        return true;
                    }
                }
            }
        }
        if (player == 2) {
            for (int i = 0; i < getCols(); i++) {
                if (getBoard()[i][0] == 2) {
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
        for (int i = 0; i < getCols(); i++ ) {
            for (int j = 0; j < getRows(); j++ ) {
                if (getBoard()[i][j] == -1) {
                    getBoard()[i][j] = player;
                }
            }
        }

    }
    private boolean depthFirstSearch(int x, int y, int player) {
        ArrayList<int[]> neighbours = getNeighbours(x, y, player);
        getBoard()[x][y] = -1;
        while (!neighbours.isEmpty()) {
            x = neighbours.get(0)[0];
            y = neighbours.get(0)[1];
            if (player == 1 && x == getCols() -1){
                return true;
            }
            if (player == 2 && y == getRows() -1) {
                return true;
            }
            neighbours.remove(0);
            neighbours.addAll(getNeighbours(x, y, player));
            getBoard()[x][y] = -1;
        }

        return false;
    }
    private ArrayList<int[]> getNeighbours(int x, int y, int player)  {
        ArrayList<int[]> neighbours = new ArrayList<>();
        if (x >= getCols() || y >= getRows() || x < 0 || y < 0) {
            System.out.println("Out of bounds");
            return neighbours;
        }
        if (x+1 < getCols() && getBoard()[x+1][y] == player) {
            neighbours.add(new int[] {x+1, y});
        }
        if (x > 0 && getBoard()[x-1][y] == player) {
            neighbours.add(new int[] {x-1, y});
        }
        if (y+1 < getRows() && getBoard()[x][y+1] == player) {
            neighbours.add(new int[] {x, y+1});
        }
        if (y > 0 && getBoard()[x][y-1] == player) {
            neighbours.add(new int[] {x, y-1});
        }
        if (x+1 < getCols() && y > 0 && getBoard()[x+1][y-1] == player) {
            neighbours.add(new int[] {x+1, y-1});
        }
        if (y+1 < getRows() && x > 0 && getBoard()[x-1][y+1] == player) {
            neighbours.add(new int[] {x-1, y+1});
        }
        return neighbours;
    }

    public int getPiece(int x, int y){
        return getBoard()[x][y];
    }

}

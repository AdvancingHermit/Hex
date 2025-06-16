package com.hex.algorithms.minimax;

import com.hex.components.Board;

import java.util.ArrayList;

public class YPosition implements Position  {
    private int[][] yBoard;
    private int yRows;
    private int yCols;
    private Board board;
    ArrayList<Move> possibleMoves;


    public YPosition(YPosition other) {
        this.yRows = other.yRows;
        this.yCols = other.yCols;

        this.yBoard = new int[yRows][yCols];
        for (int i = 0; i < yRows; i++) {
            System.arraycopy(other.yBoard[i], 0, this.yBoard[i], 0, yCols);
        }

        this.board = new Board(other.board);

        possibleMoves = other.getPossibleMoves();
    }
    public YPosition(Board boardObj){
        board = boardObj;
        possibleMoves = new ArrayList<Move>();
        for (int i = 0; i < board.getCols(); i++){
            for (int j = 0; j < board.getRows(); j++){
                if (board.getPiece(i, j) == 0 ) { possibleMoves.add(new Move(i, j)); }
            }
        }
        createYBoard();
        AddToYBoard();
    }

    private void createYBoard(){
        yRows = 2*board.getRows()-1;
        yCols = 2* board.getCols()-1;

        yBoard = new int[yRows][yCols];
        // color bot
        for (int i = 0; i < board.getRows()-1; i++){
            for (int j = 0; j < board.getRows()- 1 - i; j++){
                yBoard[i + board.getRows()][j] = 2;
            }
        }

        // Color Right
        for (int i = 0; i < board.getRows(); i++){
            for (int j = 0; j < board.getCols()-1-i; j++){
                yBoard[i][board.getCols() + j] = 1;
            }
        }
    }

    private void AddToYBoard(){
        for (int i = 0; i < board.getRows(); i++){
            for (int j = 0; j < board.getCols(); j++){
                yBoard[i][j] = board.getPiece(i, j);
            }
        }
    }

    public YPosition Move(Move move, int player){
        YPosition other = new YPosition(this);
        other.placeMove(move, player);
        other.removeFromPossibleMoves(move);
        System.out.println(move + " With " + player);
        return other;
    }

    @Override
    public void placeMove(Move move, int player){
        yBoard[move.x][move.y] = player;
        board.setPiece(move.x, move.y, player);
    }


    @Override
    public float evaluate(int player) {
        System.out.println("WOWIDEK");
        int n = yRows;
        float[][][] eval = new float[yRows][yCols][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int cell = yBoard[i][j];
                if (cell == player) { eval[i][j][n - 1] = 1; }
                else if (cell == 0) { eval[i][j][n - 1] =  0; }
                else { eval[i][j][n - 1] = -1; }
            }
        }
        for (int k = n - 2; k >= 0; k--) {
            for (int i = 0; i <= k; i++) {
                for (int j = 0; j <= k - i; j++) {
                    eval[i][j][k] = f(
                            eval[i][j][k + 1],
                            eval[i + 1][j][k + 1],
                            eval[i][j + 1][k + 1]
                    );
                }
            }
        }
        System.out.println(eval[0][0][0]);
        return eval[0][0][0];
    }

    private float f(float p, float q, float r) {
        return 0.5f * (p + q + r - p * q * r);
    }

    @Override
    public int getSize() {
        return board.getRows();
    }

    @Override
    public void removeFromPossibleMoves(Move move) {
        possibleMoves.remove(move);
    }

    @Override
    public boolean checkWin(int player) {
        return board.checkWin(player);
    }

    @Override
    public ArrayList<Move> getPossibleMoves() {
        return new ArrayList<>(possibleMoves);
    }

    public Move getMiddleMove(){
        return new Move(board.getCols()/2, board.getRows()/2);
    }

    @Override
    public void setplayerOnTurn(boolean playerOnTurn) {

    }
    public int[][] getBoard(){
        return board.getBoard();
    }

}

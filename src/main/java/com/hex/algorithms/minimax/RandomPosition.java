package com.hex.algorithms.minimax;

import com.hex.components.Board;

import java.util.ArrayList;
import java.util.Random;

public class RandomPosition implements Position{
    private Board board;
    ArrayList<Move> possibleMoves;

    public RandomPosition(RandomPosition other) {

        this.board = new Board(other.board);
        possibleMoves = other.getPossibleMoves();

    }
    public RandomPosition(Board boardObj){
        board = boardObj;
        possibleMoves = new ArrayList<Move>();
        for (int i = 0; i < board.getCols(); i++){
            for (int j = 0; j < board.getRows(); j++){
                if (board.getPiece(i, j) == 0 ) { possibleMoves.add(new Move(i, j)); }
            }
        }
    }

    public Move getMiddleMove(){
        return new Move(board.getCols()/2, board.getRows()/2);
    }

    @Override
    public Position Move(Move move, int player) {
        RandomPosition other = new RandomPosition(this);
        other.placeMove(move, player);
        other.removeFromPossibleMoves(move);
        System.out.println(move + " With " + player);
        return other;
    }

    @Override
    public float evaluate(int player) {
        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);
        return rand.nextFloat(0,1);
    }

    @Override
    public int getSize() {
        return board.getRows();
    }

    @Override
    public void placeMove(Move move, int player) {
        board.setPiece(move.x, move.y, player);
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

    @Override
    public void setplayerOnTurn(boolean playerOnTurn) {

    }

    public int[][] getBoard(){
        return board.getBoard();
    }
}

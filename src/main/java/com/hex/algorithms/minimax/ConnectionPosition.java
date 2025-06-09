package com.hex.algorithms.minimax;

import com.hex.components.Board;

import java.util.ArrayList;

public class ConnectionPosition implements Position{
    private Connections board;
    ArrayList<Move> possibleMoves;
    private boolean playerOnTurn;

    public ConnectionPosition(ConnectionPosition other) {
        this.board = new Connections(other.board);
        this.playerOnTurn = other.playerOnTurn;
        possibleMoves = other.getPossibleMoves();
    }
    public ConnectionPosition(Board boardObj){
        board = new Connections(boardObj);
        possibleMoves = new ArrayList<>();
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
    public int getSize(){
        return board.getRows();
    }

    @Override
    public Position Move(Move move, int player) {
        ConnectionPosition other = new ConnectionPosition(this);
        other.placeMove(move, player);
        other.removeFromPossibleMoves(move);
        return other;
    }

    @Override
    public float evaluate(int player) {
        int defVal = board.getRows() * board.getCols();
        int blueDepth = defVal;
        int blueSemiDepth = defVal;
        int redDepth = defVal;
        int redSemiDepth = defVal;

        board.HProcess();

        if (board.bestBlueVC != null){
            blueDepth = board.bestBlueVC.depth;
        } if (board.bestBlueSemiVC != null){
            blueSemiDepth = board.bestBlueSemiVC.depth;
        } if (board.bestRedVC != null){
            redDepth = board.bestRedVC.depth;
        } if (board.bestRedSemiVC != null){
            redSemiDepth = board.bestRedSemiVC.depth;
        }

        if (player == Colors.BLUE.getValue()) {
            if (playerOnTurn) {
                return redDepth - Math.min(blueDepth, blueSemiDepth);
            }
            return Math.min(redDepth, redSemiDepth) - blueDepth;
        } else{
            if (playerOnTurn) {
                return blueDepth - Math.min(redDepth, redSemiDepth);
            }
            return Math.min(blueDepth, blueSemiDepth) - redDepth;
        }
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
        this.playerOnTurn = playerOnTurn;
    }

    public int[][] getBoard(){
        return board.getBoard();
    }
}

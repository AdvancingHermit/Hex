package com.hex.algorithms.minimax;

import com.hex.algorithms.minimax.connectionsHelpers.SetHolder;
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
    public float evaluate(int player, SetHolder setHolder) {
        int defVal = board.getRows() * board.getCols();
        int blueDepth = defVal;
        int blueSemiDepth = defVal;
        int redDepth = defVal;
        int redSemiDepth = defVal;

        int[] evalArr = board.HProcess(setHolder);

        if (blueDepth > evalArr[0]) blueDepth = evalArr[0];
        if (blueSemiDepth > evalArr[1]) blueSemiDepth = evalArr[1];
        if (redDepth > evalArr[2]) redDepth = evalArr[2];
        if (redSemiDepth > evalArr[3]) redSemiDepth = evalArr[3];


        if (player == Colors.BLUE.getValue()) {
            if (playerOnTurn) {
                if (blueDepth * 2 == redDepth + blueSemiDepth && blueDepth == defVal && redSemiDepth != defVal) return (float) (redSemiDepth*-0.01);
                return redDepth - Math.min(blueDepth, blueSemiDepth);
            }
            if (blueDepth * 2 == redDepth + redSemiDepth && blueDepth == defVal && blueSemiDepth != defVal) return (float) (blueSemiDepth*0.01);
            return Math.min(redDepth, redSemiDepth) - blueDepth;
        } else{
            if (playerOnTurn) {
                if (blueDepth * 2 == redDepth + redSemiDepth && blueDepth == defVal && blueSemiDepth != defVal) return (float) (blueSemiDepth*-0.01);
                return blueDepth - Math.min(redDepth, redSemiDepth);
            }
            if (blueDepth * 2 == redDepth + blueSemiDepth && blueDepth == defVal && redSemiDepth != defVal) return (float) (redSemiDepth*0.01);
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

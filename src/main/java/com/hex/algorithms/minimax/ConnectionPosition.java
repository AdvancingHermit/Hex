package com.hex.algorithms.minimax;

import com.hex.algorithms.minimax.connectionsHelpers.SetHolder;
import com.hex.components.Board;

import java.util.ArrayList;

// Christian
public class ConnectionPosition implements Position{
    protected Connections board;
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
        float eval = defVal;

        int[] evalArr = board.HProcess(setHolder);

        if (blueDepth > evalArr[0]) blueDepth = evalArr[0];
        if (blueSemiDepth > evalArr[1]) blueSemiDepth = evalArr[1];
        if (redDepth > evalArr[2]) redDepth = evalArr[2];
        if (redSemiDepth > evalArr[3]) redSemiDepth = evalArr[3];

        blueSemiDepth = Math.min(blueDepth, blueSemiDepth);
        redSemiDepth = Math.min(redDepth, redSemiDepth);

        if (player == Colors.BLUE.getValue()) {
            if (playerOnTurn) {
                if (blueSemiDepth <= redDepth && blueSemiDepth != defVal) eval = defVal - blueSemiDepth;
                else if (redDepth < blueSemiDepth) eval = redDepth - defVal;
                else if (redSemiDepth != defVal) eval = redSemiDepth*0.01f - 1;
            }
            else {
                if (blueDepth < redSemiDepth) eval = defVal - blueDepth;
                else if (redSemiDepth <= blueDepth && redSemiDepth != defVal) eval = redSemiDepth - defVal;
                else if (blueSemiDepth != defVal) eval = 1 - blueSemiDepth*0.01f;
            }
        }
        if (player == Colors.RED.getValue()) {
            if (playerOnTurn) {
                if (redSemiDepth <= blueDepth && redSemiDepth != defVal) eval = defVal - redSemiDepth;
                else if (blueDepth < redSemiDepth) eval = blueDepth - defVal;
                else if (blueSemiDepth != defVal) eval = blueSemiDepth*0.01f - 1;
            }
            else {
                if (redDepth < blueSemiDepth) eval = defVal - redDepth;
                else if (blueSemiDepth <= redDepth && blueSemiDepth != defVal) eval = blueSemiDepth - defVal;
                else if (redSemiDepth != defVal) eval = 1 - redSemiDepth*0.01f;
            }
        }
        if (eval == defVal) eval = 0;

        return eval;
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

    public void addPossibleMove(Move move){
        possibleMoves.add(move);
    }
}

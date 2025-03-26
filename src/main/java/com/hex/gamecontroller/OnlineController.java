package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;
import com.hex.components.Piece;

public class OnlineController implements Controller {

    private static Board board;
    private static GameState gameState;
    private static java.util.function.Consumer<int[]> onMoveHandler;


    public OnlineController(Board board, GameState gameState, java.util.function.Consumer<int[]> onMoveHandler){
        this.board = board;
        this.gameState = gameState;
        this.onMoveHandler = onMoveHandler;
    }

    public static void gameIteration(BoardCoordinate coord) {
        if (gameState.isGameFinished()) {
            return;
        }
        placePiece(coord);
    }

    private static void placePiece(BoardCoordinate coord) {
        int x = coord.x;
        int y = coord.y;
        System.out.println("Hex clicked! " + x + " " + y);
        boolean myTurn = gameState.getCurrentPlayer() == gameState.getPlayerNum();
        boolean emptySpot = board.getPiece(x, y) == 0;
        if ((gameState.getSwap()|| emptySpot) && onMoveHandler != null && myTurn) {
            if (gameState.getSwap() && !emptySpot) {
                board.setPiece(x, y, 0);
                board.setPiece(y, x, gameState.getCurrentPlayer());
            } else {
                board.setPiece(x, y, gameState.getCurrentPlayer());
            }

            onMoveHandler.accept(new int[]{x, y});
            gameState.nextPlayer();
            gameState.setSwap(false);
        }

    }

    private static void updateBoard(int player){
        if (board.checkWin(player)){
            System.out.println("Player " + gameState.getCurrentPlayer() + " won");
            gameState.setGameFinished(true);
        }
    }



}

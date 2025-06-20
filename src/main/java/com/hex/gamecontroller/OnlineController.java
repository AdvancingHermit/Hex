package com.hex.gamecontroller;

import com.hex.GameState;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

import java.util.function.Consumer;

public class OnlineController extends Controller {

    private static OnlineController INSTANCE;

    private static Board board;
    private static GameState gameState;
    private static Consumer<int[]> onMoveHandler;

    public static OnlineController getInstance() {
        return INSTANCE;
    }

    public static void createOnlineController(Board board, GameState gameState, Consumer<int[]> onMoveHandler) {
        INSTANCE = new OnlineController(board, gameState, onMoveHandler);
    }

    private OnlineController(Board board, GameState gameState, Consumer<int[]> onMoveHandler){
        this.board = board;
        this.gameState = gameState;
        this.onMoveHandler = onMoveHandler;
    }

    public void gameIteration(BoardCoordinate coord) {
        if (gameState.isGameFinished()) {
            return;
        }
        placePiece(coord);
    }

    public  void placePiece(BoardCoordinate coord) {
        int x = coord.x;
        int y = coord.y;
        System.out.println("Hex clicked! " + x + " " + y);
        boolean myTurn = gameState.getCurrentPlayer() == gameState.getPlayerNum();
        System.out.println("cp: " + gameState.getCurrentPlayer() + " pn: " + gameState.getPlayerNum());
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
            notifyMoveListener(coord);
        }

    }

}

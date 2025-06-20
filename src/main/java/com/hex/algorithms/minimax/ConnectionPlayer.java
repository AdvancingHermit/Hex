package com.hex.algorithms.minimax;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

// Christian
public class ConnectionPlayer extends MiniMax implements Algorithm {
    boolean hasFoundWin;
    public ConnectionPlayer(){ hasFoundWin = false; };
    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations, boolean swap) {
        //System.out.println("Started");
        Position startPos = new ConnectionPosition(board);
        boolean doSwap = false;
        if (gameState.getBoardPieces() == 1 && swap && player == 2) {
            doSwap = true;
            for (int x = 0; x < board.getCols(); x++){
                for (int y = 0; y < board.getRows(); y++){
                    if (board.getPiece(x, y) == 3 - player) startPos.addPossibleMove(new Move(x, y));
                }
            }
        }
        int depth;
        depth = switch (iterations) {
            case 50_000 -> 1;
            case 250_000 -> 3;
            case 1_000_000 -> 4;
            case 5_000_000 -> 5;
            default -> 3;
        };
        //if (hasFoundWin) depth = 1;
        startPos.setplayerOnTurn(depth % 2 == 0);
        MoveValue suggestedMove = findBestMove(startPos, player, depth);
        //if (suggestedMove.value > 1) hasFoundWin = true;
        return new BoardCoordinate(suggestedMove.move.x, suggestedMove.move.y);
    }
}

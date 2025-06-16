package com.hex.algorithms.minimax;

import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

public class ConnectionPlayer extends MiniMax implements Algorithm {
    public ConnectionPlayer(){};
    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations, boolean swap) {
        System.out.println("Started");
        Position startPos = new ConnectionPosition(board);
        if (maxDepth % 2 == 0){
            startPos.setplayerOnTurn(true);
        } else {
            startPos.setplayerOnTurn(false);
        }
        Move suggestedMove = findBestMove(startPos, player);
        return new BoardCoordinate(suggestedMove.x, suggestedMove.y);
    }
}

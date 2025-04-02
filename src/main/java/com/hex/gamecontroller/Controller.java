package com.hex.gamecontroller;

import com.hex.components.BoardCoordinate;
import com.hex.components.Piece;

public interface Controller {

    void gameIteration(BoardCoordinate coord);

    void placePiece(BoardCoordinate co);
}

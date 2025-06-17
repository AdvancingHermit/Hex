package com.hex.gamecontroller;

import com.hex.components.BoardCoordinate;
import com.hex.components.Piece;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public abstract class Controller {

    protected Consumer<BoardCoordinate> moveListener;
    public void setMoveListener(Consumer<BoardCoordinate> ml) {
        this.moveListener = ml;
    }

    abstract void gameIteration(BoardCoordinate coord);

    abstract void placePiece(BoardCoordinate co);

    public void notifyMoveListener(BoardCoordinate co) {
        if (this.moveListener == null) {
            return;
        }
        try {
            this.moveListener.accept(co);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

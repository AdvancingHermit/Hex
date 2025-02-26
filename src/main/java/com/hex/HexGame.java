package com.hex;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class HexGame extends BaseScene {
    HexGame(HexApp hexApp) {
        StackPane root = new StackPane();
        Board hexBoard = new Board(5, 5, 30);
        hexBoard.setPiece(3, 1, 1);
        hexBoard.setPiece(3, 2, 2);
        hexBoard.drawBoard();
        root.getChildren().add(hexBoard);

        scene = new Scene(root, 800, 600);

    }

}

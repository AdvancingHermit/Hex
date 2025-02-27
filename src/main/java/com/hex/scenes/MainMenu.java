package com.hex.scenes;

import com.hex.HexApp;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainMenu extends BaseScene {
    public MainMenu(HexApp hexApp) {
        StackPane root = new StackPane();

        Button playButton = new Button("Play!");
        playButton.setOnAction(e -> hexApp.switchToScene(HexApp.Scenes.HEXGAME));
        root.getChildren().add(playButton);

        scene = new Scene(root, 800, 600);

    }
}

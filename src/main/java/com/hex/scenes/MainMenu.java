package com.hex.scenes;

import com.hex.HexApp;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenu extends BaseScene {
    public MainMenu(HexApp hexApp) {
        StackPane root = new StackPane();
        VBox vbox = new VBox(10);

        Button playOnlineBtn = new Button("Play Online!");
        playOnlineBtn.setOnAction(e -> hexApp.switchToScene(HexApp.Scenes.ONLINE));
        vbox.getChildren().add(playOnlineBtn);
        Button playLocalBtn = new Button("Play Local!");
        playLocalBtn.setOnAction(e -> hexApp.switchToScene(HexApp.Scenes.LOCAL));
        vbox.getChildren().add(playLocalBtn);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        root.getChildren().add(vbox);
        scene = new Scene(root, 800, 600);

    }
}

package com.hex.scenes;

import com.hex.HexApp;
import com.hex.SceneManager;
import com.hex.SceneManager.SceneType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenu extends BaseScene {
    public MainMenu(SceneManager sceneManager) {
        StackPane root = new StackPane();
        VBox vbox = new VBox(10);

        Button playOnlineBtn = new Button("Play Online!");
        playOnlineBtn.setOnAction(e -> sceneManager.switchScene(SceneType.LOADING_SCREEN));
        vbox.getChildren().add(playOnlineBtn);
        Button playLocalBtn = new Button("Play Local!");
        playLocalBtn.setOnAction(e -> sceneManager.switchScene(SceneType.LOCAL_GAME_SETTINGS));
        vbox.getChildren().add(playLocalBtn);
        Button playAIsBtn = new Button("Test AI!");
        playAIsBtn.setOnAction(e -> sceneManager.switchScene(SceneType.ALGO_V_ALGO_SETTINGS));
        vbox.getChildren().add(playAIsBtn);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        root.getChildren().add(vbox);
        scene = new Scene(root, 800, 600);

    }
}

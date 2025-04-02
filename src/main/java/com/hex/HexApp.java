package com.hex;


import javafx.application.Application;
import javafx.stage.Stage;
import com.hex.SceneManager.SceneType;

public class HexApp extends Application {
    private SceneManager sceneManager;
    @Override
    public void start(Stage stage) {
        stage.setTitle("Hex Game Board");
        sceneManager = new SceneManager(stage);
        sceneManager.switchScene(SceneType.MAIN_MENU);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
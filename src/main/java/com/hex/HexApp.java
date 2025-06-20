package com.hex;


import javafx.application.Application;
import javafx.stage.Stage;
import com.hex.SceneManager.SceneType;
import com.hex.algorithms.dqn.Driver;

import java.net.URISyntaxException;
// Made by Oscar
public class HexApp extends Application {
    private SceneManager sceneManager;
    @Override
    public void start(Stage stage) throws URISyntaxException {
        stage.setTitle("Hex Arena");
        sceneManager = new SceneManager(stage);
        sceneManager.switchScene(SceneType.MAIN_MENU);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
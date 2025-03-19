package com.hex;

import com.hex.scenes.HexGame;
import com.hex.scenes.MainMenu;
import javafx.application.Application;
import javafx.stage.Stage;

public class HexApp extends Application {
    private Stage primaryStage;
    public enum Scenes {
        MAINMENU,
        HEXGAME
    }
    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        stage.setTitle("Hex Game Board");
        stage.setScene(new MainMenu(this).getScene());
        stage.show();
    }

    public void switchToScene(Scenes scene) {
        switch (scene) {
            case MAINMENU:
                primaryStage.setScene(new MainMenu(this).getScene());
                break;
            case HEXGAME:
                primaryStage.setScene(new HexGame(this).getScene());
                break;

        }

    }

    public static void main(String[] args) {
        launch();
    }
}
package com.hex;

import com.hex.scenes.LocalGame;
import com.hex.scenes.OnlineGame;
import com.hex.scenes.MainMenu;
import javafx.application.Application;
import javafx.stage.Stage;

public class HexApp extends Application {
    private Stage primaryStage;
    public enum Scenes {
        MAINMENU,
        ONLINE,
        LOCAL
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
            case ONLINE:
                primaryStage.setScene(new OnlineGame(this).getScene());
                break;
            case LOCAL:
                primaryStage.setScene(new LocalGame(this).getScene());
                break;

        }

    }

    public static void main(String[] args) {
        launch();
    }
}
package com.hex;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class HexApp extends Application {
    private Stage primaryStage;
    enum Scenes {
        MAINMENU,
        HEXGAME
    }
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Scenes sceneSwitcher = Scenes.MAINMENU;

        stage.setTitle("Hex Game Board");
        stage.setScene(new MainMenu(this).getScene());
        stage.show();
    }8

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
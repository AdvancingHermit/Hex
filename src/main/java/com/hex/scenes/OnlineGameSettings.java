package com.hex.scenes;

import com.hex.SceneManager;
import com.hex.SceneManager.SceneType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.function.UnaryOperator;

public class OnlineGameSettings extends BaseScene {
    public OnlineGameSettings(SceneManager sceneManager, String currentServerIP) {
        // Create Visual Components
        StackPane root = new StackPane();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(50));

        // Label above TextField
        Label ipLabel = new Label("Server IP:");

        TextField inputField = getInputField(currentServerIP);
        Tooltip tooltip = new Tooltip("ex. 192.168.1.1 or localhost");
        Tooltip.install(inputField, tooltip);

        Button playBtn = new Button("Play now!");
        playBtn.disableProperty().bind(inputField.textProperty().isEmpty());
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> sceneManager.switchScene(SceneType.MAIN_MENU));

        // Scene switching on button press
        playBtn.setOnAction(e -> {
            sceneManager.serverIP = inputField.getText();
            sceneManager.switchScene(SceneType.LOADING_SCREEN);
        });
        vbox.getChildren().addAll(ipLabel, inputField, playBtn, backButton);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        root.getChildren().add(vbox);


        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());

    }

    private static TextField getInputField(String currentServerIP) {
        TextField inputField = new TextField();
        inputField.setText(currentServerIP);

        // Ensures only wanted text can be inputted::
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Allow only if newText is at most 30 chars and contains only allowed characters
            if (newText.matches("[A-Za-z0-9./]{0,30}")) {
                return change;
            }
            return null; // reject change otherwise
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        inputField.setTextFormatter(formatter);
        inputField.setMaxWidth(200);
        return inputField;
    }
}

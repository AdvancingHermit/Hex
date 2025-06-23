package com.hex.scenes;

import com.hex.GameMode;
import com.hex.SceneManager;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.dqn.DQNAlgorithm;
import com.hex.algorithms.minimax.ConnectionPlayer;
import com.hex.algorithms.montecarlo.MCTS;
import com.hex.algorithms.montecarlo.MCTSDouble;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.Random;
//Made by Oliver

public class SandboxGameSettings extends BaseScene {
    // Getters
    @Getter
    private int boardSize;

    // UI elements (optional to store if needed outside)
    private Slider boardSizeSlider;

    //scene for changing settings for local game
    public SandboxGameSettings(SceneManager sceneManager) {
        //backbutton
        Button backButton = new Button("← Back");
        backButton.setOnAction(e -> sceneManager.switchScene(SceneManager.SceneType.MAIN_MENU));
        backButton.setStyle("-fx-font-size: 14px;");

        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(10));

        StackPane root = new StackPane();
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        // Slider for board size
        Label boardSizeLabel = new Label("Board Size:");
        boardSizeSlider = new Slider(3, 11, 7);
        boardSizeSlider.setShowTickLabels(true);
        boardSizeSlider.setShowTickMarks(true);
        boardSizeSlider.setMajorTickUnit(1);
        boardSizeSlider.setMinorTickCount(0);
        boardSizeSlider.setBlockIncrement(1);
        boardSizeSlider.setSnapToTicks(true);


        // Start button
        Button playBtn = new Button("Start Game");
        playBtn.setOnAction(e -> {
            boardSize = (int) boardSizeSlider.getValue();
            // Start the game
            Scene scene1 = new SandboxGame(boardSize, sceneManager).getScene();
            sceneManager.switchScene(scene1);
        });

        vbox.getChildren().addAll(
                boardSizeLabel, boardSizeSlider,
                playBtn
        );
        vbox.setPickOnBounds(false);
        backButtonBox.setPickOnBounds(false);
        root.getChildren().addAll(vbox, backButtonBox);
        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());
    }

    private void updateSlider() {
        if (boardSizeSlider.getMax() == 7){
            boardSizeSlider.setMax(11);
            boardSizeSlider.setValue(5);

        }
    }
}

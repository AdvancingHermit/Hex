package com.hex.scenes;

import com.hex.GameMode;
import com.hex.SceneManager;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.minimax.ConnectionPlayer;
import com.hex.algorithms.minimax.MiniMax;
import com.hex.algorithms.montecarlo.MCTS;
import com.hex.algorithms.montecarlo.MCTSDouble;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.Random;

public class LocalGameSettings extends BaseScene {
    // Getters
    @Getter
    private int boardSize;
    @Getter
    private String difficulty;
    @Getter
    private String algorithm;
    @Getter
    private String startingPlayer;

    private boolean isSwapEnabled;
    private boolean isDoubleEnabled;

    // UI elements (optional to store if needed outside)
    private final Slider boardSizeSlider;
    private final ComboBox<String> difficultyBox;
    private final ComboBox<String> algorithmBox;
    private final ComboBox<String> startingPlayerBox;
    private final CheckBox swapCheckBox;
    private final CheckBox doubleCheckBox;

    public LocalGameSettings(SceneManager sceneManager) {
        //backbutton
        Button backButton = new Button("← Back");
        backButton.setOnAction(e -> sceneManager.switchScene(SceneManager.SceneType.MAIN_MENU));
        backButton.setStyle("-fx-font-size: 14px;");

        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(10));



        StackPane root = new StackPane();
        VBox vbox = new VBox(10);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        // Slider for board size
        Label boardSizeLabel = new Label("Board Size:");
        boardSizeSlider = new Slider(3, 11, 7);
        boardSizeSlider.setShowTickLabels(true);
        boardSizeSlider.setShowTickMarks(true);
        boardSizeSlider.setMajorTickUnit(1);
        boardSizeSlider.setMinorTickCount(0);
        boardSizeSlider.setBlockIncrement(1);
        boardSizeSlider.setSnapToTicks(true);

        // Algorithm selection
        Label algorithmLabel = new Label("Select Algorithm:");
        algorithmBox = new ComboBox<>();
        algorithmBox.getItems().addAll("None", "MCTS", "Connection", "AI");
        algorithmBox.setValue("None");

        // Difficulty selection
        Label difficultyLabel = new Label("Select Difficulty:");
        difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard", "Very Hard (might take some time)");
        difficultyBox.setValue("Medium");

        // Starting player selection
        Label startingPlayerLabel = new Label("Play as:");
        startingPlayerBox = new ComboBox<>();
        startingPlayerBox.getItems().addAll("Random", "Red (First)", "Blue (Second)");
        startingPlayerBox.setValue("Random");

        // Swap rule checkbox
        swapCheckBox = new CheckBox("Enable Swap Rule");
        swapCheckBox.setSelected(false);

        // Double rule checkbox
        doubleCheckBox = new CheckBox("Double moves");
        doubleCheckBox.setSelected(false);

        swapCheckBox.selectedProperty().addListener((obs, oldVal, isNowSelected) -> {
            if (isNowSelected) {
                doubleCheckBox.setSelected(false);
            }
        });

        doubleCheckBox.selectedProperty().addListener((obs, oldVal, isNowSelected) -> {
            if (isNowSelected) {
                swapCheckBox.setSelected(false);
            }
        });

        // Start button
        Button playBtn = new Button("Start Game");
        playBtn.setOnAction(e -> {
            boardSize = (int) boardSizeSlider.getValue();
            int iterations = 100;
            difficulty = difficultyBox.getValue();
            switch (difficulty) {
                case "Easy" -> iterations = 50_000;
                case "Medium" -> iterations = 250_000;
                case "Hard" -> iterations = 1_000_000;
                case "Very Hard (might take some time)" -> iterations = 5_000_000;
            }
            algorithm = algorithmBox.getValue();
            Algorithm algorithm1 = null;
            switch (algorithm) {
                case "MCTS" -> algorithm1 = new MCTS();
                case "Connection" -> algorithm1 = new ConnectionPlayer();
                case "AI" -> throw new RuntimeException("No AI implemented yet");
            }
            startingPlayer = startingPlayerBox.getValue();
            boolean algostart = false;
            Random random = new Random();
            switch (startingPlayer){
                case "Random" -> {
                    int i = random.nextInt(2);
                    algostart = i == 0;
                }
                case "Red (First)" -> algostart = false;
                case "Blue (Second)" -> algostart = true;

            }
            isSwapEnabled = swapCheckBox.isSelected();
            isDoubleEnabled = doubleCheckBox.isSelected();
            GameMode mode = GameMode.NORMAL;
            if (isSwapEnabled){
                mode = GameMode.SWAP;
            }
            if (isDoubleEnabled){
                mode = GameMode.DOUBLE;
                if (algorithm1 != null) {
                    algorithm1 = new MCTSDouble();
                }
            }
            // Start the game
            Scene scene1 = new LocalGame(boardSize, algorithm1, algostart, iterations, mode, sceneManager).getScene();
            sceneManager.switchScene(scene1);
        });

        vbox.getChildren().addAll(
                boardSizeLabel, boardSizeSlider,
                algorithmLabel, algorithmBox,
                difficultyLabel, difficultyBox,
                startingPlayerLabel, startingPlayerBox,
                swapCheckBox, doubleCheckBox,
                playBtn
        );
        vbox.setPickOnBounds(false);
        backButtonBox.setPickOnBounds(false);
        root.getChildren().addAll(vbox, backButtonBox);
        scene = new Scene(root, 800, 600);
    }

}

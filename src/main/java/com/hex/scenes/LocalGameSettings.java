package com.hex.scenes;

import com.hex.GameMode;
import com.hex.SceneManager;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.dqn.DQNAlgorithm;
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
    private Slider boardSizeSlider;
    private final ComboBox<String> difficultyBox;
    private final ComboBox<String> algorithmBox;
    private final ComboBox<String> startingPlayerBox;
    private final CheckBox swapCheckBox;
    private final CheckBox doubleCheckBox;
    private final Label difficultyLabel;

    //scene for changing settings for local game
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
        algorithmBox.getItems().addAll("None", "MCTS", "Connection", "DQN");
        algorithmBox.setValue("None");
        algorithmBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFieldsByAlgo());
        //algorithmBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFieldsByMCTSAlgo());


        // Difficulty selection
        difficultyLabel = new Label("Select Difficulty:");
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
        difficultyBox.setVisible(false);
        difficultyLabel.setVisible(false);

        swapCheckBox.selectedProperty().addListener((obs, oldVal, isNowSelected) -> {
            if (isNowSelected) {
                doubleCheckBox.setSelected(false);
            }
        });

        doubleCheckBox.selectedProperty().addListener((obs, oldVal, isNowSelected) -> {
            if (!algorithmBox.getValue().equals("MCTS")){
                updateSlider();
                if (isNowSelected){
                    swapCheckBox.setSelected(false);
                }
                return;
            }
            if (isNowSelected) {
                swapCheckBox.setSelected(false);
                difficultyBox.setValue("Hard");
                boardSizeSlider.setMax(7);
                boardSizeSlider.setValue(5);

            } else {
                difficultyBox.setDisable(false);
                boardSizeSlider.setMax(11);
                boardSizeSlider.setValue(7);
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
                case "MCTS"          -> algorithm1 = new MCTS();
                case "Connection"    -> algorithm1 = new ConnectionPlayer();
                case "DQN"           -> algorithm1 = new DQNAlgorithm();
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
        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());
    }

    private void updateFieldsByAlgo() {
        resetDefaultSettings();
        switch (algorithmBox.getValue()) {
            case "DQN" -> {
                boardSizeSlider.setDisable(true);
                boardSizeSlider.setValue(5);
                difficultyBox.setDisable(true);
                doubleCheckBox.setDisable(true);
                doubleCheckBox.setSelected(false);
                swapCheckBox.setDisable(true);
                doubleCheckBox.setDisable(true);
                difficultyBox.setVisible(false);
                difficultyLabel.setVisible(false);

            }
            case "MCTS" -> {
                difficultyLabel.setVisible(true);
                difficultyBox.setVisible(true);

            }
            case "Connection" ->{
                swapCheckBox.setDisable(false);
                doubleCheckBox.setDisable(true);
                doubleCheckBox.setSelected(false);
            }

            default -> {
                difficultyBox.setVisible(false);
                difficultyLabel.setVisible(false);
            }
        }

    }
    private void resetDefaultSettings() {
        difficultyBox.setVisible(true);
        difficultyLabel.setVisible(true);
        swapCheckBox.setDisable(false);
        boardSizeSlider.setDisable(false);
        difficultyBox.setDisable(false);
        doubleCheckBox.setDisable(false);
        doubleCheckBox.setSelected(false);
    }

    private void updateSlider() {
        if (boardSizeSlider.getMax() == 7){
            boardSizeSlider.setMax(11);
            boardSizeSlider.setValue(5);

        }
    }

}

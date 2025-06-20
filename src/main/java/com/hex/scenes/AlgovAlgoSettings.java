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
//Made by Oliver

public class AlgovAlgoSettings extends BaseScene{

    // Getters
    @Getter
    private int boardSize;
    @Getter
    private String startingDifficulty;
    @Getter
    private String secondDifficulty;
    @Getter
    private String startingAlgorithm;
    @Getter
    private String secondAlgorithm;

    private boolean isSwapEnabled;


    // UI elements (optional to store if needed outside)
    private final Slider boardSizeSlider;
    private final ComboBox<String> startingDifficultyBox;
    private final ComboBox<String> secondDifficultyBox;
    private final ComboBox<String> startingAlgorithmBox;
    private final ComboBox<String> secondAlgorithmBox;
    private final CheckBox swapCheckBox;
    private final Label diflabel1;
    private final Label diflabel2;


    public AlgovAlgoSettings(SceneManager sceneManager) {
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
        Label startingAlgorithmLabel = new Label("Select Algorithm That Starts:");
        startingAlgorithmBox = new ComboBox<>();
        startingAlgorithmBox.getItems().addAll( "MCTS", "Connection", "DQN");
        startingAlgorithmBox.setValue("MCTS");

        // Difficulty selection
        diflabel1 = new Label("Select Difficulty For Starting Algorithm:");
        startingDifficultyBox = new ComboBox<>();
        startingDifficultyBox.getItems().addAll("Easy", "Medium", "Hard", "Very Hard (might take some time)");
        startingDifficultyBox.setValue("Medium");

        // Second Algorithm selection
        Label secondAlgorithmLabel = new Label("Select Algorithm That Goes Second:");
        secondAlgorithmBox = new ComboBox<>();
        secondAlgorithmBox.getItems().addAll("MCTS", "Connection", "DQN");
        secondAlgorithmBox.setValue("Connection");

        // Difficulty selection
        diflabel2 = new Label("Select Difficulty:");
        secondDifficultyBox = new ComboBox<>();
        secondDifficultyBox.getItems().addAll("Easy", "Medium", "Hard", "Very Hard (might take some time)");
        secondDifficultyBox.setValue("Medium");


        // Swap rule checkbox
        swapCheckBox = new CheckBox("Enable Swap Rule");
        swapCheckBox.setSelected(false);

        // Double rule checkbox


        startingAlgorithmBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFieldsByAlgo());
        secondAlgorithmBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFieldsByAlgo2());



        // Start button
        Button playBtn = new Button("Start Game");
        playBtn.setOnAction(e -> {
            boardSize = (int) boardSizeSlider.getValue();
            int startingIterations = 100;
            startingDifficulty = startingDifficultyBox.getValue();
            switch (startingDifficulty) {
                case "Easy" -> startingIterations = 50_000;
                case "Medium" -> startingIterations = 250_000;
                case "Hard" -> startingIterations = 1_000_000;
                case "Very Hard (might take some time)" -> startingIterations = 5_000_000;
            }
            int secondIterations = 100;
            secondDifficulty = secondDifficultyBox.getValue();
            switch (secondDifficulty) {
                case "Easy" -> secondIterations = 50_000;
                case "Medium" -> secondIterations = 250_000;
                case "Hard" -> secondIterations = 1_000_000;
                case "Very Hard (might take some time)" -> secondIterations = 5_000_000;
            }
            startingAlgorithm = startingAlgorithmBox.getValue();
            Algorithm algorithm1 = null;
            switch (startingAlgorithm) {
                case "MCTS" -> algorithm1 = new MCTS();
                case "Connection" -> algorithm1 = new ConnectionPlayer();
                case "DQN" -> algorithm1 = new DQNAlgorithm();
            }
            secondAlgorithm = secondAlgorithmBox.getValue();
            Algorithm algorithm2 = null;
            switch (secondAlgorithm) {
                case "MCTS" -> algorithm2 = new MCTS();
                case "Connection" -> algorithm2 = new ConnectionPlayer();
                case "DQN" -> algorithm2 = new DQNAlgorithm();
            }
            isSwapEnabled = swapCheckBox.isSelected();
            GameMode mode = GameMode.NORMAL;
            if (isSwapEnabled){
                mode = GameMode.SWAP;
            }

            // Start the game

            Scene scene1 = new AlgovAlgo(boardSize, algorithm1, algorithm2, startingIterations, secondIterations, mode, sceneManager, startingAlgorithm, secondAlgorithm).getScene();
            sceneManager.switchScene(scene1);
        });

        vbox.getChildren().addAll(
                boardSizeLabel, boardSizeSlider,
                startingAlgorithmLabel, startingAlgorithmBox,
                diflabel1, startingDifficultyBox,
                secondAlgorithmLabel, secondAlgorithmBox,
                diflabel2, secondDifficultyBox,
                swapCheckBox,
                playBtn
        );
        vbox.setPickOnBounds(false);
        backButtonBox.setPickOnBounds(false);
        root.getChildren().addAll(vbox, backButtonBox);
        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());
    }
    private void updateFieldsByAlgo() {
        resetDefaultSettings();
        switch (startingAlgorithmBox.getValue()) {
            case "DQN" -> {
                boardSizeSlider.setDisable(true);
                boardSizeSlider.setValue(5);
                startingDifficultyBox.setDisable(true);
                startingDifficultyBox.setVisible(false);
                diflabel1.setVisible(false);

            }
            case "MCTS" -> {
                diflabel1.setVisible(true);
                startingDifficultyBox.setVisible(true);

            }
            case "Connection" ->{
            }

            default -> {
                startingDifficultyBox.setVisible(false);
                diflabel1.setVisible(false);
            }
        }

    }
    private void resetDefaultSettings() {
        startingDifficultyBox.setVisible(true);
        diflabel1.setVisible(true);
        if (!secondAlgorithmBox.getValue().equals("DQN")){
            boardSizeSlider.setDisable(false);
        }
        startingDifficultyBox.setDisable(false);
    }
    private void updateFieldsByAlgo2() {
        resetDefaultSettings2();
        switch (secondAlgorithmBox.getValue()) {
            case "DQN" -> {
                boardSizeSlider.setDisable(true);
                boardSizeSlider.setValue(5);
                secondDifficultyBox.setDisable(true);
                secondDifficultyBox.setVisible(false);
                diflabel2.setVisible(false);

            }
            case "MCTS" -> {
                diflabel2.setVisible(true);
                secondDifficultyBox.setVisible(true);

            }
            case "Connection" ->{
            }

            default -> {
                secondDifficultyBox.setVisible(false);
                diflabel2.setVisible(false);
            }
        }

    }
    private void resetDefaultSettings2() {
        secondDifficultyBox.setVisible(true);
        diflabel2.setVisible(true);
        if (!startingAlgorithmBox.getValue().equals("DQN")){
            boardSizeSlider.setDisable(false);
        }
        secondDifficultyBox.setDisable(false);
    }


}

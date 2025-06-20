package com.hex.scenes;

import com.hex.GameMode;
import com.hex.SceneManager;
import com.hex.algorithms.Algorithm;
import com.hex.algorithms.RandomAlgorithm;
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
// Christian
public class AlgovAlgoNoUISettings extends BaseScene{

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
    private boolean isDoubleEnabled;

    // UI elements (optional to store if needed outside)
    private final Slider boardSizeSlider;
    private final ComboBox<String> startingDifficultyBox;
    private final ComboBox<String> secondDifficultyBox;
    private final ComboBox<String> startingAlgorithmBox;
    private final ComboBox<String> secondAlgorithmBox;
    private final CheckBox swapCheckBox;
    private final CheckBox doubleCheckBox;

    public AlgovAlgoNoUISettings(SceneManager sceneManager) {
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

        // Algorithm selection
        Label startingAlgorithmLabel = new Label("Select Algorithm That Starts:");
        startingAlgorithmBox = new ComboBox<>();
        startingAlgorithmBox.getItems().addAll( "MCTS", "Connection","DQN", "Random");
        startingAlgorithmBox.setValue("MCTS");

        // Difficulty selection
        Label startingDifficultyLabel = new Label("Select Difficulty For Starting Algorithm:");
        startingDifficultyBox = new ComboBox<>();
        startingDifficultyBox.getItems().addAll("Easy", "Medium", "Hard", "Very Hard (might take some time)");
        startingDifficultyBox.setValue("Medium");

        // Second Algorithm selection
        Label secondAlgorithmLabel = new Label("Select Algorithm That Goes Second:");
        secondAlgorithmBox = new ComboBox<>();
        secondAlgorithmBox.getItems().addAll("MCTS", "Connection", "DQN", "Random");
        secondAlgorithmBox.setValue("Connection");

        // Difficulty selection
        Label secondDifficultyLabel = new Label("Select Difficulty:");
        secondDifficultyBox = new ComboBox<>();
        secondDifficultyBox.getItems().addAll("Easy", "Medium", "Hard", "Very Hard (might take some time)");
        secondDifficultyBox.setValue("Medium");


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
                case "Random" -> algorithm1 = new RandomAlgorithm();
            }
            secondAlgorithm = secondAlgorithmBox.getValue();
            Algorithm algorithm2 = null;
            switch (secondAlgorithm) {
                case "MCTS" -> algorithm2 = new MCTS();
                case "Connection" -> algorithm2 = new ConnectionPlayer();
                case "DQN" -> algorithm2 = new DQNAlgorithm();
                case "Random" -> algorithm2 = new RandomAlgorithm();
            }
            isSwapEnabled = swapCheckBox.isSelected();
            GameMode mode = GameMode.NORMAL;
            if (isSwapEnabled){
                mode = GameMode.SWAP;
            }
            // Start the game

            Scene scene1 = new AlgovAlgoNoUI(boardSize, algorithm1, algorithm2, startingIterations, secondIterations, mode, sceneManager).getScene();
            sceneManager.switchScene(scene1);
        });

        vbox.getChildren().addAll(
                boardSizeLabel, boardSizeSlider,
                startingAlgorithmLabel, startingAlgorithmBox,
                startingDifficultyLabel, startingDifficultyBox,
                secondAlgorithmLabel, secondAlgorithmBox,
                secondDifficultyLabel, secondDifficultyBox,
                swapCheckBox, doubleCheckBox,
                playBtn
        );

        vbox.setPickOnBounds(false);
        backButtonBox.setPickOnBounds(false);
        root.getChildren().addAll(vbox, backButtonBox);
        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());
    }
}

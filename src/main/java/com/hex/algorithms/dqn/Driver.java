// File: Driver.java
package com.hex.algorithms.dqn;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.NoopTranslator;
import com.hex.GameState;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {
    private static final String MODEL_NAME = "hex-dqn";
    private static final String WEIGHTS_PATH = "F:\\Documents\\GitHub\\Hex-Deep\\Hex-Deep\\Hex\\DQNDir\\djl_weights.txt";

    public static void main(String[] args) throws Exception {
        // Force DJL to use MXNet (must happen before Model.newInstance)
        System.setProperty("ai.djl.default_engine", "MXNet");
        System.out.println("Using engine: " + Engine.getInstance().getEngineName());

        // --- Original Driver logic (can be kept for testing model loading) ---
        // Instantiate your block
        HexDqnBlock block = new HexDqnBlock();

        Path weightsPath = Paths.get(WEIGHTS_PATH);

        // Use a single NDManager for initialization, loading weights, and inference
        try (NDManager manager = NDManager.newBaseManager()) {
            // Initialize the block under this manager
            // Input shapes: board (batch, 2, 5, 5), swap (batch, 1)
            block.initialize(manager, DataType.FLOAT32, new Shape[]{new Shape(1, 2, 5, 5), new Shape(1, 1)});
            System.out.println("HexDqnBlock initialized successfully.");

            // Load weights under same manager
            block.loadWeights(manager, weightsPath);
            System.out.println("Weights loaded successfully.");

            // Prepare dummy inputs under same manager
            NDArray boardNHWC = manager.zeros(new Shape(1, 5, 5, 2));
            NDArray boardNCHW = boardNHWC.transpose(0, 3, 1, 2);
            NDArray swap = manager.zeros(new Shape(1, 1));
            NDList input = new NDList(boardNCHW, swap);

            // Create the model and attach block
            try (Model model = Model.newInstance(MODEL_NAME, Device.cpu())) {
                model.setBlock(block);
                // Run inference
                try (Predictor<NDList, NDList> predictor = model.newPredictor(new NoopTranslator())) {
                    System.out.println("Running inference with test input...");
                    NDList output = predictor.predict(input);
                    NDArray qValues = output.singletonOrThrow();

                    System.out.println("Q-Values shape: " + qValues.getShape());
                    System.out.println("Q-Values (dummy):");
                    float[] data = qValues.toFloatArray();
                    int size = (int) qValues.getShape().size();
                    for (int i = 0; i < size; i++) {
                        System.out.printf("%6.3f%c", data[i], (i + 1) % 5 == 0 ? '\n' : ' ');
                    }
                }
            }
        }
        // --- End of original Driver logic ---


        System.out.println("\n--- Testing DQNAlgorithm ---");
        DQNAlgorithm dqnAlgorithm = new DQNAlgorithm();
        Board testBoard = new Board(5, 5); // Example 5x5 board
        // Set some pieces on the board for a more realistic scenario
        testBoard.setPiece(0, 0, 1);
        testBoard.setPiece(0, 1, 2);
        testBoard.setPiece(1, 1, 1);
        testBoard.setPiece(2, 2, 2);

        GameState testGameState = new GameState(); // Default GameState

        System.out.println("Current board state:");
        for (int r = 0; r < testBoard.getRows(); r++) {
            for (int c = 0; c < testBoard.getCols(); c++) {
                System.out.print(testBoard.getPiece(r, c) + " ");
            }
            System.out.println();
        }

        BoardCoordinate chosenMove = dqnAlgorithm.makeMove(testGameState.getCurrentPlayer(), testBoard, testGameState, 1, false);

        if (chosenMove != null) {
            System.out.println("\nDQNAlgorithm recommends move: (" + chosenMove.x + ", " + chosenMove.y + ")");
        } else {
            System.out.println("\nDQNAlgorithm failed to recommend a move.");
        }
    }
}
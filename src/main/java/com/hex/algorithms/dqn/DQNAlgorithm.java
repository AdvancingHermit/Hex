// File: DQNAlgorithm.java
package com.hex.algorithms.dqn;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.NoopTranslator;
import com.hex.GameState;
import com.hex.algorithms.Algorithm;
import com.hex.components.Board;
import com.hex.components.BoardCoordinate;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DQNAlgorithm implements Algorithm {

    private static final String MODEL_NAME = "hex-dqn";
    private static final String WEIGHTS_PATH = "F:\\Documents\\GitHub\\Hex-Deep\\Hex-Deep\\Hex\\DQNDir\\djl_weights.txt";

    @Override
    public BoardCoordinate makeMove(int player, Board board, GameState gameState, int iterations, boolean swap) {

        try (NDManager manager = NDManager.newBaseManager()) {
            HexDqnBlock block = new HexDqnBlock();
            Path weightsPath = loadWeightsFromResource("/com/hex/models/djl_weights.txt");


            block.initialize(manager, DataType.FLOAT32, new Shape[]{new Shape(1, 2, board.getRows(), board.getCols()), new Shape(1, 1)});
            block.loadWeights(manager, weightsPath);

            // Prepare inputs for the DQN model
            NDArray boardInput = convertBoardToNDArray(manager, board, player);
            NDArray swapInput = manager.create(new float[]{swap ? 1.0f : 0.0f}, new Shape(1, 1));
            NDList input = new NDList(boardInput, swapInput);

            try (Model model = Model.newInstance(MODEL_NAME, Device.cpu())) {
                model.setBlock(block);
                try (Predictor<NDList, NDList> predictor = model.newPredictor(new NoopTranslator())) {
                    NDList output = predictor.predict(input);
                    NDArray qValues = output.singletonOrThrow();

                    // Process Q-values to find the best move
                    return selectBestMove(qValues, board);

                }
            }
        } catch (Exception e) {
            System.err.println("Error during DQN move generation: " + e.getMessage());
            e.printStackTrace();
            // Fallback: if DQN fails, perhaps a random move or an error
            return null;
        }
    }

     //Converts the game board into a 2-channel NDArray with the shape (1, 2, rows, cols).
    private NDArray convertBoardToNDArray(NDManager manager, Board board, int currentPlayer) {
        int rows = board.getRows();
        int cols = board.getCols();
        // The final shape is NCHW: Batch, Channels, Height, Width
        NDArray boardArray = manager.zeros(new Shape(1, 2, rows, cols), DataType.FLOAT32);

        int p1 = 1;
        int p2 = 2;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int piece = board.getPiece(i, j);

                if (currentPlayer == p1) {
                    // Player 1's perspective (no transposition)
                    if (piece == p1) {
                        boardArray.set(new NDIndex(0, 0, i, j), 1.0f); // P1's pieces in channel 0
                    } else if (piece == p2) {
                        boardArray.set(new NDIndex(0, 1, i, j), 1.0f); // P2's pieces in channel 1
                    }
                } else { // currentPlayer == p2
                    // Player 2's perspective (with transposition)
                    // The board is viewed as if P2 is connecting top-to-bottom,
                    // which requires transposing the coordinates (i, j) -> (j, i).
                    if (piece == p2) {
                        boardArray.set(new NDIndex(0, 0, j, i), 1.0f); // P2's pieces in channel 0 (transposed)
                    } else if (piece == p1) {
                        boardArray.set(new NDIndex(0, 1, j, i), 1.0f); // P1's pieces in channel 1 (transposed)
                    }
                }
            }
        }
        return boardArray;
    }


    private BoardCoordinate selectBestMove(NDArray qValues, Board board) {
        float[] data = qValues.toFloatArray();
        int bestX = -1;
        int bestY = -1;
        float maxQValue = Float.NEGATIVE_INFINITY;

        int rows = board.getRows();
        int cols = board.getCols();

        // Iterate through the flattened Q-values
        for (int i = 0; i < data.length; i++) {
            int r = i / cols; // Row from flattened index
            int c = i % cols; // Column from flattened index

            // Only consider empty cells
            if (board.getPiece(r, c) == 0) {
                if (data[i] > maxQValue) {
                    maxQValue = data[i];
                    bestX = r;
                    bestY = c;
                }
            }
        }

        if (bestX == -1 || bestY == -1) {
            // This should ideally not happen if there are valid moves.
            // As a fallback, return the first available move.
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (board.getPiece(i, j) == 0) {
                        return new BoardCoordinate(i, j);
                    }
                }
            }
        }
        return new BoardCoordinate(bestX, bestY);
    }

    private Path loadWeightsFromResource(String resourcePath) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            Path tempFile = Files.createTempFile("djl_weights", ".txt");
            tempFile.toFile().deleteOnExit();
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        }
    }
}
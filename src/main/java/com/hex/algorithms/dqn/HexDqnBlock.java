
// File: HexDqnBlock.java
package com.hex.algorithms.dqn;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.AbstractBlock;
import ai.djl.nn.Activation;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import ai.djl.training.ParameterStore;
import ai.djl.util.PairList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HexDqnBlock extends AbstractBlock {

    private final Conv2d localPatterns;
    private final Conv2d globalPatterns;
    private final Conv2d patternCombinations;
    private final Linear decisionLayer1;
    private final Linear decisionLayer2;
    private final Linear qValuesLayer;

    public HexDqnBlock() {
        super();
        localPatterns = addChildBlock("localPatterns", Conv2d.builder()
                .setKernelShape(new Shape(3, 3)).setFilters(128).optPadding(new Shape(1, 1)).build());
        globalPatterns = addChildBlock("globalPatterns", Conv2d.builder()
                .setKernelShape(new Shape(3, 3)).setFilters(128).optPadding(new Shape(1, 1)).build());
        patternCombinations = addChildBlock("patternCombinations", Conv2d.builder()
                .setKernelShape(new Shape(3, 3)).setFilters(128).optPadding(new Shape(1, 1)).build());

        decisionLayer1 = addChildBlock("decisionLayer1", Linear.builder().setUnits(256).build());
        decisionLayer2 = addChildBlock("decisionLayer2", Linear.builder().setUnits(128).build());
        qValuesLayer = addChildBlock("qValuesLayer", Linear.builder().setUnits(25).build());
    }

    @Override
    protected NDList forwardInternal(
            ParameterStore parameterStore,
            NDList inputs,
            boolean training,
            PairList<String, Object> params
    ) {
        NDArray board = inputs.get(0);
        NDArray swap = inputs.get(1);

        NDArray x = Activation.relu(
                localPatterns.forward(parameterStore, new NDList(board), training, params)
                        .singletonOrThrow());
        x = Activation.relu(
                globalPatterns.forward(parameterStore, new NDList(x), training, params)
                        .singletonOrThrow());
        x = Activation.relu(
                patternCombinations.forward(parameterStore, new NDList(x), training, params)
                        .singletonOrThrow());

        // Flatten manually since MxNDArray.flatten may not be implemented
        long batch = x.getShape().get(0);
        long rest = x.getShape().size() / batch;
        NDArray xFlat = x.reshape(batch, rest);

        NDArray concatenated = NDArrays.concat(new NDList(xFlat, swap), 1);

        NDArray d = Activation.relu(
                decisionLayer1.forward(parameterStore, new NDList(concatenated), training, params)
                        .singletonOrThrow());
        d = Activation.relu(
                decisionLayer2.forward(parameterStore, new NDList(d), training, params)
                        .singletonOrThrow());

        NDArray qOut = qValuesLayer.forward(parameterStore, new NDList(d), training, params)
                .singletonOrThrow();
        return new NDList(qOut);
    }

    @Override
    public Shape[] getOutputShapes(Shape[] inputShapes) {
        return new Shape[]{ new Shape(inputShapes[0].get(0), 25) };
    }

    @Override
    protected void initializeChildBlocks(NDManager manager, DataType dataType, Shape... inputShapes) {
        Shape boardInputShape = inputShapes[0];
        Shape swapInputShape = inputShapes[1];

        localPatterns.initialize(manager, dataType, boardInputShape);
        Shape convOutputShape = new Shape(boardInputShape.get(0), 128, boardInputShape.get(2), boardInputShape.get(3));

        globalPatterns.initialize(manager, dataType, convOutputShape);
        patternCombinations.initialize(manager, dataType, convOutputShape);

        long flattenSize = convOutputShape.get(1) * convOutputShape.get(2) * convOutputShape.get(3);
        Shape concatenatedInputShape = new Shape(boardInputShape.get(0), flattenSize + swapInputShape.get(1));

        decisionLayer1.initialize(manager, dataType, concatenatedInputShape);
        Shape decisionLayer1OutputShape = new Shape(boardInputShape.get(0), 256);

        decisionLayer2.initialize(manager, dataType, decisionLayer1OutputShape);
        Shape decisionLayer2OutputShape = new Shape(boardInputShape.get(0), 128);

        qValuesLayer.initialize(manager, dataType, decisionLayer2OutputShape);
    }

    public void loadWeights(NDManager manager, Path weightsFile) throws IOException {
        System.out.println("Loading weights from text file: " + weightsFile);
        Map<String, NDArray> weightsMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(weightsFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 3);
                if (parts.length != 3) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }
                String key = parts[0];
                long[] shapeArray = Arrays.stream(parts[1].split(","))
                        .mapToLong(Long::parseLong)
                        .toArray();
                Shape shape = new Shape(shapeArray);

                String[] stringData = parts[2].split(",");
                float[] data = new float[stringData.length];
                for (int i = 0; i < stringData.length; i++) {
                    data[i] = Float.parseFloat(stringData[i]);
                }

                NDArray nd = manager.create(data).reshape(shape);
                weightsMap.put(key, nd);
            }
        }

        // Map block names in camelCase
        Map<String, AbstractBlock> childBlocks = new HashMap<>();
        childBlocks.put("localPatterns", localPatterns);
        childBlocks.put("globalPatterns", globalPatterns);
        childBlocks.put("patternCombinations", patternCombinations);
        childBlocks.put("decisionLayer1", decisionLayer1);
        childBlocks.put("decisionLayer2", decisionLayer2);
        childBlocks.put("qValuesLayer", qValuesLayer);

        for (Map.Entry<String, NDArray> entry : weightsMap.entrySet()) {
            String fullKey = entry.getKey();
            NDArray originalNdArray = entry.getValue();

            String blockNameRaw;
            String paramType;
            if (fullKey.endsWith("_weight")) {
                blockNameRaw = fullKey.substring(0, fullKey.length() - "_weight".length());
                paramType = "weight";
            } else if (fullKey.endsWith("_bias")) {
                blockNameRaw = fullKey.substring(0, fullKey.length() - "_bias".length());
                paramType = "bias";
            } else {
                System.err.println("Skipping unrecognized weight key: " + fullKey);
                continue;
            }
            // Normalize snake_case to camelCase if needed
            String lookupName = blockNameRaw;
            if (blockNameRaw.contains("_")) {
                String[] parts = blockNameRaw.split("_");
                StringBuilder sb = new StringBuilder(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
                }
                lookupName = sb.toString();
            }
            AbstractBlock targetBlock = childBlocks.get(lookupName);
            if (targetBlock == null) {
                System.err.println("Error: Target block not found for key: " + blockNameRaw);
                continue;
            }

            NDArray tempTransformed = null;
            try {
                int ndim = originalNdArray.getShape().dimension();
                if (paramType.equals("weight") && ndim == 4 && (lookupName.equals("localPatterns") || lookupName.equals("globalPatterns") || lookupName.equals("patternCombinations"))) {
                    // Assume saved as [H, W, inC, outC], transform to [outC, inC, H, W]
                    tempTransformed = originalNdArray.transpose(3, 2, 0, 1);
                } else if (paramType.equals("weight") && ndim == 2 && (lookupName.equals("decisionLayer1") || lookupName.equals("decisionLayer2") || lookupName.equals("qValuesLayer"))) {
                    // For linear layers: assume saved [out, in], transform to [in, out]
                    tempTransformed = originalNdArray.transpose();
                } else if (paramType.equals("bias") && ndim == 1) {
                    // Bias vector: no transpose needed
                    tempTransformed = originalNdArray;
                } else {
                    System.err.printf("Warning: Unexpected shape for %s %s: dimensions=%d. Using data without transpose.\n", lookupName, paramType, ndim);
                    tempTransformed = originalNdArray;
                }
                tempTransformed.copyTo(targetBlock.getParameters().get(paramType).getArray());
            } finally {
                if (tempTransformed != null && tempTransformed != originalNdArray) {
                    tempTransformed.close();
                }
            }
            // originalNdArray closed when manager closes
        }
        System.out.println("Weights loaded and assigned successfully.");
    }

    private static final long serialVersionUID = 1L;
}
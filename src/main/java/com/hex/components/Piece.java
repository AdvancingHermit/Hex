package com.hex.components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;
import javafx.scene.Group;

import static java.lang.Math.*;
// Made by Oscar
public class Piece extends Group {
    private Polygon hexagon;
    private double[] gridPosition;
    private double[][] vertices;
    private Line[] edges;

    //These pieces are used to draw the board, and they can be colored to represent a player move

    public Piece(double[] gridPosition, double size) {
        this.gridPosition = gridPosition;
        this.hexagon = new Polygon();

        // Calculate center position
        double horSpacing = sqrt(3) * size;
        double verSpacing = 3.0 / 2.0 * size;
        double x = gridPosition[0] * horSpacing + gridPosition[1] * (horSpacing / 2) + horSpacing / 2;
        double y = gridPosition[1] * verSpacing + size;
        double[] center = new double[] {x, y};

        // Pre-calculate values for vertices
        double angle = toRadians(30);
        double second = sin(angle) * size;
        double realSize = size * sqrt(3) / 2.0;

        // Store vertices for the hexagon (and for edge drawing)
        vertices = new double[][] {
                {center[0], center[1] - size},               // top (0)
                {center[0] + realSize, center[1] - second},    // top right (1)
                {center[0] + realSize, center[1] + second},    // bottom right (2)
                {center[0], center[1] + size},                 // bottom (3)
                {center[0] - realSize, center[1] + second},    // bottom left (4)
                {center[0] - realSize, center[1] - second}     // top left (5)
        };

        // Create the hexagon polygon
        Double[] points = new Double[12];
        for (int i = 0; i < 6; i++) {
            points[i * 2] = vertices[i][0];
            points[i * 2 + 1] = vertices[i][1];
        }
        hexagon.getPoints().addAll(points);
        getChildren().add(hexagon);
    }

    public void setFill(Color color) {
        hexagon.setFill(color);
    }

    public void setStroke(Color color) {
        hexagon.setStroke(color);
    }

    public double[] getGridPosition() {
        return gridPosition;
    }

    // Lazy initialization of an edge at the given index
    private void initEdge(int i, Color color) {
        if (edges == null) {
            this.edges = new Line[6];
        }
        if (edges[i] == null) {
            int nextIndex = (i + 1) % 6;
            edges[i] = new Line(
                    vertices[i][0], vertices[i][1],
                    vertices[nextIndex][0], vertices[nextIndex][1]
            );
            edges[i].setStrokeWidth(2);
            getChildren().add(edges[i]);
        }
        edges[i].setStroke(color);
        edges[i].setVisible(true);
    }

    public void setTopRightEdge(Color color) {
        initEdge(0, color);
    }

    public void setRightEdge(Color color) {
        initEdge(1, color);
    }

    public void setBottomRightEdge(Color color) {
        initEdge(2, color);
    }

    public void setBottomLeftEdge(Color color) {
        initEdge(3, color);
    }

    public void setLeftEdge(Color color) {
        initEdge(4, color);
    }

    public void setTopLeftEdge(Color color) {
        initEdge(5, color);
    }
}

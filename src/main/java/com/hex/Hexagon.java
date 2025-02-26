package com.hex;

import javafx.scene.shape.Polygon;

import static java.lang.Math.*;

public class Hexagon extends Polygon {
    public Hexagon(double[] center, double size) {
        super();
        getPoints().clear();
        double angle = toRadians(30);
        double second = sin(angle) * size;
        double realSize = size*sqrt(3)/2.0;
        getPoints().addAll(new Double[]{
                center[0], center[1] -size, // highest
                center[0] + realSize, -second + center[1], // 2nd highest right
                center[0] + realSize, second + center[1], // 3rd highest right
                center[0], center[1] + size, // lowest
                center[0] - realSize, second + center[1], // 3rd highest left
                center[0] - realSize, -second + center[1]}); // 2nd highest left
    }
}

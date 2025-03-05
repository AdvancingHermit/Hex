package com.hex.components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.awt.*;

import static java.lang.Math.*;

public class Piece extends Polygon {
    double[] gridPosition;
    public Piece(double[] gridPosition, double size) {
        super();
        getPoints().clear();
        this.gridPosition = gridPosition;

        //calc position
        double horSpacing = sqrt(3)*size;
        double verSpacing = 3.0/2.0 * size;
        double x = gridPosition[0] * horSpacing + gridPosition[1] * (horSpacing / 2 ) + horSpacing/2;
        double y = gridPosition[1] * verSpacing + size;
        double[] center = new double[] {x, y};
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

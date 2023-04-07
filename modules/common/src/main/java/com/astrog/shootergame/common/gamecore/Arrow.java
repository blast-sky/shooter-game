package com.astrog.shootergame.common.gamecore;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import lombok.Getter;

public class Arrow extends GameObject {

    public final String owner;
    @Getter
    private final double width = 80;
    @Getter
    private final double height = 25;

    public Arrow(Point2D initialPosition, Point2D initialVelocity, String owner) {
        super(initialPosition, initialVelocity);
        this.owner = owner;
    }

    @Override
    public Bounds getBounds() {
        return new BoundingBox(
            position.getX(),
            position.getY(),
            width,
            height
        );
    }
}

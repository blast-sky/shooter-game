package com.astrog.shootergame.common.gamecore;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import lombok.Getter;

public class Target extends GameObject {

    public final long scorePerShoot;
    @Getter
    private final double radius;

    public Target(Point2D initialPosition, Point2D initialVelocity, double radius, long scorePerShoot) {
        super(initialPosition, initialVelocity);
        this.radius = radius;
        this.scorePerShoot = scorePerShoot;
    }

    public void inverseVelocity() {
        velocity = velocity.multiply(-1);
    }

    @Override
    public Bounds getBounds() {
        return new BoundingBox(
            position.getX() - radius,
            position.getY() - radius,
            radius * 2,
            radius * 2
        );
    }
}

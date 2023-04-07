package com.astrog.shootergame.common.gamecore;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import lombok.Getter;

import java.io.Serializable;

public abstract class GameObject implements Serializable {

    @Getter
    protected Point2D position;
    protected Point2D velocity;

    public GameObject(Point2D position, Point2D velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public abstract Bounds getBounds();

    public void update() {
        position = position.add(velocity);
    }
}

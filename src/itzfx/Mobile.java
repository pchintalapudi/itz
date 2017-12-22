/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public abstract class Mobile {

    private final DoubleProperty centerX;
    private final DoubleProperty centerY;

    private final double layoutX;
    private final double layoutY;
    private final double initRotate;

    private boolean reset;

    protected Mobile(double layoutX, double layoutY) {
        this(layoutX, layoutY, 0);
    }

    protected Mobile(double layoutX, double layoutY, double initRotate) {
        centerX = new SimpleDoubleProperty();
        centerY = new SimpleDoubleProperty();
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.initRotate = initRotate;
        centerX.addListener((ObservableValue<? extends Number> obs, Number old, Number next) -> reset = false);
        centerY.addListener((ObservableValue<? extends Number> obs, Number old, Number next) -> reset = false);
    }

    protected void registerProperties() {
        getNode().translateXProperty().bind(translateXBind());
        getNode().translateYProperty().bind(translateYBind());
        getNode().setUserData(this);
        reset();
    }

    protected DoubleBinding translateXBind() {
        return Bindings.createDoubleBinding(() -> {
            Node n = getNode();
            return centerX.get() - (n.getBoundsInLocal().getWidth() / 2 + n.getBoundsInLocal().getMinX());
        }, centerX);
    }

    protected DoubleBinding translateYBind() {
        return Bindings.createDoubleBinding(() -> {
            Node n = getNode();
            return centerY.get() - (n.getBoundsInLocal().getHeight() / 2 + n.getBoundsInLocal().getMinY());
        }, centerY);
    }

    public final void shiftCenter(double x, double y) {
        centerX.set(centerX.get() + x);
        centerY.set(centerY.get() + y);
    }

    public final void setCenter(double x, double y) {
        centerX.set(x);
        centerY.set(y);
    }

    public final void setX(double x) {
        centerX.set(x);
    }

    public final void setY(double y) {
        centerY.set(y);
    }

    public final void shiftRight(double x) {
        centerX.set(centerX.get() + x);
    }

    public final void shiftDown(double y) {
        centerY.set(centerY.get() + y);
    }

    public final double getCenterX() {
        return centerX.get();
    }

    public final double getCenterY() {
        return centerY.get();
    }

    public final DoubleProperty centerXProperty() {
        return this.centerX;
    }

    public final DoubleProperty centerYProperty() {
        return this.centerY;
    }

    public final void reset() {
        resetProperties();
        centerX.set(layoutX);
        centerY.set(layoutY);
        getNode().setRotate(initRotate);
        reappear();
        reset = true;
    }

    public final boolean isReset() {
        return reset;
    }

    protected void resetProperties() {
    }

    public final void vanish() {
        Platform.runLater(() -> {
            disableCollision();
            getNode().setVisible(false);
            vanished.set(true);
        });
    }

    public final void reappear() {
        Platform.runLater(() -> {
            enableCollision();
            getNode().setVisible(true);
            vanished.set(false);
        });
    }

    private final BooleanProperty vanished = new SimpleBooleanProperty();

    public boolean isVanished() {
        return vanished.get();
    }

    public BooleanProperty vanishedProperty() {
        return vanished;
    }

    public abstract Node getNode();

    public abstract void enableCollision();

    public abstract void disableCollision();

    public abstract void permaDisableCollisions();

    public abstract boolean canCollide();

    protected static ChangeListener<Number> limitToField(double radius, DoubleProperty limit) {
        return (ObservableValue<? extends Number> obs, Number old, Number next) -> {
            if (next.doubleValue() < radius && next.doubleValue() > -50) {
                limit.set(radius + Math.random());
            } else if (next.doubleValue() + radius > 720) {
                limit.set(720 - radius - Math.random());
            }
        };
    }

    protected final ChangeListener<Number> exclude20(double radius) {
        return (ObservableValue<? extends Number> obs, Number old, Number next) -> {
            if (getCenterX() - radius < 120 && getCenterY() - getCenterX() > 600 - radius && getCenterY() - getCenterX() < 615 - radius) {
                double dist = lineDistance(getCenterX(), getCenterY(), 1, 600 - radius);
                shiftRight(dist / Math.sqrt(2));
                shiftDown(-dist / Math.sqrt(2));
            }
            if (getCenterY() - radius < 120 && getCenterX() - getCenterY() > 600 - radius && getCenterX() - getCenterY() < 615 - radius) {
                double dist = lineDistance(getCenterX(), getCenterY(), 1, -600 + radius);
                shiftRight(-dist / Math.sqrt(2));
                shiftDown(dist / Math.sqrt(2));
            }
        };
    }

    private double lineDistance(double x, double y, double m, double k) {
        return Math.abs(k + m * x - y) / Math.sqrt(1 + m * m);
    }
}

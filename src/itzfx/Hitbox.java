/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public final class Hitbox {

    public static final BooleanProperty VISIBLE;

    private static final List<Hitbox> COLLIDABLE;

    static {
        VISIBLE = new SimpleBooleanProperty();
        COLLIDABLE = new LinkedList<>();
    }

    public static void pulse() {
        for (int i = COLLIDABLE.size() - 1; i > -1; i--) {
            Hitbox hb = COLLIDABLE.get(i);
            if (hb.cType != CollisionType.PHANTOM && hb.canCollide()) {
                COLLIDABLE.subList(0, i).stream()
                        .filter(h -> h.collisionEnabled.get())
                        .filter(h -> h.check != hb.check)
                        .filter(h -> h.movableOwner != null)
                        .filter(h -> inRange(hb, h))
                        .forEach(h -> resolveCollision(hb, h));
            }
        }
    }

    private static void resolveCollision(Hitbox hb1, Hitbox hb2) {
        if (hb2.cType == CollisionType.PHANTOM) {
            return;
        }
        Point2D field1 = hb1.getTranslates();
        Point2D field2 = hb2.getTranslates();
        Point2D approach = field2.subtract(field1);
        Platform.runLater(() -> {
            try {
                double collisionFactor = 10;
                if (hb1.cType == CollisionType.WEAK || hb2.cType == CollisionType.WEAK) {
                    collisionFactor = .275;
                }
                if (hb1.mass == Double.POSITIVE_INFINITY) {
                    Point2D approach0 = approach.normalize();
                    if (hb2.movableOwner != null) {
                        hb2.movableOwner.shiftCenter(approach0.getX() * collisionFactor, approach0.getY() * collisionFactor);
                    }
                } else if (hb2.mass == Double.POSITIVE_INFINITY) {
                    Point2D approach0 = approach.normalize();
                    if (hb1.movableOwner != null) {
                        hb1.movableOwner.shiftCenter(-approach0.getX() * collisionFactor, -approach0.getY() * collisionFactor);
                    }
                } else {
                    double rr1 = hb2.mass / (hb1.mass + hb2.mass);
                    double rr2 = hb1.mass / (hb1.mass + hb2.mass);
                    Point2D approach0 = approach.normalize();
                    hb1.movableOwner.shiftCenter(-rr1 * approach0.getX() * collisionFactor, -rr1 * approach0.getY() * collisionFactor);
                    hb2.movableOwner.shiftCenter(rr2 * approach0.getX() * collisionFactor, rr2 * approach0.getY() * collisionFactor);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private static boolean inRange(Hitbox hb1, Hitbox hb2) {
        try {
            return (hb1.getTranslates().distance(hb2.getTranslates()) < hb1.visual.getRadius() + hb2.visual.getRadius());
        } catch (Throwable t) {
            return false;
        }
    }

    public static void register(Hitbox hb) {
        try {
            COLLIDABLE.add(hb);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void unregister(Hitbox hb) {
        try {
            COLLIDABLE.remove(hb);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void clear() {
        COLLIDABLE.clear();
    }

    private final Circle visual;

    private final BooleanProperty collisionEnabled;

    private Hitbox(double radius, CollisionType cType, Mobile movableOwner, Node check, double mass) {
        visual = new Circle(radius, Color.LIME);
        collisionEnabled = new SimpleBooleanProperty(true);
        visual.visibleProperty().bind(VISIBLE.and(collisionEnabled));
        this.cType = cType;
        this.mass = mass;
        visual.setMouseTransparent(true);
        this.movableOwner = movableOwner;
        this.check = check;
    }

    public Hitbox(double radius, CollisionType cType, Node check, double mass) {
        this(radius, cType, null, check, mass);
    }

    public Hitbox(double radius, CollisionType cType, Mobile movableOwner, double mass) {
        this(radius, cType, movableOwner, movableOwner.getNode(), mass);
    }

    private Supplier<Double> xPos;
    private Supplier<Double> yPos;

    public void setXSupplier(Supplier<Double> xPos) {
        this.xPos = xPos;
    }

    public void setYSupplier(Supplier<Double> yPos) {
        this.yPos = yPos;
    }

    private Point2D getTranslates() {
        return new Point2D(xPos == null ? check.getTranslateX() + visual.getRadius() : (xPos.get()),
                yPos == null ? check.getTranslateY() + visual.getRadius() : (yPos.get()));
    }

    private final Mobile movableOwner;
    private final Node check;

    private CollisionType cType;

    private final double mass;

    public boolean canCollide() {
        return collisionEnabled.get();
    }

    public void disable() {
        collisionEnabled.set(false);
    }

    public void enable() {
        collisionEnabled.set(true);
    }

    public Mobile getMovable() {
        return this.movableOwner;
    }

    public boolean isMoveable() {
        return movableOwner != null;
    }

    public Circle getVisual() {
        return visual;
    }

    public static enum CollisionType {
        STRONG, WEAK, PHANTOM;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.GameObjects;

import itzfx.Hitbox;
import itzfx.Mobile;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public final class Cone extends Mobile {

    private final StackPane cone;
    private final Hitbox hitbox;

    private final BooleanProperty stacked;

    public Cone(double layoutX, double layoutY) {
        super(layoutX, layoutY);
        try {
            Parent loaded = FXMLLoader.load(Cone.class.getResource("Cone.fxml"));
            cone = new StackPane(loaded);
            hitbox = new Hitbox(15, Hitbox.CollisionType.STRONG, this, 1);
            Hitbox.register(hitbox);
            cone.getChildren().add(hitbox.getVisual());
            stacked = new SimpleBooleanProperty();
            super.registerProperties();
            super.centerXProperty().addListener(Mobile.limitToField(15, super.centerXProperty()));
            super.centerYProperty().addListener(Mobile.limitToField(15, super.centerYProperty()));
            super.centerXProperty().addListener(super.exclude20(15));
            super.centerYProperty().addListener(super.exclude20(15));
        } catch (IOException ex) {
            Logger.getLogger(Cone.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void permaDisableCollisions() {
        Hitbox.unregister(hitbox);
    }

    @Override
    public void disableCollision() {
        hitbox.disable();
    }

    @Override
    public void enableCollision() {
        hitbox.enable();
    }

    @Override
    public boolean canCollide() {
        return hitbox.canCollide();
    }

    public BooleanProperty stackedProperty() {
        return stacked;
    }

    public boolean isStacked() {
        return stacked.get();
    }

    public void stack() {
        stacked.set(true);
    }

    public void destack() {
        stacked.set(false);
    }
    
    @Override
    protected void resetProperties() {
        stacked.set(false);
    }

    @Override
    public Parent getNode() {
        return cone;
    }
}

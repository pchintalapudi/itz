/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.GameObjects;

import itzfx.fxml.GameObjects.Cone;
import itzfx.Hitbox;
import itzfx.fxml.Field;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public class Loader {

    private final StackPane loader;

    private final boolean red;

    private Hitbox hitbox;

    public Loader(double layoutX, double layoutY, boolean red) {
        try {
            loader = new StackPane();
            loader.getChildren().add(FXMLLoader.load(Loader.class.getResource("Loader.fxml")));
            loader.setTranslateX(layoutX);
            loader.setTranslateY(layoutY);
            hitbox = new Hitbox(15, Hitbox.CollisionType.STRONG, loader, Double.POSITIVE_INFINITY);
            hitbox.setXSupplier(loader::getTranslateX);
            hitbox.setYSupplier(loader::getTranslateY);
            Hitbox.register(hitbox);
            loader.getChildren().add(hitbox.getVisual());
            this.red = red;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Cone load() {
        if (!Field.getOwner(this).hasCone(this)) {
            Cone load = Field.getOwner(this).getLoadableCone(red);
            if (load != null) {
                Platform.runLater(() -> {
                    load.disableCollision();
                    load.setCenter(getCenter().getX(), getCenter().getY());
                });
                return load;
            }
        }
        return null;
    }

    public Point2D getCenter() {
        return new Point2D(red ? 15 : 302, red ? 302 : 15);
    }

    public Node getNode() {
        return loader;
    }
}

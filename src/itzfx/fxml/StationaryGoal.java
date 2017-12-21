/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import itzfx.Hitbox;
import itzfx.scoring.ScoreReport;
import itzfx.scoring.Scoreable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author prem
 */
public final class StationaryGoal implements Scoreable {

    private final StackPane statGoal;

    private final ObservableList<Cone> stacked;
    private final Hitbox hitbox;

    private final boolean red;

    private final ScoreReport sr;

    public StationaryGoal(double layoutX, double layoutY, boolean red) {
        this.stacked = FXCollections.observableList(new LinkedList<>());
        Label stackLabel = new Label();
        statGoal = new StackPane();
        stackLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> stacked.size() > 0, stacked));
        stackLabel.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(stacked.size()), stacked));
        this.hitbox = new Hitbox(12.5, Hitbox.CollisionType.STRONG, statGoal, Double.POSITIVE_INFINITY);
        Hitbox.register(hitbox);
        try {
            statGoal.getChildren().add(FXMLLoader.load(StationaryGoal.class.getResource("StationaryGoal.fxml")));
            Pane cone = FXMLLoader.load(StationaryGoal.class.getResource("StackedCone.fxml"));
            cone.visibleProperty().bind(stackLabel.visibleProperty());
            statGoal.getChildren().addAll(cone, stackLabel);
        } catch (IOException ex) {
            Logger.getLogger(StationaryGoal.class.getName()).log(Level.SEVERE, null, ex);
        }
        statGoal.getChildren().add(hitbox.getVisual());
        statGoal.setTranslateX(layoutX - 12.5);
        statGoal.setTranslateY(layoutY - 12.5);
        stacked.addListener((ListChangeListener.Change<? extends Cone> change) -> {
            change.next();
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(c -> c.stack());
            } else if (change.wasRemoved()) {
                change.getRemoved().forEach(c -> c.destack());
            }
        });
        this.red = red;
        sr = new ScoreReport(this);
        sr.setScoreType(ScoreType.STAT_GOAL);
    }

    public ScoreReport getReporter() {
        return sr;
    }

    public void stack(Cone cone) {
        stacked.add(cone);
    }

    public Cone destack() {
        return stacked.size() > 0 ? stacked.remove(0) : null;
    }

    public void shiftStack(MobileGoal other) {
        while (stacked.size() > 0) {
            other.stack(stacked.remove(0));
        }
    }

    public void reset() {
        Platform.runLater(() -> {
            while (stacked.size() > 0) {
                destack().reset();
            }
        });
    }

    @Override
    public boolean isRed() {
        return red;
    }

    @Override
    public int score() {
        return stacked.size() * 2;
    }

    public Node getNode() {
        return statGoal;
    }
}

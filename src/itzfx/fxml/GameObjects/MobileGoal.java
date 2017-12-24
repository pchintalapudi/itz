/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.GameObjects;

import itzfx.Hitbox;
import itzfx.Mobile;
import itzfx.scoring.ScoreType;
import itzfx.scoring.ScoreReport;
import itzfx.scoring.Scoreable;
import java.io.IOException;
import java.util.LinkedList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public abstract class MobileGoal extends Mobile implements Scoreable {

    private final ObservableList<Cone> stacked;
    private final IntegerProperty countModifier;
    private Hitbox hitbox;
    private final Text stackLabel;

    private final ScoreReport sr;

    protected MobileGoal(double layoutX, double layoutY) {
        super(layoutX, layoutY);
        this.stacked = FXCollections.observableList(new LinkedList<>());
        this.stackLabel = new Text();
        sr = new ScoreReport(this);
        countModifier = new SimpleIntegerProperty();
    }

    @Override
    protected void registerProperties() {
        try {
            Pane cone = FXMLLoader.load(MobileGoal.class.getResource("StackedCone.fxml"));
            cone.visibleProperty().bind(stackLabel.visibleProperty());
            getNode().getChildren().add(cone);
        } catch (IOException ex) {
        }
        stackLabel.setEffect(new InnerShadow());
        stackLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> stacked.size() + countModifier.intValue() > 0, stacked, countModifier));
        stackLabel.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(stacked.size() + countModifier.intValue()), stacked, countModifier));
        stacked.addListener((Change<? extends Cone> change) -> {
            change.next();
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(c -> c.stack());
            } else if (change.wasRemoved()) {
                change.getRemoved().forEach(c -> c.destack());
            }
        });
        this.hitbox = new Hitbox(25, Hitbox.CollisionType.STRONG, this, 10);
        Hitbox.register(hitbox);
        getNode().getChildren().addAll(stackLabel, hitbox.getVisual());
        super.registerProperties();
        super.centerXProperty().addListener(Mobile.limitToField(25, super.centerXProperty()));
        super.centerYProperty().addListener(Mobile.limitToField(25, super.centerYProperty()));
        super.centerXProperty().addListener(super.exclude20(25));
        super.centerYProperty().addListener(super.exclude20(25));
    }

    @Override
    protected void rightClickOptions(ContextMenu rightClick) {
        MenuItem stack = new MenuItem("Stack");
        stack.setOnAction(e -> countModifier.set(countModifier.get() + 1));
        rightClick.getItems().add(stack);
        MenuItem destack = new MenuItem("Destack");
        destack.setOnAction(e -> countModifier.set(countModifier.get() + stacked.size() > 0 ? countModifier.get() - 1 : -stacked.size()));
        rightClick.getItems().add(destack);
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

    @Override
    public void permaDisableCollisions() {
        Hitbox.unregister(hitbox);
    }

    @Override
    protected void resetProperties() {
        while (stacked.size() > 0) {
            destack().reset();
        }
        countModifier.set(0);
    }

    @Override
    public abstract StackPane getNode();

    @Override
    public int score() {
        sr.setScoreType(determineScoringZone());
        return stacked.size() * 2;
    }

    protected abstract ScoreType determineScoringZone();
}

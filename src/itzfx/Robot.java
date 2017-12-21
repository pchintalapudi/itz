/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

import itzfx.fxml.MobileGoal;
import itzfx.fxml.StationaryGoal;
import itzfx.fxml.RedMobileGoal;
import itzfx.fxml.BlueMobileGoal;
import itzfx.fxml.Cone;
import itzfx.fxml.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author prem
 */
public final class Robot extends Mobile {

    private final StackPane node;
    private final StackPane realRobot;

    private final double robotSpeed;
    private final double robotMogoIntakeTime;
    private final double robotAutostackTime;
    private final double robotStatTime;
    private int robotMogoMaxStack;
    private int robotStatMaxStack;
    private boolean mogoIntakeFront;

    private final Hitbox hb;

    private final ObjectProperty<Paint> filter;

    private final BooleanProperty active;

    private final BooleanProperty driveBaseMovable;

    private final BooleanProperty red;

    public Robot(double layoutX, double layoutY, double initRotate) {
        super(layoutX, layoutY, initRotate);
        node = new StackPane();
        realRobot = new StackPane();
        realRobot.setEffect(new DropShadow());
        node.getChildren().add(realRobot);
        node.setOnMouseDragged((MouseEvent m) -> super.setCenter(m.getSceneX() - 120, m.getSceneY() - 120 - 45));
        ImageView iv = new ImageView(new Image(Robot.class.getResourceAsStream("topviewicon.png"), 90, 90, false, true));
        iv.setRotate(90);
        realRobot.getChildren().add(new Pane(iv));
        Rectangle cover = new Rectangle(90, 90);
        filter = cover.fillProperty();
        realRobot.getChildren().add(cover);
        hb = new Hitbox(45, Hitbox.CollisionType.STRONG, this, 18);
        hitboxing();
        filter.set(new Color(1, 0, 0, .03));
        red = new SimpleBooleanProperty(true);
        filter.bind(Bindings.createObjectBinding(() -> red.get() ? new Color(1, 0, 0, .05) : new Color(0, 0, 1, .05), red));
        robotSpeed = 24;
        robotMogoIntakeTime = 2.2;
        robotAutostackTime = 2;
        robotStatTime = 2.5;
        active = new SimpleBooleanProperty(true);
        actions = new LinkedList<>();
        driveBaseMovable = new SimpleBooleanProperty(true);
        redMogo = new RedMobileGoal(25, 45);
        blueMogo = new BlueMobileGoal(25, 45);
        node.getChildren().add(redMogo.getNode());
        node.getChildren().add(blueMogo.getNode());
        privateCone = new Cone(90, 45);
        node.getChildren().add(privateCone.getNode());
        privateCone.permaDisableCollisions();
        privateCone.vanish();
        properties();
        mogoUndo();
        linkActions();
        setController(KeyControl.Defaults.SINGLE.getKC());
    }

    public void registerMogos() {
        Field.getOwner(this).register(redMogo);
        Field.getOwner(this).register(blueMogo);
    }

    public boolean owner(MobileGoal mogo) {
        return mogo == redMogo || mogo == blueMogo;
    }

    private void mogoUndo() {
        redMogo.getNode().translateXProperty().unbind();
        blueMogo.getNode().translateXProperty().unbind();
        redMogo.permaDisableCollisions();
        blueMogo.permaDisableCollisions();
        redMogo.vanish();
        blueMogo.vanish();
    }

    @Override
    protected DoubleBinding translateXBind() {
        return Bindings.createDoubleBinding(() -> {
            Node n = realRobot;
            return super.centerXProperty().get() - (n.getBoundsInLocal().getWidth() / 2 + n.getBoundsInLocal().getMinX());
        }, realRobot.boundsInLocalProperty(), super.centerXProperty());
    }

    @Override
    protected DoubleBinding translateYBind() {
        return Bindings.createDoubleBinding(() -> {
            Node n = realRobot;
            return super.centerYProperty().get() - (n.getBoundsInLocal().getHeight() / 2 + n.getBoundsInLocal().getMinY());
        }, super.centerYProperty(), realRobot.boundsInLocalProperty());
    }

    private void hitboxing() {
        hb.setXSupplier(super.centerXProperty()::get);
        hb.setYSupplier(super.centerYProperty()::get);
        node.getChildren().add(hb.getVisual());
        Hitbox.register(hb);
    }

    private void properties() {
        super.centerXProperty().addListener(Mobile.limitToField(45, centerXProperty()));
        super.centerYProperty().addListener(Mobile.limitToField(45, centerYProperty()));
        super.centerXProperty().addListener(super.exclude20(65));
        super.centerYProperty().addListener(super.exclude20(65));
        super.registerProperties();
    }

    private void linkActions() {
        actions.add(k -> Platform.runLater(this::forward));
        actions.add(k -> Platform.runLater(this::leftTurn));
        actions.add(k -> Platform.runLater(this::backward));
        actions.add(k -> Platform.runLater(this::rightTurn));
        actions.add(k -> Platform.runLater(this::mogo));
        actions.add(k -> Platform.runLater(this::autostack));
        actions.add(k -> Platform.runLater(this::cone));
        actions.add(k -> Platform.runLater(this::statStack));
        actions.add(k -> Platform.runLater(this::load));
    }

    private final List<Consumer<KeyCode>> actions;

    private KeyControl controller;

    public final void setController(KeyControl controller) {
        Iterator<KeyCode> iteratorNew = Arrays.asList(controller.keys()).iterator();
        if (this.controller != null) {
            Iterator<KeyCode> iteratorOld = Arrays.asList(this.controller.keys()).iterator();
            actions.stream().peek(a -> KeyBuffer.remove(iteratorOld.next(), a)).forEach(a -> KeyBuffer.register(iteratorNew.next(), a));
        } else {
            actions.stream().forEach(a -> KeyBuffer.register(iteratorNew.next(), a));
        }
        this.controller = controller;
    }

    @Override
    public void disableCollision() {
        hb.disable();
    }

    @Override
    public void enableCollision() {
        hb.enable();
    }

    @Override
    public boolean canCollide() {
        return hb.canCollide();
    }

    @Override
    public void permaDisableCollisions() {
        Hitbox.unregister(hb);
    }

    public void setRed(boolean red) {
        this.red.set(red);
    }

    public boolean isRed() {
        return red.get();
    }

    public BooleanProperty redProperty() {
        return red;
    }

    private void forward() {
        if (active.get() && driveBaseMovable.get()) {
            super.shiftCenter(robotSpeed / 12 * Math.cos(Math.toRadians(node.getRotate())),
                    robotSpeed / 12 * Math.sin(Math.toRadians(node.getRotate())));
        }
    }

    private void backward() {
        if (active.get() && driveBaseMovable.get()) {
            super.shiftCenter(-robotSpeed / 12 * Math.cos(Math.toRadians(node.getRotate())),
                    -robotSpeed / 12 * Math.sin(Math.toRadians(node.getRotate())));
        }
    }

    private void leftTurn() {
        if (active.get() && driveBaseMovable.get()) {
            node.setRotate(node.getRotate() - robotSpeed / (Math.PI * 7));
        }
    }

    private void rightTurn() {
        if (active.get() && driveBaseMovable.get()) {
            node.setRotate(node.getRotate() + robotSpeed / (Math.PI * 7));
        }
    }

    private final BooleanProperty movingMogo = new SimpleBooleanProperty();

    private final Timeline mogoAnimation = new Timeline();
    private final MobileGoal redMogo;
    private final MobileGoal blueMogo;

    private final ObjectProperty<MobileGoal> privateMogo = new SimpleObjectProperty<>();
    private final ObjectProperty<MobileGoal> heldMogo = new SimpleObjectProperty<>();

    public void mogo() {
        try {
            if (active.get()) {
                if (!movingMogo.get()) {
                    if (heldMogo.get() == null) {
                        mogoIntake();
                    } else {
                        mogoOuttake();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mogoIntake() {
        MobileGoal mogo = Field.getOwner(this).huntMogo(new Point2D(super.getCenterX(), super.getCenterY()),
                new Point2D(70 * Math.cos(Math.toRadians(node.getRotate())) * (mogoIntakeFront ? 1 : -1),
                        70 * Math.sin(Math.toRadians(node.getRotate())) * (mogoIntakeFront ? 1 : -1)));
        if (mogo != null) {
            privateMogo.set(mogo instanceof RedMobileGoal ? redMogo : blueMogo);
            heldMogo.set(mogo);
            mogo.vanish();
            heldMogo.get().shiftStack(privateMogo.get());
            privateMogo.get().getNode().setTranslateX(mogoIntakeFront ? 70 : -70);
            privateMogo.get().reappear();
            movingMogo.set(true);
            mogoAnimation.stop();
            mogoAnimation.getKeyFrames().clear();
            mogoAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(this.robotMogoIntakeTime), this::finishMogoIntake,
                    new KeyValue(privateMogo.get().getNode().translateXProperty(), mogoIntakeFront ? 25 : -25)));
            mogoAnimation.play();
        }
    }

    private void finishMogoIntake(ActionEvent e) {
        e.consume();
        mogoAnimation.stop();
        movingMogo.set(false);
    }

    private void mogoOuttake() {
        movingMogo.set(true);
        mogoAnimation.stop();
        mogoAnimation.getKeyFrames().clear();
        mogoAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(this.robotMogoIntakeTime), this::finishMogoOuttake,
                new KeyValue(privateMogo.get().getNode().translateXProperty(), -70)));
        mogoAnimation.play();
    }

    private void finishMogoOuttake(ActionEvent e) {
        e.consume();
        mogoAnimation.stop();
        privateMogo.get().vanish();
        privateMogo.get().shiftStack(heldMogo.get());
        heldMogo.get().setCenter(super.getCenterX() + 70 * Math.cos(Math.toRadians(node.getRotate())) * (mogoIntakeFront ? 1 : -1),
                super.getCenterY() + 70 * Math.sin(Math.toRadians(node.getRotate())) * (mogoIntakeFront ? 1 : -1));
        heldMogo.get().reappear();
        heldMogo.set(null);
        movingMogo.set(false);
    }

    private final BooleanProperty movingCone = new SimpleBooleanProperty();

    private final Cone privateCone;
    private final ObjectProperty<Cone> heldCone = new SimpleObjectProperty<>();

    private long lastConeMove;

    public void cone() {
        try {
            if (active.get()) {
                if (!movingCone.get() && System.currentTimeMillis() > 100 + lastConeMove) {
                    lastConeMove = System.currentTimeMillis();
                    if (heldCone.get() == null) {
                        coneIntake();
                    } else {
                        coneOuttake();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void coneIntake() {
        Cone cone = Field.getOwner(this).huntCone(new Point2D(super.getCenterX(), super.getCenterY()),
                new Point2D(60 * Math.cos(Math.toRadians(node.getRotate())),
                        60 * Math.sin(Math.toRadians(node.getRotate()))));
        if (cone != null) {
            forceIntake(cone);
        }
    }

    private void coneOuttake() {
        privateCone.vanish();
        heldCone.get().setCenter(super.getCenterX() + 60 * Math.cos(Math.toRadians(node.getRotate())),
                super.getCenterY() + 60 * Math.sin(Math.toRadians(node.getRotate())));
        heldCone.get().reappear();
        heldCone.set(null);
    }

    private final Timeline autostackAnimation = new Timeline();

    public void autostack() {
        if (active.get() && heldMogo.get() != null && !movingCone.get()) {
            if (heldCone.get() == null) {
                coneIntake();
            }
            if (heldCone.get() != null) {
                runAutostack();
            }
        }
    }

    private void runAutostack() {
        autostackAnimation.stop();
        autostackAnimation.getKeyFrames().clear();
        movingCone.set(true);
        autostackAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(this.robotAutostackTime), this::finishAutostack,
                new KeyValue(privateCone.centerXProperty(), 25)));
        autostackAnimation.play();
    }

    private void finishAutostack(ActionEvent e) {
        e.consume();
        privateCone.vanish();
        autostackAnimation.stop();
        movingCone.set(false);
        privateMogo.get().stack(heldCone.get());
        heldCone.set(null);
    }

    public void statStack() {
        try {
            if (active.get()) {
                if (!movingCone.get() && heldCone.get() != null) {
                    runStatStack();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void runStatStack() {
        StationaryGoal sg = Field.getOwner(this).huntStat(new Point2D(super.getCenterX(), super.getCenterY()), new Point2D(57.5 * Math.cos(Math.toRadians(node.getRotate())),
                57.5 * Math.sin(Math.toRadians(node.getRotate()))));
        if (sg != null) {
            Point2D sgCenter = new Point2D(sg.getNode().getTranslateX() + 12.5, sg.getNode().getTranslateY() + 12.5);
            heldCone.get().setCenter(super.getCenterX() + 60 * Math.cos(Math.toRadians(node.getRotate())),
                    super.getCenterY() + 60 * Math.sin(Math.toRadians(node.getRotate())));
            privateCone.vanish();
            heldCone.get().reappear();
            heldCone.get().disableCollision();
            driveBaseMovable.set(false);
            movingCone.set(true);
            autostackAnimation.stop();
            autostackAnimation.getKeyFrames().clear();
            autostackAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(this.robotStatTime), e -> finishStat(e, sg),
                    new KeyValue(heldCone.get().centerXProperty(), sgCenter.getX()), new KeyValue(heldCone.get().centerYProperty(), sgCenter.getY())));
            autostackAnimation.play();
        }
    }

    private void finishStat(ActionEvent e, StationaryGoal sg) {
        e.consume();
        movingCone.set(false);
        autostackAnimation.stop();
        driveBaseMovable.set(true);
        sg.stack(heldCone.get());
        heldCone.get().vanish();
        heldCone.set(null);
    }

    public void load() {
        Field.getOwner(this).load(this);
    }

    /**
     * @TreatAsPrivate @param cone the cone to intake
     * @deprecated only public for field reset
     */
    @Deprecated
    public void forceIntake(Cone cone) {
        heldCone.set(cone);
        cone.vanish();
        privateCone.setX(90);
        privateCone.reappear();
    }

    @Override
    public void resetProperties() {
        mogoAnimation.stop();
        autostackAnimation.stop();
        if (heldMogo.get() != null) {
            privateMogo.get().shiftStack(heldMogo.get());
            heldMogo.get().reset();
            heldMogo.set(null);
        }
        if (heldCone.get() != null) {
            heldCone.get().reset();
            heldCone.set(null);
        }
        privateCone.vanish();
        redMogo.vanish();
        blueMogo.vanish();
        movingMogo.set(false);
        movingCone.set(false);
        active.set(true);
    }

    @Override
    public StackPane getNode() {
        return node;
    }
}

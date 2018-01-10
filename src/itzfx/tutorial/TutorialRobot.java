/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial;

import itzfx.Hitbox;
import itzfx.KeyBuffer;
import itzfx.KeyControl;
import itzfx.Mobile;
import itzfx.Robot;
import itzfx.fxml.GameObjects.Cone;
import itzfx.fxml.GameObjects.MobileGoal;
import itzfx.fxml.GameObjects.RedMobileGoal;
import itzfx.fxml.GameObjects.StationaryGoal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author prem
 */
public class TutorialRobot extends Mobile {

    private final StackPane node;
    private final StackPane realRobot;

    private double robotSpeed;
    private double robotMogoIntakeTime;
    private double robotAutostackTime;
    private double robotStatTime;
    private int robotMogoMaxStack;
    private int robotStatMaxStack;
    private boolean robotMogoFront;

    private final BooleanProperty driveBaseMovable;

    private final Hitbox hb;

    public TutorialRobot(double layoutX, double layoutY, double initRotate) {
        super(layoutX, layoutY, initRotate);
        node = new StackPane();
        realRobot = new StackPane();
        realRobot.setEffect(new DropShadow());
        node.getChildren().add(realRobot);
        node.setOnMouseDragged((MouseEvent m) -> super.setCenter(m.getSceneX() - 120, m.getSceneY() - 120 - 45));
        ImageView iv = new ImageView(new Image(Robot.class.getResourceAsStream("/itzfx/Images/topviewicon.png"), 90, 90, false, true));
        iv.setRotate(90);
        realRobot.getChildren().add(new Pane(iv));
        hb = new Hitbox(45, Hitbox.CollisionType.STRONG, this, 18);
        hitboxing();
        actions = new LinkedList<>();
        redMogo = new RedMobileGoal(25, 45);
        node.getChildren().add(redMogo.getNode());
        privateCone = new Cone(90, 45);
        node.getChildren().add(privateCone.getNode());
        privateCone.permaDisableCollisions();
        privateCone.vanish();
        properties();
        mogoUndo();
        linkActions();
        preassignValues();
        driveBaseMovable = new SimpleBooleanProperty(true);
    }

    private void preassignValues() {
        robotSpeed = 24;
        robotMogoIntakeTime = 2.2;
        robotAutostackTime = 2;
        robotStatTime = 2.5;
        robotMogoMaxStack = 12;
        robotStatMaxStack = 5;
    }

    private void mogoUndo() {
        redMogo.getNode().translateXProperty().unbind();
        redMogo.permaDisableCollisions();
        redMogo.vanish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleBinding translateXBind() {
        return Bindings.createDoubleBinding(() -> {
            Node n = realRobot;
            return super.centerXProperty().get() - (n.getBoundsInLocal().getWidth() / 2 + n.getBoundsInLocal().getMinX());
        }, super.centerXProperty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleBinding translateYBind() {
        return Bindings.createDoubleBinding(() -> {
            Node n = realRobot;
            return super.centerYProperty().get() - (n.getBoundsInLocal().getHeight() / 2 + n.getBoundsInLocal().getMinY());
        }, super.centerYProperty());
    }

    /**
     *
     *
     * @see {@link Mobile#cleanUp}
     */
    @Override
    protected void cleanUp() {
        mogoAnimation.stop();
        stackAnimation.stop();
        if (heldMogo.get() != null) {
            heldMogo.get().setCenter(super.getCenterX() + privateMogo.get().getNode().getTranslateX() * Math.cos(Math.toRadians(node.getRotate())),
                    super.getCenterY() + privateMogo.get().getNode().getTranslateX() * Math.sin(Math.toRadians(node.getRotate())));
            heldMogo.get().reappear();
            heldMogo.set(null);
        }
        if (heldCone.get() != null) {
            heldCone.get().setCenter(super.getCenterX() + privateCone.getCenterX() * Math.cos(Math.toRadians(node.getRotate())),
                    super.getCenterY() + privateCone.getCenterX() * Math.sin(Math.toRadians(node.getRotate())));
            heldCone.get().reappear();
            heldCone.set(null);
        }
    }

    private void hitboxing() {
        hb.setXSupplier(super.centerXProperty()::get);
        hb.setYSupplier(super.centerYProperty()::get);
        node.getChildren().add(hb.getVisual());
        Hitbox.register(hb);
    }

    private ChangeListener<Number> limitToViewX(DoubleProperty limit) {
        return (ObservableValue<? extends Number> obs, Number old, Number next) -> {
            if (next.doubleValue() < 45 && next.doubleValue() > -50) {
                limit.set(45 + Math.random());
            } else if (next.doubleValue() + 45 > 480) {
                limit.set(480 - 45 - Math.random());
            }
        };
    }

    private ChangeListener<Number> limitToViewY(DoubleProperty limit) {
        return (ObservableValue<? extends Number> obs, Number old, Number next) -> {
            if (next.doubleValue() < 45 && next.doubleValue() > -50) {
                limit.set(45 + Math.random());
            } else if (next.doubleValue() + 45 > 360) {
                limit.set(360 - 45 - Math.random());
            }
        };
    }

    private void properties() {
        super.centerXProperty().addListener(limitToViewX(centerXProperty()));
        super.centerYProperty().addListener(limitToViewY(centerYProperty()));
        super.centerXProperty().addListener(super.exclude20(65));
        super.centerYProperty().addListener(super.exclude20(65));
        super.registerProperties();
    }

    private void linkActions() {
        actions.add(k -> forward());
        actions.add(k -> leftTurn());
        actions.add(k -> backward());
        actions.add(k -> rightTurn());
        actions.add(k -> mogo());
        actions.add(k -> autostack());
        actions.add(k -> cone());
        actions.add(k -> statStack());
        actions.add(k -> load());
    }

    private final List<Consumer<KeyCode>> actions;

    private KeyControl controller;

    /**
     * Gets the control format used by this Robot. A control format consists of
     * a set of {@link KeyCode KeyCodes} linked to specific actions. This robot
     * accepts a {@link KeyControl} as a valid control format.
     *
     * @return a KeyControl that represents this robot's control format
     */
    public KeyControl getController() {
        return controller;
    }

    /**
     * Temporarily erases the control format associated with this robot. The
     * intent of this method is to disable driver control during autonomous and
     * programming skills.
     */
    public void eraseController() {
        Iterator<KeyCode> iteratorOld = Arrays.asList(this.controller.keys()).iterator();
        actions.forEach(a -> KeyBuffer.remove(iteratorOld.next(), a));
    }

    /**
     * Sets the control format used by this Robot. A control format consists of
     * a set of {@link KeyCode KeyCodes} linked to specific actions. This robot
     * accepts a {@link KeyControl} as a valid control format.
     *
     * @param controller the KeyControl to set as a control format
     */
    public void setController(KeyControl controller) {
        if (controller != null) {
            Iterator<KeyCode> iteratorNew = Arrays.asList(controller.keys()).iterator();
            if (this.controller != null) {
                Iterator<KeyCode> iteratorOld = Arrays.asList(this.controller.keys()).iterator();
                actions.stream().peek(a -> KeyBuffer.remove(iteratorOld.next(), a)).forEach(a -> KeyBuffer.register(iteratorNew.next(), a));
            } else {
                actions.stream().forEach(a -> KeyBuffer.register(iteratorNew.next(), a));
            }
            this.controller = controller;
        }
    }

    public static List<EventHandler<KeyEvent>> getActionList(TutorialRobot r) {
        Iterator<Consumer<KeyCode>> itr = r.actions.iterator();
        return Arrays.stream(r.controller.keys()).map(k -> new Mapping<>(k, itr.next()))
                .filter(m -> m.getKey() != KeyCode.UNDEFINED)
                .map(m -> (EventHandler<KeyEvent>) (k -> {
            if (k.getCode() == m.getKey()) {
                m.getValue().accept(k.getCode());
            }
        })).collect(Collectors.toList());
    }

    private static class Mapping<K, V> {

        private final K key;
        private final V value;

        public Mapping(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableCollision() {
        hb.disable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableCollision() {
        hb.enable();
    }

    /**
     * {@inheritDoc}
     *
     * @return true if the robot can collide
     */
    @Override
    public boolean canCollide() {
        return hb.canCollide();
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void permaDisableCollisions() {
        Hitbox.unregister(hb);
    }

    /**
     * Moves the robot forwards. This does take into account robot speed.
     */
    public void forward() {
        if (driveBaseMovable.get()) {
            Platform.runLater(() -> {
                super.shiftCenter(robotSpeed / 12 * Math.cos(Math.toRadians(node.getRotate())),
                        robotSpeed / 12 * Math.sin(Math.toRadians(node.getRotate())));
            });
        }
    }

    /**
     * Moves the robot backwards. This does take into account robot speed.
     */
    public void backward() {
        if (driveBaseMovable.get()) {
            Platform.runLater(() -> {
                super.shiftCenter(-robotSpeed / 12 * Math.cos(Math.toRadians(node.getRotate())),
                        -robotSpeed / 12 * Math.sin(Math.toRadians(node.getRotate())));
            });
        }
    }

    /**
     * Turns the robot to the left. This does take into account robot speed.
     */
    public void leftTurn() {
        if (driveBaseMovable.get()) {
            Platform.runLater(() -> {
                node.setRotate(node.getRotate() - robotSpeed / (Math.PI * 7));
            });
        }
    }

    /**
     * MOves the robot to the right. This does take into account robot speed.
     */
    public void rightTurn() {
        if (driveBaseMovable.get()) {
            Platform.runLater(() -> {
                node.setRotate(node.getRotate() + robotSpeed / (Math.PI * 7));
            });
        }
    }

    private final BooleanProperty movingMogo = new SimpleBooleanProperty();

    private final Timeline mogoAnimation = new Timeline();
    private final MobileGoal redMogo;

    private final ObjectProperty<MobileGoal> privateMogo = new SimpleObjectProperty<>();
    private final ObjectProperty<MobileGoal> heldMogo = new SimpleObjectProperty<>();

    /**
     * Toggles intake/outtake of mobile goal, and attempts to do so.
     */
    public void mogo() {
        if (!movingMogo.get()) {
            if (heldMogo.get() == null) {
                Platform.runLater(() -> {
//                        mogoIntake();
                });
            } else {
                Platform.runLater(() -> {
                    mogoOuttake();
                });
            }
        }
    }

    private void mogoIntake(MobileGoal mogo) {
//        MobileGoal mogo = Field.getOwner(this).huntMogo(new Point2D(super.getCenterX(), super.getCenterY()),
//                new Point2D(70 * Math.cos(Math.toRadians(node.getRotate())) * (robotMogoFront ? 1 : -1),
//                        70 * Math.sin(Math.toRadians(node.getRotate())) * (robotMogoFront ? 1 : -1)));
        if (mogo != null) {
            privateMogo.set(redMogo);
            heldMogo.set(mogo);
            mogo.vanish();
            heldMogo.get().shiftStack(privateMogo.get());
            privateMogo.get().getNode().setTranslateX(robotMogoFront ? 70 : -70);
            privateMogo.get().reappear();
            movingMogo.set(true);
            mogoAnimation.stop();
            mogoAnimation.getKeyFrames().clear();
            mogoAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(this.robotMogoIntakeTime), this::finishMogoIntake,
                    new KeyValue(privateMogo.get().getNode().translateXProperty(), robotMogoFront ? 25 : -25)));
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
                new KeyValue(privateMogo.get().getNode().translateXProperty(), robotMogoFront ? 70 : -70)));
        mogoAnimation.play();
    }

    private void finishMogoOuttake(ActionEvent e) {
        e.consume();
        mogoAnimation.stop();
        privateMogo.get().vanish();
        privateMogo.get().shiftStack(heldMogo.get());
        heldMogo.get().setCenter(super.getCenterX() + 70 * Math.cos(Math.toRadians(node.getRotate())) * (robotMogoFront ? 1 : -1),
                super.getCenterY() + 70 * Math.sin(Math.toRadians(node.getRotate())) * (robotMogoFront ? 1 : -1));
        heldMogo.get().reappear();
        heldMogo.set(null);
        movingMogo.set(false);
    }

    private final BooleanProperty movingCone = new SimpleBooleanProperty();

    private final Cone privateCone;
    private final ObjectProperty<Cone> heldCone = new SimpleObjectProperty<>();

    private long lastConeMove;

    /**
     * Toggles intake/outtake of a cone, and attempts to do so.
     */
    public void cone() {
        if (!movingCone.get() && System.currentTimeMillis() > 100 + lastConeMove) {
            lastConeMove = System.currentTimeMillis();
            if (heldCone.get() == null) {
                Platform.runLater(() -> {
//                        coneIntake();
                });
            } else {
                Platform.runLater(() -> {
                    coneOuttake();
                });
            }
        }
    }

    private void coneIntake() {
//        Cone cone = Field.getOwner(this).huntCone(new Point2D(super.getCenterX(), super.getCenterY()),
//                new Point2D(60 * Math.cos(Math.toRadians(node.getRotate())),
//                        60 * Math.sin(Math.toRadians(node.getRotate()))));
    }

    private void coneOuttake() {
        privateCone.vanish();
        heldCone.get().setCenter(super.getCenterX() + 60 * Math.cos(Math.toRadians(node.getRotate())),
                super.getCenterY() + 60 * Math.sin(Math.toRadians(node.getRotate())));
        heldCone.get().reappear();
        heldCone.set(null);
    }

    private final Timeline stackAnimation = new Timeline();

    /**
     * Tries to intake a cone if none are held, and autostacks it if one is held
     * following the check.
     */
    public void autostack() {
        if (heldMogo.get() != null && !movingCone.get() && privateMogo.get().score() / 2 < this.robotMogoMaxStack) {
            if (heldCone.get() == null) {
                Platform.runLater(() -> {
//                    coneIntake();
                });
            }
            if (heldCone.get() != null) {
                Platform.runLater(() -> {
                    runAutostack();
                });
            }
        }
    }

    private void runAutostack() {
        stackAnimation.stop();
        stackAnimation.getKeyFrames().clear();
        movingCone.set(true);
        stackAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(this.robotAutostackTime), this::finishAutostack,
                new KeyValue(privateCone.centerXProperty(), robotMogoFront ? 70 : 25)));
        stackAnimation.play();
    }

    private void finishAutostack(ActionEvent e) {
        e.consume();
        privateCone.vanish();
        stackAnimation.stop();
        movingCone.set(false);
        privateMogo.get().stack(heldCone.get());
        heldCone.set(null);
    }

    /**
     * Tries to stack a cone on a nearby stationary goal.
     */
    public void statStack() {
        if (!movingCone.get() && heldCone.get() != null) {
            Platform.runLater(() -> {
//                    runStatStack();
            });
        }
    }

    private void runStatStack(StationaryGoal sg) {
//        StationaryGoal sg = Field.getOwner(this).huntStat(new Point2D(super.getCenterX(), super.getCenterY()), new Point2D(57.5 * Math.cos(Math.toRadians(node.getRotate())),
//                57.5 * Math.sin(Math.toRadians(node.getRotate()))));
        if (sg != null && sg.score() / 2 < robotStatMaxStack) {
            Point2D sgCenter = new Point2D(sg.getNode().getTranslateX() + 12.5, sg.getNode().getTranslateY() + 12.5);
            heldCone.get().setCenter(super.getCenterX() + 60 * Math.cos(Math.toRadians(node.getRotate())),
                    super.getCenterY() + 60 * Math.sin(Math.toRadians(node.getRotate())));
            privateCone.vanish();
            heldCone.get().reappear();
            heldCone.get().disableCollision();
            driveBaseMovable.set(false);
            movingCone.set(true);
            stackAnimation.stop();
            stackAnimation.getKeyFrames().clear();
            stackAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(this.robotStatTime), e -> finishStat(e, sg),
                    new KeyValue(heldCone.get().centerXProperty(), sgCenter.getX()), new KeyValue(heldCone.get().centerYProperty(), sgCenter.getY())));
            stackAnimation.play();
        }
    }

    private void finishStat(ActionEvent e, StationaryGoal sg) {
        e.consume();
        movingCone.set(false);
        stackAnimation.stop();
        driveBaseMovable.set(true);
        sg.stack(heldCone.get());
        heldCone.get().vanish();
        heldCone.set(null);
    }

    /**
     * Tries to load a driver load cone onto this robot's alliance loader.
     */
    public void load() {
    }

    /**
     * @param cone the cone to intake
     */
    public void intake(Cone cone) {
        heldCone.set(cone);
        cone.vanish();
        privateCone.setX(90);
        privateCone.reappear();
    }

    @Override
    public StackPane getNode() {
        return node;
    }
}

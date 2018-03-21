/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import itzfx.Start;
import itzfx.scoring.ScoreAggregator;
import itzfx.ControlMode;
import itzfx.fxml.GameObjects.MobileGoal;
import itzfx.fxml.GameObjects.RedMobileGoal;
import itzfx.fxml.GameObjects.Loader;
import itzfx.fxml.GameObjects.BlueMobileGoal;
import itzfx.fxml.GameObjects.Cone;
import itzfx.fxml.GameObjects.StationaryGoal;
import itzfx.Hitbox;
import itzfx.KeyBuffer;
import itzfx.KeyControl;
import itzfx.Mobile;
import itzfx.Robot;
import itzfx.scoring.ScoreReport;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * FXML Controller class. Controls file "Field.fxml". Handles all field-related
 * work (resetting, layout, loading, match setup, etc). Also communicates with
 * robots to inform them of nearby
 * {@link Cone cones}, {@link MobileGoal mobile goals}, and
 * {@link StationaryGoal stationary goals}.
 *
 * @author Prem Chintalapudi 5776E
 */
public class Field implements AutoCloseable {

    private static final List<Field> FIELDS;

    static {
        FIELDS = new ArrayList<>();
    }

    @FXML
    private BorderPane field;
    @FXML
    private Pane center;

    private final List<Robot> robots;
    private final List<Cone> onField;
    private final List<Cone> redDriverLoads;
    private final List<Cone> blueDriverLoads;
    private final List<Cone> preloads;
    private final List<MobileGoal> mogos;
    private Loader rLoad;
    private Loader bLoad;
    private StationaryGoal rStat;
    private StationaryGoal bStat;

    private final List<Mobile> added;

    /**
     * Constructs a new field, usually called by {@link FXMLLoader}.
     */
    public Field() {
        robots = new ArrayList<>();
        onField = new ArrayList<>();
        redDriverLoads = new ArrayList<>();
        blueDriverLoads = new ArrayList<>();
        preloads = new ArrayList<>();
        mogos = new ArrayList<>();
        added = new ArrayList<>();
    }

    private PulseManager pulseManager;

    @FXML
    private void initialize() {
        FIELDS.add(this);
        field.setUserData(this);
        setRobots();
        addLoaders();
        dropCones();
        dropMogos();
        addStats();
        decorateScoringBars();
        (pulseManager = new PulseManager(Start.PULSER)).begin();
        Start.PULSER.schedule(this::reset, 3, TimeUnit.SECONDS);
    }

    private class PulseManager {

        private final ScheduledExecutorService executor;
        private long lastTimeChecked;
        private final Runnable inputTask = () -> {
            if (KeyBuffer.pulse()) {
                lastTimeChecked = System.currentTimeMillis();
                if (this.scorePulser == null) {
                    beginScorePulsing();
                    beginCollisionPulsing();
                }
            }
        };
        private final Runnable scoreTask = () -> {
            if (sbc != null && mode != null) {
                Platform.runLater(() -> {
                    switch (mode) {
                        case AUTON:
                            sbc.pulseAuton();
                            break;
                        default:
                            sbc.pulse();
                            break;
                    }
                });
            }
        };
        private final Runnable collisionTask = Hitbox::pulse;
        private ScheduledFuture<?> inputPulser;
        private ScheduledFuture<?> scorePulser;
        private ScheduledFuture<?> collisionPulser;
        private ScheduledFuture<?> disuseMonitor;

        public PulseManager(ScheduledExecutorService executor) {
            this.executor = executor;
        }

        public void begin() {
            if (disuseMonitor == null) {
                inputPulser = executor.scheduleWithFixedDelay(inputTask, 0, 17, TimeUnit.MILLISECONDS);
                disuseMonitor = executor.scheduleWithFixedDelay(() -> {
                    if (lastTimeChecked + 1000 < System.currentTimeMillis()) {
                        stopScorePulsing();
                        stopCollisionPulsing();
                    }
                }, 10, 5, TimeUnit.SECONDS);
            }
        }

        private void beginScorePulsing() {
            scorePulser = executor.scheduleWithFixedDelay(scoreTask, 0, 17, TimeUnit.MILLISECONDS);
        }

        private void beginCollisionPulsing() {
            collisionPulser = executor.scheduleWithFixedDelay(collisionTask, 0, 17, TimeUnit.MILLISECONDS);
        }

        private void stopCollisionPulsing() {
            if (collisionPulser != null) {
                collisionPulser.cancel(false);
                collisionPulser = null;
            }
        }

        private void stopScorePulsing() {
            if (scorePulser != null) {
                scorePulser.cancel(false);
                scorePulser = null;
            }
        }

        private void stopInputPulsing() {
            if (inputPulser != null) {
                inputPulser.cancel(false);
                inputPulser = null;
            }
        }

        public void stop() {
            stopInputPulsing();
            stopScorePulsing();
            stopCollisionPulsing();
            if (disuseMonitor != null) {
                disuseMonitor.cancel(false);
                disuseMonitor = null;
            }
        }
    }

    private void decorateScoringBars() {
        decorate10(red10, true);
        decorate10(blue10, false);
    }

    private void dropCones() {
        dropMiddleCones();
        dropLeftCones();
        dropRightCones();
        dropPreloads();
        dropRedDriverLoads();
        dropBlueDriverLoads();
        onField.stream().map(Cone::getNode).forEach(center.getChildren()::add);
        redDriverLoads.stream().map(Cone::getNode).forEach(center.getChildren()::add);
        blueDriverLoads.stream().map(Cone::getNode).forEach(center.getChildren()::add);
    }

    private void dropMogos() {
        dropBlueMogos();
        dropRedMogos();
        mogos.stream().peek(this::register).map(MobileGoal::getNode).forEach(center.getChildren()::add);
    }

    private void addLoaders() {
        redLoader();
        blueLoader();
        center.getChildren().addAll(rLoad.getNode(), bLoad.getNode());
    }

    private void addStats() {
        redStat();
        blueStat();
        register(rStat);
        register(bStat);
        center.getChildren().add(1, rStat.getNode());
        center.getChildren().add(1, bStat.getNode());
    }

    private void setRobots() {
        robot1();
        robot2();
        robot3();
        robot4();
        getRobots().stream().peek(Robot::registerMogos).map(Robot::getNode).forEach(center.getChildren()::add);
    }

    @FXML
    private StackPane blue10;
    @FXML
    private StackPane red10;

    private void decorate10(Node check, boolean red) {
        for (float i = 2.5f; i < 325; i += 10) {
            float a = 482.5f + i / (float) Math.sqrt(2);
            float b = 2.5f + i / (float) Math.sqrt(2);
            Hitbox h = new Hitbox(2.5f, Hitbox.CollisionType.WEAK, check, Float.POSITIVE_INFINITY);
            if (!red) {
                h.setXSupplier(() -> a);
                h.setYSupplier(() -> b);
            } else {
                h.setXSupplier(() -> b);
                h.setYSupplier(() -> a);
            }
            h.getVisual().setCenterX(red ? b : a);
            h.getVisual().setCenterY(red ? a : b);
            center.getChildren().add(h.getVisual());
            Hitbox.register(h);
        }
    }

    private void dropMiddleCones() {
        onField.addAll(Arrays.asList(
                new Cone(240, 240),
                new Cone(300, 240),
                new Cone(240, 300),
                new Cone(360, 300),
                new Cone(300, 360),
                new Cone(360, 420),
                new Cone(420, 360),
                new Cone(420, 480),
                new Cone(480, 420),
                new Cone(480, 480)
        ));
    }

    private void dropLeftCones() {
        onField.addAll(Arrays.asList(
                new Cone(15, 15),
                new Cone(15, 67.5f),
                new Cone(15, 120),
                new Cone(15, 180),
                new Cone(15, 240),
                new Cone(67.5f, 15),
                new Cone(67.5f, 67.5f),
                new Cone(67.5f, 120),
                new Cone(120, 15),
                new Cone(120, 67.5f),
                new Cone(120, 120),
                new Cone(120, 180),
                new Cone(120, 240),
                new Cone(180, 15),
                new Cone(180, 120),
                new Cone(240, 15),
                new Cone(240, 120)
        ));
    }

    private void dropRightCones() {
        onField.addAll(Arrays.asList(
                new Cone(360, 600),
                new Cone(360, 705),
                new Cone(420, 600),
                new Cone(420, 705),
                new Cone(480, 600),
                new Cone(480, 705),
                new Cone(540, 600),
                new Cone(540, 705),
                new Cone(600, 600),
                new Cone(600, 705),
                new Cone(652.5f, 600),
                new Cone(652.5f, 705),
                new Cone(705, 600),
                new Cone(705, 705),
                new Cone(600, 652.5f),
                new Cone(652.5f, 652.5f),
                new Cone(705, 652.5f),
                new Cone(600, 540),
                new Cone(705, 540),
                new Cone(600, 480),
                new Cone(705, 480),
                new Cone(600, 420),
                new Cone(705, 420),
                new Cone(600, 360),
                new Cone(705, 360)
        ));
    }

    private void dropPreloads() {
        preloads.addAll(Arrays.asList(
                new Cone(75, 525),
                new Cone(525, 75),
                new Cone(195, 645),
                new Cone(645, 195)
        ));
        onField.addAll(preloads);
    }

    private void dropRedDriverLoads() {
        redDriverLoads.addAll(Arrays.asList(
                new Cone(-100, -150),
                new Cone(-60, 240),
                new Cone(-90, 256),
                new Cone(-60, 272),
                new Cone(-90, 288),
                new Cone(-60, 304),
                new Cone(-90, 320),
                new Cone(-60, 336),
                new Cone(-90, 352),
                new Cone(-60, 368),
                new Cone(-90, 384),
                new Cone(-60, 400)
        ));
    }

    private void dropBlueDriverLoads() {
        blueDriverLoads.addAll(Arrays.asList(
                new Cone(-150, -100),
                new Cone(240, -60),
                new Cone(256, -90),
                new Cone(272, -60),
                new Cone(288, -90),
                new Cone(304, -60),
                new Cone(320, -90),
                new Cone(336, -60),
                new Cone(352, -90),
                new Cone(368, -60),
                new Cone(384, -90),
                new Cone(400, -60)
        ));
    }

    private void dropBlueMogos() {
        mogos.addAll(Arrays.asList(
                new BlueMobileGoal(180, 67.5f),
                new BlueMobileGoal(240, 360),
                new BlueMobileGoal(360, 480),
                new BlueMobileGoal(652.5f, 540)
        ));
    }

    private void dropRedMogos() {
        mogos.addAll(Arrays.asList(
                new RedMobileGoal(67.5f, 180),
                new RedMobileGoal(360, 240),
                new RedMobileGoal(480, 360),
                new RedMobileGoal(540, 652.5f)
        ));
    }

    private void redLoader() {
        rLoad = new Loader(0, 300 - 13.75f, true);
    }

    private void blueLoader() {
        bLoad = new Loader(300 - 13.75f, 0, false);
        bLoad.getNode().setRotate(90);
    }

    private void redStat() {
        rStat = new StationaryGoal(240, 480, true);
    }

    private void blueStat() {
        bStat = new StationaryGoal(480, 240, false);
    }

    private void robot1() {
        Robot r = new Robot(50, 485, 90);
        r.setController(KeyControl.Defaults.DUAL_1.getKC());
        getRobots().add(r);
    }

    private void robot2() {
        Robot r = new Robot(675, 225, -90);
        r.setController(KeyControl.Defaults.DUAL_2.getKC());
        r.setRed(false);
        getRobots().add(r);
    }

    private void robot3() {
        Robot r = new Robot(225, 675, 180);
        r.setController(KeyControl.Defaults.QUAD_3.getKC());
        getRobots().add(r);
    }

    private void robot4() {
        Robot r = new Robot(485, 40, 0);
        r.setController(KeyControl.Defaults.QUAD_4.getKC());
        r.setRed(false);
        getRobots().add(r);
    }

    /**
     * Resets this field to its initial starting positions. Also clears
     * user-added objects, destacks all stacks, and preloads cones. Also brings
     * back all vanished objects.
     */
    public void reset() {
        try {
            stop();
            clearAdded();
            if (mode == null) {
                mode = ControlMode.FREE_PLAY;
            }
            setMode(mode);
            bStat.reset();
            rStat.reset();
            getRobots().forEach(Robot::reset);
            mogos.forEach(MobileGoal::reset);
            redDriverLoads.stream().peek(onField::remove).forEach(Cone::reset);
            blueDriverLoads.stream().peek(onField::remove).forEach(Cone::reset);
            onField.forEach(Cone::reset);
            load(getRobots().get(0));
            load(getRobots().get(1));
            List<Cone> c = new ArrayList<>(preloads);
            getRobots().forEach(r -> r.forceIntake(preloads.remove(0)));
            preloads.addAll(c);
        } catch (Exception ex) {
        }
    }

    /**
     * Returns the field that the given {@link Robot Robot} has been placed on.
     * This enables robot-field communication.
     *
     * @param r the robot on a field
     * @return the field the robot is on
     */
    public static final Field getOwner(Robot r) {
        List<Field> fields = FIELDS.stream().filter(f -> f.getRobots().contains(r)).collect(Collectors.toList());
        return fields.size() > 0 ? fields.get(0) : null;
    }

    /**
     * Returns the field that the given {@link Loader Loader} has been placed
     * on. This enables loader-field communication.
     *
     * @param l the loader on a field
     * @return the field the loader is on
     */
    public static final Field getOwner(Loader l) {
        List<Field> fields = FIELDS.stream().filter(f -> f.rLoad == l || f.bLoad == l).collect(Collectors.toList());
        return fields.size() > 0 ? fields.get(0) : null;
    }

    /**
     * Returns the field that the given {@link StationaryGoal Stationary Goal}
     * has been placed on. This enables stationary goal-field communication.
     *
     * @param sg the stationary goal on a field
     * @return the field the stationary goal is on
     */
    public static final Field getOwner(StationaryGoal sg) {
        List<Field> fields = FIELDS.stream().filter(f -> f.bStat == sg || f.rStat == sg).limit(1).collect(Collectors.toList());
        return fields.isEmpty() ? null : fields.get(0);
    }

    /**
     * Returns the field that the given {@link MobileGoal Mobile Goal} has been
     * placed on. This enables mobile goal-field communication.
     *
     * @param mg the mobile goal on a field
     * @return the field the mobile goal is on
     */
    public static final Field getOwner(MobileGoal mg) {
        List<Field> field = FIELDS.stream().filter(f -> f.mogos.contains(mg)).limit(1).collect(Collectors.toList());
        if (field.isEmpty()) {
            field = FIELDS.stream().filter(f -> f.getRobots().stream().anyMatch(r -> r.owner(mg))).limit(1).collect(Collectors.toList());
        }
        return field.isEmpty() ? null : field.get(0);
    }

    /**
     * Registers a {@link MobileGoal Mobile Goal}'s
     * {@link ScoreReport score report} with this field's scorer.
     *
     * @param mg the mobile goal to register
     */
    public final void register(MobileGoal mg) {
        scores.registerReport(mg.getReporter());
    }

    /**
     * Registers a {@link StationaryGoal Stationary Goal}'s
     * {@link ScoreReport score report} with this field's scorer.
     *
     * @param sg the stationary goal to register
     */
    public final void register(StationaryGoal sg) {
        scores.registerReport(sg.getReporter());
    }

    /**
     * Returns a {@link MobileGoal mobile goal} near the given center point in
     * the direction of the pointing vector at about the distance of the
     * pointing vector.
     *
     * @param center the tail point of the pointing vector. This can be viewed
     * as a translation of the vector from the origin.
     * @param pointingVector the object representing the approximate distance
     * and direction relative to the center point in which to look for a mobile
     * goal.
     * @param red if the robot looking for a mobile goal is red. This determines
     * if during driver control a mobile goal is eligible to be picked up.
     * @return a mobile goal, if one is nearby the indicated point. If not, this
     * method returns null.
     */
    public MobileGoal huntMogo(Point2D center, Point2D pointingVector, boolean red) {
        return mogos.stream()
                .filter(m -> (mode != ControlMode.DRIVER_CONTROL && mode != ControlMode.AUTON) || m.isRed() == red)
                .filter(m -> !m.isVanished())
                .filter(m -> {
                    Point2D realVector = new Point2D(m.getCenterX(), m.getCenterY()).subtract(center);
                    return Math.abs(realVector.getX() - pointingVector.getX()) < 15 && Math.abs(realVector.getY() - pointingVector.getY()) < 15;
                }).findAny().orElse(null);
    }

    /**
     * Returns a {@link Cone cone} near the given center point in the direction
     * of the pointing vector at about the distance of the pointing vector.
     *
     * @param center the tail point of the pointing vector. This can be viewed
     * as a translation of the vector from the origin.
     * @param pointingVector the object representing the approximate distance
     * and direction relative to the center point in which to look for a cone.
     * @return a cone, if one is nearby the indicated point. If not, this method
     * returns null.
     */
    public Cone huntCone(Point2D center, Point2D pointingVector) {
        List<Cone> possible = onField.stream()
                .filter(c -> !c.isVanished())
                .filter(c -> {
                    Point2D realVector = new Point2D(c.getCenterX(), c.getCenterY()).subtract(center);
                    return Math.abs(realVector.getX() - pointingVector.getX()) < 15 && Math.abs(realVector.getY() - pointingVector.getY()) < 15;
                }).limit(1).collect(Collectors.toList());
        return possible.size() > 0 ? possible.get(0) : null;
    }

    /**
     * Returns a {@link StationaryGoal stationary goal} near the given center
     * point in the direction of the pointing vector at about the distance of
     * the pointing vector.
     *
     * @param center the tail point of the pointing vector. This can be viewed
     * as a translation of the vector from the origin.
     * @param pointingVector the object representing the approximate distance
     * and direction relative to the center point in which to look for a
     * stationary goal.
     * @param red the color of the robot looking for a stationary goal. This
     * determines if the stationary goal being looked for is of the same team as
     * the robot.
     * @return a stationary goal, if one is nearby the indicated point. If not,
     * this method returns null.
     */
    public StationaryGoal huntStat(Point2D center, Point2D pointingVector, boolean red) {
        if (mode == ControlMode.AUTON || mode == ControlMode.DRIVER_CONTROL) {
            if (red) {
                return checkRedStat(center, pointingVector);
            } else {
                return checkBlueStat(center, pointingVector);
            }
        } else {
            StationaryGoal sg;
            if ((sg = checkRedStat(center, pointingVector)) == null) {
                sg = checkBlueStat(center, pointingVector);
            }
            return sg;
        }
    }

    private StationaryGoal checkRedStat(Point2D center, Point2D pointingVector) {
        if (Math.abs(240 - center.getX() - pointingVector.getX()) < 20 && Math.abs(480 - center.getY() - pointingVector.getY()) < 20) {
            return rStat;
        }
        return null;
    }

    private StationaryGoal checkBlueStat(Point2D center, Point2D pointingVector) {
        if (Math.abs(240 - center.getY() - pointingVector.getY()) < 20 && Math.abs(480 - center.getX() - pointingVector.getX()) < 20) {
            return bStat;
        }
        return null;
    }

    /**
     * Tries to load a {@link Cone cone} on the loader belonging to the alliance
     * that the given robot belongs to.
     *
     * @param r the robot denoting the alliance for which the cone must be
     * loaded on
     */
    public void load(Robot r) {
        if (r.isRed()) {
            Cone c = rLoad.load();
            if (c != null) {
                onField.add(c);
            }
        } else {
            Cone c = bLoad.load();
            if (c != null) {
                onField.add(c);
            }
        }
    }

    /**
     * Determines whether the given loader already has a {@link Cone cone}
     * stacked on it.
     *
     * @param l the given loader being checked for a cone
     * @return true if there is already a cone on this loader
     */
    public boolean hasCone(Loader l) {
        Point2D loadCenter = l.getCenter();
        return onField.stream().filter(c -> !c.isVanished()).anyMatch(c -> c.getCenterX() == loadCenter.getX() && c.getCenterY() == loadCenter.getY());
    }

    /**
     * Finds a driver load {@link Cone cone} of the specified alliance, if any
     * are still present.
     *
     * @param red the boolean denoting which alliance's driver loads are to be
     * siphoned off.
     * @return a driver load cone. If none are present, this method will return
     * null.
     */
    public Cone getLoadableCone(boolean red) {
        if (red) {
            List<Cone> cone = redDriverLoads.stream().filter(c -> !onField.contains(c)).limit(1).collect(Collectors.toList());
            return cone.size() > 0 ? cone.get(0) : null;
        } else {
            List<Cone> cone = blueDriverLoads.stream().filter(c -> !onField.contains(c)).limit(1).collect(Collectors.toList());
            return cone.size() > 0 ? cone.get(0) : null;
        }
    }

    private final ScoreAggregator scores = new ScoreAggregator();

    /**
     * Gets the internal {@link ScoreAggregator scorer} used by the injected
     * {@link ScoringBoxController}. This is mainly so {@link Robot robots} can
     * register their own private {@link MobileGoal mobile goals} for scoring
     * purposes.
     *
     * @return the Score Aggregator associated with this field
     */
    public ScoreAggregator getAggregator() {
        return scores;
    }

    private ScoringBoxController sbc;

    /**
     * Inserts the given {@link ScoringBoxController scoring box} into the
     * field. This allows the scoring box to be updated in real time. This
     * method must be called for real-time scoring.
     *
     * @param sbc the scoring box to insert
     */
    public void inject(ScoringBoxController sbc) {
        this.sbc = sbc;
        System.out.println(sbc);
        sbc.setAggregator(scores);
        time = sbc.getTime();
        setMode(ControlMode.AUTON);
        setMode(ControlMode.FREE_PLAY);
        reset();
        KeyBuffer.registerMulti(k -> play_pause(), KeyCode.SPACE, System.getProperty("os.name").toLowerCase()
                .contains("windows") ? KeyCode.CONTROL : KeyCode.META);
        System.out.println(System.getProperty("os.name"));
    }

    private DoubleProperty time;

    private ControlMode mode;

    /**
     * Sets the {@link ControlMode mode} of this field. Also updates the timer
     * with the mode's alloted time.
     *
     * @param cm the mode to set
     */
    public void setMode(ControlMode cm) {
        this.mode = cm;
        reregisterMode(cm);
        switchToDriver = false;
    }

    /**
     * Prepares the field for a match scenario.
     */
    public void preMatch() {
        reset();
        setMode(ControlMode.FREE_PLAY);
    }

    /**
     * Begins a match.
     * **************************************************************************
     * Matches are played out as follows:
     *
     * 1. Autonomous begins immediately. Driver controls are locked out, and
     * recorded autonomous routines are triggered. The timer counts down from 15
     * seconds.
     *
     * 2. Autonomous finishes. Robots are paused, driver controls are still
     * locked out. Autonomous winner is determined and viewable on the score
     * sheet.
     *
     * 3. After 5 seconds, the robots are resumed. Driver controls are back in
     * play.
     *
     * 4. When any robot begins moving, the timer starts again, with 105 seconds
     * on the clock.
     *
     * 5. Game finishes. Match is visible on the score sheet.
     */
    public void startMatch() {
        setMode(ControlMode.AUTON);
        switchToDriver = true;
    }

    private final Timeline timer = new Timeline();

    private boolean switchToDriver;

    private void lockout(ActionEvent e) {
        e.consume();
        if (mode == ControlMode.AUTON) {
            sbc.determineAutonWinner();
        }
        robots.forEach(r -> r.pause());
        if (switchToDriver) {
            setMode(ControlMode.DRIVER_CONTROL);
            robots.stream().peek(Robot::pause).forEach(Robot::prime);
            Start.PULSER.schedule(() -> robots.forEach(Robot::resume), 5000, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stops the field/match timer, and resets the time.
     */
    public void stop() {
        timer.stop();
        reregisterMode(mode);
    }

    private long lastPlay_Pause;

    private void play_pause() {
        if (lastPlay_Pause + 200 < System.currentTimeMillis()) {
            if (timer.getStatus() == Animation.Status.RUNNING) {
                pause();
            } else {
                play();
            }
            lastPlay_Pause = System.currentTimeMillis();
        }
    }

    /**
     * Begins counting down the field/match timer. Also resumes all robot
     * movement, if it has not been enabled already.
     */
    public void play() {
        robots.forEach(Robot::resume);
        timer.play();
        robots.forEach(Robot::deprime);
    }

    /**
     * Temporarily pauses gameplay. Also prevents robots from moving.
     */
    public void pause() {
        robots.forEach(Robot::pause);
        timer.pause();
    }

    private void reregisterMode(ControlMode cm) {
        timer.stop();
        timer.getKeyFrames().clear();
        if (time != null && cm != null) {
            time.set(cm.getTime());
        }
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(time.get()), this::lockout, new KeyValue(time, 0)));
        KeyBuffer.unlock();
        getRobots().stream().peek(Robot::resume).forEach(Robot::prime);
        if (cm == ControlMode.AUTON || cm == ControlMode.PROGRAMMING_SKILLS) {
            getRobots().stream().peek(Robot::eraseController).forEach(Robot::runProgram);
        } else {
            getRobots().forEach(Robot::driverControl);
        }
        if (cm == ControlMode.DRIVER_SKILLS || cm == ControlMode.PROGRAMMING_SKILLS) {
        }
    }

    /**
     * Closes this field. Cancels all tasks maintaining this field, and clears
     * the {@link Hitbox#COLLIDABLE hitbox list} maintaining collisions.
     */
    @Override
    public void close() {
        timer.stop();
        Hitbox.clear();
        pulseManager.stop();
    }

    /**
     * Creates a new {@link Cone cone} at the specified coordinates. This is
     * meant for user-induced cone placement.
     *
     * @param sceneX the scene x coordinate at which the cone should be placed
     * @param sceneY the scene y coordinate at which the cone should be placed
     * @return the newly generated cone
     */
    public Cone generateCone(float sceneX, float sceneY) {
        Point2D localCenter = center.sceneToLocal(sceneX, sceneY);
        Cone c = new Cone((float) localCenter.getX(), (float) localCenter.getY());
        added.add(c);
        center.getChildren().add(c.getNode());
        return c;
    }

    /**
     * Creates a new {@link MobileGoal mobile goal} at the specified
     * coordinates. This is meant for user-induced mobile goal placement.
     *
     * @param sceneX the scene x coordinate at which the mobile goal should be
     * placed
     * @param sceneY the scene y coordinate at which the mobile goal should be
     * placed
     * @param red the color of the mobile goal (true if red, false if blue)
     * @return the newly generated mobile goal
     */
    public MobileGoal generateMobileGoal(float sceneX, float sceneY, boolean red) {
        Point2D centerMogo = center.sceneToLocal(sceneX, sceneY);
        MobileGoal mg = red ? new RedMobileGoal((float) centerMogo.getX(), (float) centerMogo.getY()) : new BlueMobileGoal((float) centerMogo.getX(), (float) centerMogo.getY());
        added.add(mg);
        center.getChildren().add(mg.getNode());
        return mg;
    }

    /**
     * Clears all user-added objects from the field. This is mainly called by
     * the {@link Field#reset() reset} method. It removes all {@link Cone cones}
     * and {@link MobileGoal mobile goals} from the field.
     */
    public void clearAdded() {
        added.stream().peek(Mobile::reset).map(Mobile::getNode).forEach(center.getChildren()::remove);
    }

    /**
     * Gets the list of robots playing on this field.
     *
     * @return the robots
     */
    public List<Robot> getRobots() {
        return robots;
    }
}

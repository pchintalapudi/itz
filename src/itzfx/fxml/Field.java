/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import itzfx.Hitbox;
import itzfx.KeyBuffer;
import itzfx.KeyControl;
import itzfx.Robot;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class Field {

    private static final List<Field> FIELDS;

    static {
        FIELDS = new LinkedList<>();
    }

    @FXML
    private BorderPane field;
    @FXML
    private Pane center;

    private Future<?> scheduled;

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

    public Field() {
        robots = new LinkedList<>();
        onField = new LinkedList<>();
        redDriverLoads = new LinkedList<>();
        blueDriverLoads = new LinkedList<>();
        preloads = new LinkedList<>();
        mogos = new LinkedList<>();
    }

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
        scheduled = Start.PULSER.scheduleAtFixedRate(() -> {
            KeyBuffer.pulse();
            Hitbox.pulse();
            if (sbc != null) {
                sbc.pulse(true);
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
        Start.PULSER.schedule(this::reset, 750, TimeUnit.MILLISECONDS);
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
        onField.stream().map(c -> c.getNode()).forEach(center.getChildren()::add);
        redDriverLoads.stream().map(c -> c.getNode()).forEach(center.getChildren()::add);
        blueDriverLoads.stream().map(c -> c.getNode()).forEach(center.getChildren()::add);
    }

    private void dropMogos() {
        dropBlueMogos();
        dropRedMogos();
        mogos.stream().peek(this::register).map(m -> m.getNode()).forEach(center.getChildren()::add);
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
        robots.stream().peek(r -> r.registerMogos()).map(r -> r.getNode()).forEach(center.getChildren()::add);
    }

    @FXML
    private StackPane blue10;
    @FXML
    private StackPane red10;

    private void decorate10(Node check, boolean red) {
        for (double i = 2.5; i < 325; i += 10) {
            double a = 482.5 + i / Math.sqrt(2);
            double b = 2.5 + i / Math.sqrt(2);
            Hitbox h = new Hitbox(2.5, Hitbox.CollisionType.WEAK, check, Double.POSITIVE_INFINITY);
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
                new Cone(15, 67.5),
                new Cone(15, 120),
                new Cone(15, 180),
                new Cone(15, 240),
                new Cone(67.5, 15),
                new Cone(67.5, 67.5),
                new Cone(67.5, 120),
                new Cone(120, 15),
                new Cone(120, 67.5),
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
                new Cone(652.5, 600),
                new Cone(652.5, 705),
                new Cone(705, 600),
                new Cone(705, 705),
                new Cone(600, 652.5),
                new Cone(652.5, 652.5),
                new Cone(705, 652.5),
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
                new BlueMobileGoal(180, 67.5),
                new BlueMobileGoal(240, 360),
                new BlueMobileGoal(360, 480),
                new BlueMobileGoal(652.5, 540)
        ));
    }

    private void dropRedMogos() {
        mogos.addAll(Arrays.asList(
                new RedMobileGoal(67.5, 180),
                new RedMobileGoal(360, 240),
                new RedMobileGoal(480, 360),
                new RedMobileGoal(540, 652.5)
        ));
    }

    private void redLoader() {
        rLoad = new Loader(0, 300 - 13.75, true);
    }

    private void blueLoader() {
        bLoad = new Loader(300 - 13.75, 0, false);
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
        robots.add(r);
    }

    private void robot2() {
        Robot r = new Robot(675, 225, -90);
        r.setController(KeyControl.Defaults.DUAL_2.getKC());
        r.setRed(false);
        robots.add(r);
    }

    private void robot3() {
        Robot r = new Robot(225, 675, 180);
        r.setController(KeyControl.Defaults.QUAD_3.getKC());
        robots.add(r);
    }

    private void robot4() {
        Robot r = new Robot(485, 40, 0);
        r.setController(KeyControl.Defaults.QUAD_4.getKC());
        r.setRed(false);
        robots.add(r);
    }

    public void reset() {
        bStat.reset();
        rStat.reset();
        robots.forEach(r -> r.reset());
        mogos.forEach(m -> m.reset());
        redDriverLoads.stream().peek(onField::remove).forEach(c -> c.reset());
        blueDriverLoads.stream().peek(onField::remove).forEach(c -> c.reset());
        onField.forEach(c -> c.reset());
        load(robots.get(0));
        load(robots.get(1));
        List<Cone> c = new LinkedList<>(preloads);
        robots.forEach(r -> r.forceIntake(preloads.remove(0)));
        preloads.addAll(c);
    }

    public static final Field getOwner(Robot r) {
        List<Field> fields = FIELDS.stream().filter(f -> f.robots.contains(r)).collect(Collectors.toList());
        return fields.size() > 0 ? fields.get(0) : null;
    }

    public static final Field getOwner(Loader l) {
        List<Field> fields = FIELDS.stream().filter(f -> f.rLoad == l || f.bLoad == l).collect(Collectors.toList());
        return fields.size() > 0 ? fields.get(0) : null;
    }

    public static final Field getOwner(StationaryGoal sg) {
        List<Field> fields = FIELDS.stream().filter(f -> f.bStat == sg || f.rStat == sg).limit(1).collect(Collectors.toList());
        return fields.isEmpty() ? null : fields.get(0);
    }

    public static final Field getOwner(MobileGoal mg) {
        List<Field> field = FIELDS.stream().filter(f -> f.mogos.contains(mg)).limit(1).collect(Collectors.toList());
        if (field.isEmpty()) {
            field = FIELDS.stream().filter(f -> f.robots.stream().anyMatch(r -> r.owner(mg))).limit(1).collect(Collectors.toList());
        }
        return field.isEmpty() ? null : field.get(0);
    }

    public final void register(MobileGoal mg) {
        scores.registerReport(mg.getReporter());
    }

    public final void register(StationaryGoal sg) {
        scores.registerReport(sg.getReporter());
    }

    public MobileGoal huntMogo(Point2D center, Point2D pointingVector) {
        List<MobileGoal> possible = mogos.stream()
                .filter(m -> !m.isVanished())
                .filter(m -> {
                    Point2D realVector = new Point2D(m.getCenterX(), m.getCenterY()).subtract(center);
                    return Math.abs(realVector.getX() - pointingVector.getX()) < 15 && Math.abs(realVector.getY() - pointingVector.getY()) < 15;
                }).limit(1).collect(Collectors.toList());
        return possible.size() > 0 ? possible.get(0) : null;
    }

    public Cone huntCone(Point2D center, Point2D pointingVector) {
        List<Cone> possible = onField.stream()
                .filter(c -> !c.isVanished())
                .filter(c -> {
                    Point2D realVector = new Point2D(c.getCenterX(), c.getCenterY()).subtract(center);
                    return Math.abs(realVector.getX() - pointingVector.getX()) < 15 && Math.abs(realVector.getY() - pointingVector.getY()) < 15;
                }).limit(1).collect(Collectors.toList());
        return possible.size() > 0 ? possible.get(0) : null;
    }

    public StationaryGoal huntStat(Point2D center, Point2D pointingVector) {
        if (Math.abs(227.5 - center.getX() - pointingVector.getX()) < 15 && Math.abs(467.5 - center.getY() - pointingVector.getY()) < 15) {
            return rStat;
        } else if (Math.abs(227.5 - center.getY() - pointingVector.getY()) < 15 && Math.abs(467.5 - center.getX() - pointingVector.getX()) < 15) {
            return bStat;
        }
        return null;
    }

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

    public boolean hasCone(Loader l) {
        Point2D center = l.getCenter();
        return onField.stream().filter(c -> !c.isVanished()).anyMatch(c -> c.getCenterX() == center.getX() && c.getCenterY() == center.getY());
    }

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

    public ScoreAggregator getAggregator() {
        return scores;
    }

    private ScoringBoxController sbc;

    public void inject(ScoringBoxController sbc) {
        this.sbc = sbc;
        sbc.setAggregator(scores);
    }

    private DoubleProperty time;

    public void inject(DoubleProperty time) {
        this.time = time;
    }

    private ControlMode mode;

    private boolean clean = true;

    public void setMode(ControlMode cm) {
        clean = true;
        this.mode = cm;
        reregisterMode(cm);
    }

    private void reregisterMode(ControlMode cm) {
        timer.stop();
        timer.getKeyFrames().clear();
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(cm.getTime()), this::lockout, new KeyValue(time, 0)));
        time.set(cm.getTime());
    }

    private void lockout(ActionEvent e) {
        e.consume();
    }

    private final Timeline timer = new Timeline();

    public void play() {
        if (mode != ControlMode.FREE_PLAY) {
            if (!clean) {
                timer.play();
                clean = false;
            } else {
                reregisterMode(mode);
            }
        }
    }

    public void pause() {
        timer.pause();
    }

    public void stop() {
        timer.stop();
        clean = true;
    }

    public void close() {
        Hitbox.clear();
        scheduled.cancel(true);
    }
}

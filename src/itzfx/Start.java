/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx;

import com.sun.javafx.application.LauncherImpl;
import itzfx.fxml.FXMLController;
import itzfx.preload.Prestart;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Preloader.ProgressNotification;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public class Start extends Application {

    private FXMLController fxml;

    public Start() {
        instance = this;
    }

    //Because I can
//    public static final Unsafe UNSAFE;
//
//    static {
//        try {
//            java.lang.reflect.Field f = Unsafe.class.getDeclaredField("theUnsafe");
//            f.setAccessible(true);
//            UNSAFE = (Unsafe) f.get(null);
//        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
//            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
//            System.err.println(ex);
//            throw new RuntimeException();
//        }
//    }
    private Parent p;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        try {
            super.notifyPreloader(new ProgressNotification(0));
            FXMLLoader loader = new FXMLLoader(Start.class.getResource("/itzfx/fxml/FXML.fxml"));
            p = loader.load();
            p.getStylesheets().add("/itzfx/fxml/Resources.css");
            addZoomListeners(p);
            fxml = loader.getController();
            fxml.inject(this);
            Thread current = Thread.currentThread();
            super.notifyPreloader(new ProgressNotification(.3));
            PULSER.schedule(() -> LockSupport.unpark(current), 3, TimeUnit.SECONDS);
            LockSupport.park(this);
            super.notifyPreloader(new ProgressNotification(1));
            PULSER.schedule(() -> LockSupport.unpark(current), 500, TimeUnit.MILLISECONDS);
            LockSupport.park(this);
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void addZoomListeners(Node n) {
        final double minZoomSoft, minZoomHard;
        final double maxZoomSoft, maxZoomHard;
        minZoomHard = 0.5;
        minZoomSoft = 0.71;
        maxZoomSoft = 1.41;
        maxZoomHard = 2;
        n.scaleYProperty().bindBidirectional(n.scaleXProperty());
        n.setOnZoom(ze -> {
            if (n.getScaleX() < maxZoomHard && n.getScaleX() > minZoomHard) {
                n.setScaleX(ze.getZoomFactor() * n.getScaleX());
            }
        });
        n.setOnZoomFinished(ze -> {
            if (n.getScaleX() < minZoomSoft) {
                restoreZoom(minZoomSoft, n.scaleXProperty());
            }
            if (n.getScaleX() > maxZoomSoft) {
                restoreZoom(maxZoomSoft, n.scaleXProperty());
            }
        });
        addDoubleTapToRestoreZoom(n);
        cancelSingleFingerScroll(n);
    }

    private static void restoreZoom(double restorationValue, DoubleProperty scale) {
        Timeline tl = new Timeline();
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(200), e -> tl.stop(), new KeyValue(scale, restorationValue)));
        tl.play();
    }

    private static void cancelSingleFingerScroll(Node n) {
        n.setOnScroll(se -> {
            if (se.getTouchCount() == 1) {
                se.consume();
            }
        });
    }

    private static void addDoubleTapToRestoreZoom(Node n) {
        AtomicInteger touchCount = new AtomicInteger();
        AtomicBoolean held = new AtomicBoolean();
        n.setOnTouchPressed(te -> {
            held.set(true);
        });
        n.setOnTouchReleased(te -> {
            if (held.getAndSet(false)) {
                if (touchCount.incrementAndGet() == 2) {
                    restoreZoom(1, n.scaleXProperty());
                }
                PULSER.schedule(touchCount::decrementAndGet, 150, TimeUnit.MILLISECONDS);
            }
            te.consume();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("In The Zone (ITZ)");
        primaryStage.getIcons().add(new Image(Start.class.getResourceAsStream("Images/icon.png")));
        AnchorPane.setLeftAnchor(p, 0d);
        AnchorPane.setTopAnchor(p, 0d);
        AnchorPane.setRightAnchor(p, 0d);
        AnchorPane.setBottomAnchor(p, 0d);
        AnchorPane windowScale = new AnchorPane(new Group(p));
        StackPane root = new StackPane(windowScale);
        NumberBinding maxScale = Bindings.min(root.widthProperty().divide(1500),
                                      root.heightProperty().divide(1000));
        windowScale.scaleXProperty().bind(maxScale);
        windowScale.scaleYProperty().bind(maxScale);
        Scene scene = new Scene(root, 1500, 1000);
        KeyBuffer.initialize(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println(Runtime.getRuntime().availableProcessors() + " " + Thread.activeCount());
    }

    /**
     * Program start point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(Start.class, Prestart.class, args);
    }

    /**
     * Signal boolean to check if this program has shut down or not.
     */
    public static boolean SHUTDOWN;

    /**
     * Main thread pool for scheduling tasks.
     */
    public static final ScheduledExecutorService PULSER = Executors.newSingleThreadScheduledExecutor();

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        PULSER.shutdownNow();
        SHUTDOWN = true;
        fxml.close();
    }

    private static Start instance;

    public static void navigate(String url) {
        instance.getHostServices().showDocument(url);
    }
}

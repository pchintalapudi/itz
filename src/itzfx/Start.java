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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Preloader.ProgressNotification;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import sun.misc.Unsafe;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public class Start extends Application {

    private FXMLController fxml;

    //Because I can
    public static final Unsafe UNSAFE;

    public static final List<Unsafe> UNSAFES = new LinkedList<>();

    static {
        try {
            java.lang.reflect.Field f = LockSupport.class.getDeclaredField("UNSAFE");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex);
            throw new RuntimeException();
        }
    }
    private Parent p;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        try {
            for (int i = 0; i < 200; i++) {
                try {
                    UNSAFES.add((Unsafe) UNSAFE.allocateInstance(Unsafe.class));
                } catch (InstantiationException ex) {
                    Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            super.notifyPreloader(new ProgressNotification(0));
            FXMLLoader loader = new FXMLLoader(Start.class.getResource("/itzfx/fxml/FXML.fxml"));
            p = loader.load();
            p.getStylesheets().add("/itzfx/fxml/Resources.css");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("In The Zone (ITZ)");
        Scene scene = new Scene(new StackPane(p, generateShift()));
        KeyBuffer.initialize(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Program start point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(Start.class, Prestart.class, args);
    }

    public static final Rectangle generateShift() {
        Rectangle warmShift = new Rectangle(1500, 1000, Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 20 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 7 ? new Color(1, 0.5, 0, 0.1) : Color.TRANSPARENT);
        warmShift.setMouseTransparent(true);
        return warmShift;
    }

    /**
     * Signal boolean to check if this program has shut down or not.
     */
    public static boolean SHUTDOWN;

    /**
     * Main thread pool for scheduling tasks.
     */
    public static final ScheduledExecutorService PULSER = Executors.newScheduledThreadPool(3);

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        PULSER.shutdownNow();
        SHUTDOWN = true;
        fxml.close();
    }
}

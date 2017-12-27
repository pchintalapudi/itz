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
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Preloader.ProgressNotification;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Prem Chintalapudi 5776E
 */
public class Start extends Application {

    private FXMLController fxml;

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

    @Override
    public void init() {
        try {
            super.notifyPreloader(new ProgressNotification(0));
            FXMLLoader loader = new FXMLLoader(Start.class.getResource("/itzfx/fxml/FXML.fxml"));
            p = loader.load();
            p.getStylesheets().add("/itzfx/fxml/Resources.css");
            fxml = loader.getController();
            fxml.inject(this);
            Thread current = Thread.currentThread();
            super.notifyPreloader(new ProgressNotification(.3));
            PULSER.schedule(() -> LockSupport.unpark(current), 3, TimeUnit.SECONDS);
            LockSupport.park();
            super.notifyPreloader(new ProgressNotification(1));
            PULSER.schedule(() -> LockSupport.unpark(current), 500, TimeUnit.MILLISECONDS);
            LockSupport.park();
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(p);
        KeyBuffer.initialize(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Start.class, Prestart.class, args);
    }

    public static boolean SHUTDOWN;

    public static final ScheduledExecutorService PULSER = Executors.newScheduledThreadPool(3);

    @Override
    public void stop() {
        PULSER.shutdownNow();
        SHUTDOWN = true;
        fxml.close();
    }
}

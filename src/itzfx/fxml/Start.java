/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import itzfx.KeyBuffer;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author prem
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
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(Start.class.getClassLoader().getResource("itzfx/fxml/FXML.fxml"));
            Parent p = loader.load();
            p.getStylesheets().add("itzfx/fxml/Resources.css");
            Scene scene = new Scene(p);
            fxml = loader.getController();
            fxml.inject(this);
            KeyBuffer.initialize(scene);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch(Start.class, args);
    }

    public static boolean SHUTDOWN;

    public static final ScheduledExecutorService PULSER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void stop() {
        PULSER.shutdownNow();
        SHUTDOWN = true;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial;

import itzfx.KeyBuffer;
import itzfx.Start;
import itzfx.tutorial.scenes.Scene1;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author prem
 */
public class Test extends Application {

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(Test.class.getResource("scenes/Scene1.fxml"));
        try {
            AnchorPane p = loader.load();
            Scene1 s1 = loader.getController();
            primaryStage.setScene(new Scene(p));
            s1.inject(new TutorialRobot(90, 180, 90));
            primaryStage.show();
            Start.PULSER.scheduleAtFixedRate(KeyBuffer::pulse, 0, 10, TimeUnit.MILLISECONDS);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stop() {
        Start.PULSER.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

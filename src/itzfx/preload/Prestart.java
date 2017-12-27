/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.preload;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Preloader;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Simple Preloader Using the ProgressBar Control
 *
 * @author Prem Chintalapudi 5776E
 */
public class Prestart extends Preloader {

    private ProgressBar bar;
    private Stage stage;

    private Scene createPreloaderScene() {
        bar = new ProgressBar(0);
        BorderPane p = new BorderPane();
        Label loading = new Label("Loading 'In The Zone'...");
        loading.setFont(Font.font(24));
        p.setTop(new StackPane(loading));
        p.setCenter(bar);
        bar.setPrefWidth(200);
        p.setPadding(new Insets(25));
        Timeline tl = new Timeline();
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(4800), e -> tl.stop(), new KeyValue(bar.progressProperty(), 1)));
        tl.play();
        return new Scene(p, 300, 150);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setScene(createPreloaderScene());
        stage.setOnHidden(w -> {
            if (suddenClose) {
                System.exit(0);
            }
        });
        stage.show();
    }

    private boolean suddenClose;

    @Override
    public void handleStateChangeNotification(StateChangeNotification scn) {
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
            suddenClose = false;
            stage.hide();
        }
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        //Nothing really happens
    }
}

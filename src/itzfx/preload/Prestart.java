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
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
        Text loading = new Text("Loading 'In The Zone'...");
        loading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        loading.setFill(Color.BLACK);
        HBox parent = new HBox();
        parent.setSpacing(50);
        parent.setStyle("-fx-background-color:#ffffff");
        VBox left = new VBox();
        left.setStyle("-fx-background-color:#ffffff");
        left.setSpacing(20);
        left.setTranslateY(50);
        parent.getChildren().add(left);
        parent.getChildren().add(new ImageView(new Image(Prestart.class.getResourceAsStream("/itzfx/Images/VEXEDR-stacked-red-transp-1000px.png"), 200, 200, true, true)));
        left.getChildren().add(new StackPane(loading));
        left.getChildren().add(new StackPane(bar));
        bar.setPrefWidth(200);
        parent.setPadding(new Insets(25));
        Timeline tl = new Timeline();
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(4800), e -> tl.stop(), new KeyValue(bar.progressProperty(), 1)));
        tl.play();
        return new Scene(parent, 650, 200);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setScene(createPreloaderScene());
        stage.setOnHidden(w -> {
            if (suddenClose) {
                System.exit(0);
            }
        });
        stage.show();
    }

    private boolean suddenClose = true;

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

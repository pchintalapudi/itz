/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import itzfx.fxml.build.RobotBuilder;
import itzfx.Start;
import itzfx.ControlMode;
import itzfx.Hitbox;
import itzfx.Robot;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class FXMLController {

    @FXML
    private BorderPane root;

    @FXML
    private VBox right;

    private Field field;

    private ScoringBoxController sbc;

    @FXML
    private ImageView logo;

    private Start ignition;

    @FXML
    private void initialize() {
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        field = (Field) ((Pane) ((BorderPane) ((Pane) root.getCenter()).getChildren().get(0)).getCenter()).getChildren().get(0).getUserData();
        Hitbox.VISIBLE.bind(showHitboxes.selectedProperty());
        sbc = (ScoringBoxController) right.getChildren().get(0).getUserData();
        right.setSpacing(25);
        field.inject(sbc);
        field.inject(sbc.getInjection());
        Button tempPlay = new Button("Play");
        root.setBottom(tempPlay);
        tempPlay.setOnAction((ActionEvent e) -> field.play());
    }

    @FXML
    private void reset() {
        field.reset();
    }

    @FXML
    private void close() {
        field.close();
        root.getScene().getWindow().hide();
    }

    @FXML
    private void screenshot() {
        copy(takeScreenshot(root));
    }

    public void inject(Start ignition) {
        this.ignition = ignition;
    }

    @FXML
    private void restart() {
        Stage primaryStage = (Stage) root.getScene().getWindow();
        close();
        ignition.start(primaryStage);
    }

    @FXML
    private void dc() {
        field.setMode(ControlMode.DRIVER_CONTROL);
    }

    @FXML
    private void auton() {
        field.setMode(ControlMode.AUTON);
    }

    @FXML
    private void ds() {
        field.setMode(ControlMode.DRIVER_SKILLS);
    }

    @FXML
    private void ps() {
        field.setMode(ControlMode.PROGRAMMING_SKILLS);
    }

    @FXML
    private void fp() {
        field.setMode(ControlMode.FREE_PLAY);
    }

    @FXML
    private void buildR1() {
        build(0);
    }

    @FXML
    private void buildR2() {
        build(1);
    }

    @FXML
    private void buildR3() {
        build(2);
    }

    @FXML
    private void buildR4() {
        build(3);
    }

    private void build(int index) {
        Robot r = getRobot(index);
        FXMLLoader loader = new FXMLLoader(FXMLController.class.getResource("/itzfx/fxml/build/RobotBuilder.fxml"));
        try {
            TabPane p = loader.load();
            RobotBuilder rb = loader.getController();
            Alert show = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.APPLY);
            show.getDialogPane().setContent(p);
            show.getDialogPane().setPrefHeight(500);
            show.showAndWait().filter(bt -> bt.getButtonData().equals(ButtonData.APPLY)).ifPresent(bt -> {
                rb.submit();
                rb.fillRobot(r);
            });
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Robot getRobot(int index) {
        return field.getRobots().get(index);
    }

    @FXML
    private CheckMenuItem showHitboxes;

    public static WritableImage takeScreenshot(Node... nodes) {
        HBox container = new HBox();
        Scene shotScene = new Scene(container);
        ImageView[] images = Arrays.stream(nodes).map(node -> {
            return node.snapshot(null, null);
        }).map(wi -> {
            return new ImageView(wi);
        }).toArray(ImageView[]::new);
        container.getChildren().addAll(images);
        return shotScene.snapshot(null);
    }

    public static void copy(Image i) {
        ClipboardContent cc = new ClipboardContent();
        cc.putImage(i);
        Clipboard.getSystemClipboard().setContent(cc);
    }
}

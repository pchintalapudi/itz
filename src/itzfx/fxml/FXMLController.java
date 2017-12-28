/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import itzfx.Start;
import itzfx.ControlMode;
import itzfx.Hitbox;
import itzfx.Robot;
import itzfx.data.FileUI;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
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
 * @author Prem Chintalapudi 5776E
 */
public class FXMLController {

    @FXML
    private BorderPane root;

    @FXML
    private VBox right;

    private Field field;

    private ScoringBoxController sbc;

    private Start ignition;

    @FXML
    private Menu robotMenu;

    @FXML
    private void initialize() {
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        field = (Field) ((Pane) ((BorderPane) ((Pane) root.getCenter()).getChildren().get(0)).getCenter()).getChildren().get(0).getUserData();
        Hitbox.VISIBLE.bind(showHitboxes.selectedProperty());
        sbc = (ScoringBoxController) right.getChildren().get(0).getUserData();
        right.setSpacing(25);
        field.inject(sbc);
        field.getRobots().forEach(r -> r.recordingProperty().addListener((o, b, s) -> {
            if (!s) {
                FileUI.saveRerun(r, root.getScene().getWindow());
            }
        }));
        List<Robot> robots = field.getRobots();
        for (int i = 0; i < robots.size(); i++) {
            RobotMenu controller = new RobotMenu(robots.get(i), "Robot " + (i + 1));
            FXMLLoader loader = new FXMLLoader(FXMLController.class.getResource("RobotMenu.fxml"));
            loader.setController(controller);
            try {
                MenuBar mb = loader.load();
                robotMenu.getItems().add(mb.getMenus().get(0));
            } catch (IOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void reset() {
        field.reset();
    }

    /**
     *
     */
    @FXML
    public void close() {
        field.close();
        if (root.getScene() != null && root.getScene().getWindow() != null) {
            root.getScene().getWindow().hide();
        }
    }

    @FXML
    private void screenshot() {
        copy(takeScreenshot(root));
    }

    /**
     *
     * @param ignition
     */
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
    private void prematch() {
        field.preMatch();
    }

    @FXML
    private void match() {
        field.startMatch();
    }

    @FXML
    private CheckMenuItem showHitboxes;

    /**
     *
     * @param nodes
     * @return
     */
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

    /**
     *
     * @param i
     */
    public static void copy(Image i) {
        ClipboardContent cc = new ClipboardContent();
        cc.putImage(i);
        Clipboard.getSystemClipboard().setContent(cc);
    }

    /**
     *
     * @param text
     */
    public static void copy(String text) {
        ClipboardContent cc = new ClipboardContent();
        cc.putString(text);
        Clipboard.getSystemClipboard().setContent(cc);
    }
}

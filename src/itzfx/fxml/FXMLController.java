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
import itzfx.preload.Prestart;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * FXML Controller class. Controls file "FXML.fxml". Handles most of the methods
 * located in the top {@link MenuBar}. The Robot {@link Menu} is filled with
 * repetitive calls to {@link RobotMenu}.
 *
 * @author Prem Chintalapudi 5776E
 */
public class FXMLController implements AutoCloseable {

    @FXML
    private BorderPane root;

    @FXML
    private Parent right;

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
        sbc = (ScoringBoxController) right.getChildrenUnmodifiable().get(0).getUserData();
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
    
    public Field getField() {
        return field;
    }

    @FXML
    private void reset() {
        field.reset();
    }

    /**
     * Closes this FXMLController and associated window. Also calls the
     * {@link Field#close()} method of the embedded field.
     */
    @FXML
    @Override
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
     * Allows a {@link Start} that initializes this object to insert itself for
     * the purposes of restarting this program, on user request.
     *
     * @param ignition the Start that began this program
     */
    public void inject(Start ignition) {
        this.ignition = ignition;
    }

    @FXML
    private void restart() {
        Stage primaryStage = (Stage) root.getScene().getWindow();
        close();
        new Prestart().start(new Stage());
        ignition.init();
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
    
    @FXML
    private void about() {
        FXMLLoader loader = new FXMLLoader(FXMLController.class.getResource("About.fxml"));
        try {
            TextFlow tf = loader.load();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("About this simulation");
            alert.setTitle("About");
            alert.getDialogPane().setContent(tf);
            alert.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Takes a screenshot of the given nodes. Each node has its picture taken in
     * its own scene. Then, the pictures are accumulated into an {@link HBox} in
     * its own scene before a snapshot is taken of the entire scene.
     *
     * @param nodes the nodes to include in the screenshot
     * @return a {@link WritableImage} that holds the screenshot image
     */
    public static WritableImage takeScreenshot(Node... nodes) {
        HBox container = new HBox();
        Scene shotScene = new Scene(container);
        Arrays.stream(nodes).map(node -> node.snapshot(null, null)).map(ImageView::new)
                .collect(container::getChildren, ObservableList<Node>::add, ObservableList<Node>::addAll);
        return shotScene.snapshot(null);
    }

    /**
     * Copies an image into the system clipboard. This is generally used in
     * combination with
     * {@link FXMLController#takeScreenshot(javafx.scene.Node...)} to copy
     * images of the program.
     *
     * @param i the image to copy into the clipboard
     */
    public static void copy(Image i) {
        ClipboardContent cc = new ClipboardContent();
        cc.putImage(i);
        Clipboard.getSystemClipboard().setContent(cc);
    }

    /**
     * Copies a text string into the system clipboard. This is generally used to
     * copy autonomous code into the system clipboard.
     *
     * @param text the text to copy
     */
    public static void copy(String text) {
        ClipboardContent cc = new ClipboardContent();
        cc.putString(text);
        Clipboard.getSystemClipboard().setContent(cc);
    }
}

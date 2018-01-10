/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial.scenes;

import itzfx.KeyControl;
import itzfx.tutorial.TutorialRobot;
import itzfx.tutorial.TutorialStep;
import itzfx.tutorial.Tutorials;
import java.util.List;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class Scene1 implements TutorialScene {

    @FXML
    private AnchorPane root;

    private TutorialRobot tr;

    private List<EventHandler<KeyEvent>> handlers;

    public void inject(TutorialRobot tr) {
        this.tr = tr;
        TutorialStep.setControllers(KeyControl.Defaults.SINGLE.getKC());
        tr.setController(TutorialStep.STEP1.getController());
        handlers = TutorialRobot.getActionList(tr);
        Tutorials.registerKeyListeners(root.getScene(), handlers);
        root.getChildren().add(tr.getNode());
    }

    @Override
    public AnchorPane getRoot() {
        return root;
    }

    @Override
    public void nextScene() {
        Tutorials.unregisterKeyListeners(root.getScene(), handlers);
        Scene2 s2 = new Scene2(tr);
        root.getScene().setRoot(s2.getRoot());
    }
}

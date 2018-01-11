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
import javafx.fxml.FXML;
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

    public void inject(TutorialRobot tr) {
        this.tr = tr;
        TutorialStep.setControllers(KeyControl.Defaults.SINGLE.getKC());
        tr.setController(TutorialStep.STEP1.getController());
        root.getChildren().add(tr.getNode());
        TutorialScene.setFocusedScene(this);
    }

    @Override
    public void init() {
    }

    @Override
    public void nextScene() {
        Scene2 s2 = new Scene2(tr);
        root.getScene().setRoot(Tutorials.load("scenes/Scene2.fxml", s2));
        s2.init();
        TutorialScene.setFocusedScene(s2);
    }
}

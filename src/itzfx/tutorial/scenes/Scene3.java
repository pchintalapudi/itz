/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial.scenes;

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
public class Scene3 implements TutorialScene {

    @FXML
    private AnchorPane root;

    private final TutorialRobot tr;

    public Scene3(TutorialRobot tr) {
        this.tr = tr;
        tr.setController(TutorialStep.STEP3.getController());
        tr.setCenter(180, 180);
        tr.getNode().setRotate(90);
    }

    @Override
    public void nextScene() {
        Scene4 s4 = new Scene4(tr);
        Tutorials.load("scenes/Scene4.fxml", s4);
        s4.init();
        TutorialScene.setFocusedScene(s4);
    }

    @Override
    public void init() {
        root.getChildren().add(tr.getNode());
    }
}

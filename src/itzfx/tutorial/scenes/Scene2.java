/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial.scenes;

import itzfx.tutorial.TutorialRobot;
import itzfx.tutorial.TutorialStep;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author prem
 */
public class Scene2 implements TutorialScene {

    @FXML
    private AnchorPane root;

    private final TutorialRobot tr;

    public Scene2(TutorialRobot tr) {
        this.tr = tr;
        tr.setController(TutorialStep.STEP1.getController());
        root.getChildren().add(tr.getNode());
    }
    @Override
    public AnchorPane getRoot() {
        return root;
    }

    @Override
    public void nextScene() {
    }
}

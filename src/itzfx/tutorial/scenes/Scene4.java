/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.tutorial.scenes;

import itzfx.fxml.GameObjects.MobileGoal;
import itzfx.fxml.GameObjects.RedMobileGoal;
import itzfx.rerun.Command;
import itzfx.tutorial.TutorialRobot;
import itzfx.tutorial.TutorialStep;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class Scene4 implements TutorialScene {

    @FXML
    private AnchorPane root;

    private final TutorialRobot tr;

    private final MobileGoal mogo;

    public Scene4(TutorialRobot tr) {
        this.tr = tr;
        tr.setCenter(180, 180);
        tr.getNode().setRotate(90);
        tr.setController(TutorialStep.STEP4.getController());
        mogo = new RedMobileGoal(180, 360);
    }

    @Override
    public void init() {
        root.getChildren().add(tr.getNode());
        root.getChildren().add(mogo.getNode());
    }

    @Override
    public void nextScene() {
    }

    @Override
    public void interact(Command command) {
        if (command == Command.MOGO) {
            if (!mogo.isVanished()) {
                if (tr.getCenter().distance(mogo.getCenter()) < 80 && tr.getCenter().distance(mogo.getCenter()) > 60) {
                    tr.mogoIntake(mogo);
                }
            } else {
                tr.mogoOuttake();
            }
        }
    }
}

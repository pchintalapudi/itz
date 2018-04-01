/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.tabs;

import itzfx.ControlMode;
import itzfx.MatchState;
import itzfx.fxml.Field;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class MatchController {

    @FXML
    private Button resetButton;
    @FXML
    private Button preAutonButton;
    @FXML
    private ToggleButton playingButton;
    @FXML
    private Button beginButton;

    @FXML
    private void initialize() {
        playingButton.selectedProperty().addListener((o, b, s) -> {
            if (s) {
                f.play();
            } else {
                f.pause();
            }
        });
    }

    @FXML
    private void reset(ActionEvent event) {
        event.consume();
        f.reset();
    }

    @FXML
    private void preAuton(ActionEvent event) {
        event.consume();
        f.preMatch();
    }

    @FXML
    private void auton(ActionEvent event) {
        event.consume();
        f.setMode(ControlMode.AUTON);
    }

    @FXML
    private void dc(ActionEvent event) {
        event.consume();
        f.setMode(ControlMode.DRIVER_CONTROL);
    }

    @FXML
    private void begin(ActionEvent event) {
        event.consume();
        f.startMatch();
    }

    private Field f;

    public void insertField(Field f) {
        this.f = f;
        f.onMatchStateChanged((o, b, s) -> {
            switch (s) {
                case NONE:
                    preAutonButton.setDisable(false);
                    playingButton.selectedProperty().set(false);
                    playingButton.setDisable(true);
                    beginButton.setDisable(true);
                    break;
                case PREPPED:
                    preAutonButton.setDisable(true);
                    beginButton.setDisable(false);
                    break;
                case AUTON:
                    preAutonButton.setDisable(true);
                    beginButton.setDisable(true);
                    playingButton.selectedProperty().set(true);
                    playingButton.setDisable(false);
                    break;
            }
        });
    }
}

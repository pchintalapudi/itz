/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.build;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class. Controls "RobotConfig.fxml". Creates and maintains
 * images/options in the "Robot Configuration" tab of the robot builder.
 *
 * @author Prem Chintalapudi 5776E
 */
public class RobotConfig {

    @FXML
    private Pane root;

    @FXML
    private TextField robotSpeed;
    @FXML
    private TextField robotAutostackTime;
    @FXML
    private TextField robotStatTime;

    @FXML
    private void initialize() {
        robotSpeed.setTextFormatter(new DecimalFormatter());
        robotAutostackTime.setTextFormatter(new DecimalFormatter());
        robotStatTime.setTextFormatter(new DecimalFormatter());
        root.setUserData(this);
    }

    /**
     * Gets the entered speed of the robot. This value will be null if the user
     * did not enter anything.
     *
     * @return the user-entered value, or null
     */
    public Double getSpeed() {
        if (robotSpeed.getText().isEmpty()) {
            return null;
        } else {
            return Double.parseDouble(robotSpeed.getText());
        }
    }

    /**
     * Gets the entered autostack time of the robot. This value will be null if
     * the user did not enter anything.
     *
     * @return the user-entered value, or null
     */
    public Double getAutostackTime() {
        if (this.robotAutostackTime.getText().isEmpty()) {
            return null;
        } else {
            return Double.parseDouble(this.robotAutostackTime.getText());
        }
    }

    /**
     * Gets the entered stationary stack time of the robot. This value will be
     * null if the user did not enter anything.
     *
     * @return the user-entered value, or null
     */
    public Double getStatTime() {
        if (this.robotStatTime.getText().isEmpty()) {
            return null;
        } else {
            return Double.parseDouble(this.robotAutostackTime.getText());
        }
    }

    private static class DecimalFormatter extends TextFormatter<Double> {

        public DecimalFormatter() {
            super(c -> {
                c.setText(c.getText().replaceAll("[^0-9.]", ""));
                return c;
            });
        }
    }
}

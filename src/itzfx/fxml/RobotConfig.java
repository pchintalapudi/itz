/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class RobotConfig {

    @FXML
    private Pane root;

    @FXML
    private TextField robotSpeed;
    @FXML
    private TextField robotMogoIntakeTime;
    @FXML
    private TextField robotAutostackTime;
    @FXML
    private TextField robotStatTime;

    @FXML
    public void initialize() {
        robotSpeed.setTextFormatter(new DecimalFormatter());
        robotMogoIntakeTime.setTextFormatter(new DecimalFormatter());
        robotAutostackTime.setTextFormatter(new DecimalFormatter());
        robotStatTime.setTextFormatter(new DecimalFormatter());
        root.setUserData(this);
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

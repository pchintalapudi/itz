/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.build;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Prem Chintalapudi 5776E
 */
public class MogoConfig {

    @FXML
    private Pane root;

    @FXML
    private CheckBox front;

    @FXML
    private TextField robotInTime;

    /**
     *
     */
    @FXML
    public void initialize() {
        robotInTime.setTextFormatter(new DecimalFormatter());
        root.setUserData(this);
    }

    /**
     *
     * @return
     */
    public Double getMogoTime() {
        if (robotInTime.getText().isEmpty()) {
            return null;
        } else {
            return Double.parseDouble(robotInTime.getText());
        }
    }

    /**
     *
     * @return
     */
    public Boolean isFrontMogo() {
        return front.isIndeterminate() ? null : front.isSelected();
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

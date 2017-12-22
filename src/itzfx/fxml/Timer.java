/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import java.math.BigDecimal;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class Timer {

    @FXML
    private AnchorPane root;
    
    @FXML
    private Text timer;

    private final DoubleProperty timeRemaining;

    public Timer() {
        timeRemaining = new SimpleDoubleProperty();
    }

    @FXML
    private void initialize() {
        root.setUserData(this);
        timer.textProperty().bind(Bindings.createStringBinding(this::binding, timeRemaining));
        timeRemaining.set(105);
    }

    private String binding() {
        int minutes = timeRemaining.intValue() / 60;
        double seconds = timeRemaining.doubleValue() % 60;
        int decaSeconds = (int) (seconds) / 10;
        double singles = seconds % 10;
        int secs = (int) singles;
        int dsecs = new BigDecimal(String.valueOf(singles % 1)).movePointRight(0).toBigInteger().intValue();
        StringBuilder s = new StringBuilder(String.valueOf(minutes));
        s.append(":").append(decaSeconds).append(secs);
        s.append(".").append(dsecs);
        for (int i = s.length(); i > -1; i--) {
            s.insert(i, " ");
        }
        return s.toString();
    }

    public DoubleProperty timeProperty() {
        return timeRemaining;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.timing;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * FXML Controller class. Controls "Clock.fxml". Basically a low-overhead
 * digitized field timer. This clock has a granularity of at maximum 0.1
 * seconds, so any more specific times will not be observed.
 *
 * @author Prem Chintalapudi 5776E
 */
public class Clock {

    @FXML
    private AnchorPane root;

    private Digit min;
    private Digit secD;
    private Digit sec;
    private Digit secd;

    @FXML
    private AnchorPane minPane;
    @FXML
    private AnchorPane secDPane;
    @FXML
    private AnchorPane secPane;
    @FXML
    private AnchorPane secdPane;

    @FXML
    private VBox colon;

    @FXML
    private Circle period;

    private final DoubleProperty time = new SimpleDoubleProperty();

    @FXML
    private void initialize() {
        root.setUserData(this);
        min = (Digit) minPane.getUserData();
        secD = (Digit) secDPane.getUserData();
        sec = (Digit) secPane.getUserData();
        secd = (Digit) secdPane.getUserData();
        time.addListener(this::display);
        time.set(0);
    }

    private void display(ObservableValue<? extends Number> obs, Number old, Number next) {
        int minutes = next.intValue() / 60;
        float seconds = next.floatValue() % 60;
        int decaSeconds = (int) (seconds) / 10;
        float singles = seconds % 10;
        int secs = (int) singles;
        float dsecs = singles % 1;
        int deciSeconds = (int) (dsecs * 10);
        min.display(minutes);
        secD.display(decaSeconds);
        sec.display(secs);
        secd.display(deciSeconds);
    }

    /**
     * Gets the property that this clock observes to display the time. This
     * property is measured in seconds.
     *
     * @return the property observed by this clock
     */
    public DoubleProperty getTime() {
        return time;
    }
}

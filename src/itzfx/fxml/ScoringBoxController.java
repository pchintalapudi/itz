/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import itzfx.fxml.timing.Clock;
import itzfx.scoring.ScoreAggregator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author Prem Chintalapudi 5776E
 */
public class ScoringBoxController {

    @FXML
    private SplitPane root;

    @FXML
    private Text redScore;

    @FXML
    private Text blueScore;

    @FXML
    private AnchorPane timerPane;

    private Clock clock;

    private final IntegerProperty rScore;
    private final IntegerProperty bScore;

    private ScoreAggregator sa;

    /**
     *
     */
    public ScoringBoxController() {
        rScore = new SimpleIntegerProperty();
        bScore = new SimpleIntegerProperty();
    }

    /**
     *
     */
    public void initialize() {
        redScore.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(rScore.get()), rScore));
        blueScore.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(bScore.get()), bScore));
        root.getParent().setUserData(this);
        clock = (Clock) timerPane.getUserData();
    }

    /**
     *
     * @return
     */
    public DoubleProperty getTime() {
        return clock.getTime();
    }

    /**
     *
     * @param sa
     */
    public void setAggregator(ScoreAggregator sa) {
        this.sa = sa;
    }

    /**
     *
     * @return
     */
    public ScoreAggregator getAggregator() {
        return sa;
    }

    /**
     *
     */
    public void pulseAuton() {
        int[] temp = sa.calculateAuton();
        rScore.set(temp[0]);
        bScore.set(temp[1]);
    }
    
    /**
     *
     */
    public void determineAutonWinner() {
        sa.determineAutonWinner();
    }

    /**
     *
     */
    public void pulseMatch() {
        int[] temp = sa.calculateMatch();
        rScore.set(temp[0]);
        bScore.set(temp[1]);
    }

    /**
     *
     */
    public void generateReport() {
        sa.showReport();
    }
}

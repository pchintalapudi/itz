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
 * FXML Controller class. Controls the "ScoringBox.fxml" file. Maintains live
 * match scoring and autonomous scoring, when pulsed. Uses an internal
 * {@link ScoreAggregator score aggregator} to check scores.
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
     * Creates a new ScoringBoxController. This is generally called by
     * {@link FXMLLoader}.
     */
    public ScoringBoxController() {
        rScore = new SimpleIntegerProperty();
        bScore = new SimpleIntegerProperty();
    }

    @FXML
    private void initialize() {
        redScore.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(rScore.get()), rScore));
        blueScore.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(bScore.get()), bScore));
        root.getParent().setUserData(this);
        clock = (Clock) timerPane.getUserData();
    }

    /**
     * Gets the {@link DoubleProperty time property} on which the displayed
     * {@link Clock clock} is based.
     *
     * @return the time property that is monitored by the clock
     */
    public DoubleProperty getTime() {
        return clock.getTime();
    }

    /**
     * Sets the aggregator that determines scores on demand.
     *
     * @param sa the aggregator to set
     */
    public void setAggregator(ScoreAggregator sa) {
        this.sa = sa;
    }

    /**
     * Gets the scoring aggregator
     *
     * @return the aggregator relied upon by this controller
     */
    public ScoreAggregator getAggregator() {
        return sa;
    }

    /**
     * Updates the displayed scores using a calculation that generates scores
     * during the autonomous period.
     */
    public void pulseAuton() {
        int[] temp = sa.calculateAuton();
        rScore.set(temp[0]);
        bScore.set(temp[1]);
    }

    /**
     * Selects the autonomous winner based on the higher number of autonomous
     * points. Automatically adds 10 points to the alliance's score.
     */
    public void determineAutonWinner() {
        sa.determineAutonWinner();
    }

    /**
     * Updates the displayed scores using a calculation that generates scores
     * during the driver control period.
     */
    public void pulseMatch() {
        int[] temp = sa.calculateMatch();
        rScore.set(temp[0]);
        bScore.set(temp[1]);
    }

    /**
     * Displays a score sheet, formatted like an actual referee's score sheet.
     */
    public void generateReport() {
        sa.showReport();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import itzfx.scoring.ScoreAggregator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class ScoringBoxController {

    @FXML
    private SplitPane root;

    @FXML
    private AnchorPane back1;

    @FXML
    private AnchorPane back2;

    @FXML
    private AnchorPane rest1;

    @FXML
    private AnchorPane rest2;

    @FXML
    private Text redScore;

    @FXML
    private Text blueScore;

    private Timer timer;
    
    private final IntegerProperty rScore;
    private final IntegerProperty bScore;

    private ScoreAggregator sa;

    public ScoringBoxController() {
        rScore = new SimpleIntegerProperty();
        bScore = new SimpleIntegerProperty();
    }

    public void initialize() {
        redScore.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(rScore.get()), rScore));
        blueScore.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(bScore.get()), bScore));
        root.getParent().setUserData(this);
        rest1.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        rest2.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        back1.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        back2.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        timer = (Timer) timerPane.getUserData();
    }

    public void setAggregator(ScoreAggregator sa) {
        this.sa = sa;
    }

    public ScoreAggregator getAggregator() {
        return sa;
    }

    public void pulse(boolean match) {
        if (match) {
            int[] temp = sa.calculateMatch();
            rScore.set(temp[0]);
            bScore.set(temp[1]);
        } else {
            sa.calculateSkills();
        }
    }
    
    public void generateReport() {
        sa.showReport();
    }
    
    public DoubleProperty getInjection() {
        return timer.timeProperty();
    }
    
    @FXML
    private AnchorPane timerPane;
}

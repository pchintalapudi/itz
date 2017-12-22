/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.build;

import itzfx.Robot;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class RobotBuilder {

    @FXML
    private TabPane root;
    @FXML
    private Pane rcPane;
    @FXML
    private Pane mcPane;
    @FXML
    private Pane scPane;

    private RobotConfig rc;
    private MogoConfig mc;
    private StackConfig sc;

    public void initialize() {
        rc = (RobotConfig) rcPane.getUserData();
        mc = (MogoConfig) mcPane.getUserData();
        sc = (StackConfig) scPane.getUserData();
        root.setUserData(this);
    }

    private Double speed;
    private Double autostack;
    private Double statstack;
    private Double mogotime;
    private Integer maxmogo;
    private Integer maxstat;
    private Boolean front;

    private boolean submitted;

    public boolean isSubmitted() {
        return submitted;
    }

    public void submit() {
        speed = rc.getSpeed();
        autostack = rc.getAutostackTime();
        statstack = rc.getStatTime();
        mogotime = mc.getMogoTime();
        front = mc.isFrontMogo();
        maxmogo = sc.getMogoStack();
        maxstat = sc.getStatStack();
        submitted = true;
    }

    public void fillRobot(Robot r) {
        r.acceptValues(speed, mogotime, autostack, statstack, maxmogo, maxstat, front);
    }
}

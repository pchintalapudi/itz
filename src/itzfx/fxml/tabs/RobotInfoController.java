/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.tabs;

import itzfx.Robot;
import java.util.Collection;
import java.util.Iterator;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class RobotInfoController {
    
    @FXML
    private AnchorPane controller1;
    @FXML
    private AnchorPane controller2;
    @FXML
    private AnchorPane controller3;
    @FXML
    private AnchorPane controller4;
    @FXML
    private ControlLayoutController controller1Controller;
    @FXML
    private ControlLayoutController controller2Controller;
    @FXML
    private ControlLayoutController controller3Controller;
    @FXML
    private ControlLayoutController controller4Controller;
    
    public void injectRobots(Collection<? extends Robot> c) {
        Iterator<? extends Robot> i = c.iterator();
        controller1Controller.insertRobot(i.next());
        controller2Controller.insertRobot(i.next());
        controller3Controller.insertRobot(i.next());
        controller4Controller.insertRobot(i.next());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml.tabs;

import itzfx.Robot;
import java.util.Collection;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class RobotInfoController {

    @FXML
    private AnchorPane root;
    @FXML
    private TableView<Robot> table;
    @FXML
    private TableColumn<Robot, String> col0;
    @FXML
    private TableColumn<Robot, KeyCode> col1;
    @FXML
    private TableColumn<Robot, KeyCode> col2;
    @FXML
    private TableColumn<Robot, KeyCode> col3;
    @FXML
    private TableColumn<Robot, KeyCode> col4;
    @FXML
    private TableColumn<Robot, KeyCode> col5;
    @FXML
    private TableColumn<Robot, KeyCode> col6;
    @FXML
    private TableColumn<Robot, KeyCode> col7;
    @FXML
    private TableColumn<Robot, KeyCode> col8;
    @FXML
    private TableColumn<Robot, KeyCode> col9;

    @FXML
    private void initialize() {
        root.setUserData(this);
        setDataFactories();
    }
    
    public void injectRobots(Collection<? extends Robot> c) {
        table.getItems().addAll(c);
    }
    
    private int robotCount = 0;
    
    private void setDataFactories() {
        col0.setCellValueFactory(cdf -> new ReadOnlyStringWrapper("Robot " + robotCount++));
        col1.setCellValueFactory(cdf -> cdf.getValue().forwardKeyProperty());
        col2.setCellValueFactory(cdf -> cdf.getValue().backKeyProperty());
        col3.setCellValueFactory(cdf -> cdf.getValue().leftKeyProperty());
        col4.setCellValueFactory(cdf -> cdf.getValue().rightKeyProperty());
        col5.setCellValueFactory(cdf -> cdf.getValue().mobileGoalKeyProperty());
        col6.setCellValueFactory(cdf -> cdf.getValue().coneKeyProperty());
        col7.setCellValueFactory(cdf -> cdf.getValue().statKeyProperty());
        col8.setCellValueFactory(cdf -> cdf.getValue().autostackKeyProperty());
        col9.setCellValueFactory(cdf -> cdf.getValue().loadKeyProperty());
    }
}

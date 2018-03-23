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
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class RobotDataController {

    @FXML
    private AnchorPane root;
    @FXML
    private TableView<Robot> table;
    @FXML
    private TableColumn<Robot, String> col0;
    @FXML
    private TableColumn<Robot, Number> col1;
    @FXML
    private TableColumn<Robot, Number> col2;
    @FXML
    private TableColumn<Robot, Number> col3;
    @FXML
    private TableColumn<Robot, Number> col4;
    @FXML
    private TableColumn<Robot, Number> col5;
    @FXML
    private TableColumn<Robot, Number> col6;
    @FXML
    private TableColumn<Robot, Boolean> col7;
    
    @FXML
    private void initialize() {
        root.setUserData(this);
        setDataFactories();
    }
    
    private int count = 0;
    
    private void setDataFactories() {
        col0.setCellValueFactory(cdf -> new ReadOnlyStringWrapper("Robot " + count++));
        col1.setCellValueFactory(cdf -> cdf.getValue().speedProperty());
        col2.setCellValueFactory(cdf -> cdf.getValue().mogoIntakeTimeProperty());
        col3.setCellValueFactory(cdf -> cdf.getValue().autostackTimeProperty());
        col4.setCellValueFactory(cdf -> cdf.getValue().statStackTimeProperty());
        col5.setCellValueFactory(cdf -> cdf.getValue().maxMogoStackProperty());
        col6.setCellValueFactory(cdf -> cdf.getValue().maxStatStackProperty());
        col7.setCellValueFactory(cdf -> cdf.getValue().mogoFrontProperty());
    }
    
    public void injectRobots(Collection<? extends Robot> c) {
        table.getItems().addAll(c);
    }
}

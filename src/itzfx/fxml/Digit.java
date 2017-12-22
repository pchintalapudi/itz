/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.fxml;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class Digit {

    @FXML
    private AnchorPane root;

    @FXML
    private Polygon top;
    @FXML
    private Polygon leftUp;
    @FXML
    private Polygon middle;
    @FXML
    private Polygon rightDown;
    @FXML
    private Polygon bottom;
    @FXML
    private Polygon leftDown;
    @FXML
    private Polygon rightUp;

    @FXML
    private void initialize() {
        root.setUserData(this);
    }

    private void manipulate(boolean top, boolean leftUp, boolean middle, boolean rightDown, boolean bottom, boolean leftDown, boolean rightUp) {
        Platform.runLater(() -> {
            this.top.setFill(top ? Color.BLACK : null);
            this.leftUp.setFill(leftUp ? Color.BLACK : null);
            this.middle.setFill(middle ? Color.BLACK : null);
            this.rightDown.setFill(rightDown ? Color.BLACK : null);
            this.bottom.setFill(bottom ? Color.BLACK : null);
            this.leftDown.setFill(leftDown ? Color.BLACK : null);
            this.rightUp.setFill(rightUp ? Color.BLACK : null);
        });
    }

    public void display(int digit) {
        assert digit > -1 && digit < 10;
        switch (digit) {
            case 1:
                manipulate(false, false, false, true, false, false, true);
                break;
            case 2:
                manipulate(true, false, true, false, true, true, true);
                break;
            case 3:
                manipulate(true, false, true, true, true, false, true);
                break;
            case 4:
                manipulate(false, true, true, true, false, false, true);
                break;
            case 5:
                manipulate(true, true, true, true, true, false, false);
                break;
            case 6:
                manipulate(true, true, true, true, true, true, false);
                break;
            case 7:
                manipulate(true, false, false, true, false, false, true);
                break;
            case 8:
                manipulate(true, true, true, true, true, true, true);
                break;
            case 9:
                manipulate(true, true, true, true, true, false, true);
                break;
            case 0:
                manipulate(true, true, false, true, true, true, true);
        }
    }
}